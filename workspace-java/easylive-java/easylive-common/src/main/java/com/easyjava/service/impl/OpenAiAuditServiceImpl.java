package com.easyjava.service.impl;

import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.dto.AiAuditResult;
import com.easyjava.entity.po.VideoInfoPost;
import com.easyjava.service.AiAuditService;
import com.easyjava.utlis.FFmpegUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class OpenAiAuditServiceImpl implements AiAuditService {

    private static final int FRAME_INTERVAL = 5;
    private static final int MAX_FRAMES = 10;

    // ==========================================
    // 火山引擎配置 - 从 application.yml / 环境变量读取
    // ==========================================
    @Value("${volcengine.api-url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;

    @Value("${volcengine.api-key:}")
    private String apiKey;

    @Value("${volcengine.image-model-id:doubao-seed-2-0-mini-260215}")
    private String imageModelId;

    @Value("${volcengine.audio-model-id:deepseek-v3-2-251201}")
    private String audioModelId;
    // ==========================================

    // ==========================================
    // 语音转文字 - Faster Whisper 本地配置
    // ==========================================
    private static final String WHISPER_MODEL = "tiny";
    private static final String WHISPER_LANGUAGE = "zh";
    private static final String WHISPER_MODEL_PATH = null;
    // ==========================================

    @Resource
    private FFmpegUtils fFmpegUtils;

    @Resource
    private AppConfig appConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AiAuditResult auditVideo(VideoInfoPost video) {
        return null;
    }

    @Override
    public AiAuditResult auditVideoFrames(String videoId, String videoFilePath) {
        try {
            String frameDir = appConfig.getProjectFolder() + Constans.FILE_FOLDER + "audit_frames/" + videoId;
            List<String> framePaths = fFmpegUtils.extractFramesEveryNSeconds(
                    videoFilePath, frameDir, FRAME_INTERVAL, MAX_FRAMES);

            if (framePaths == null || framePaths.isEmpty()) {
                log.warn("未提取到视频帧，videoId: {}", videoId);
                return createDefaultResult(false, "未提取到视频帧");
            }

            log.info("成功提取 {} 帧，开始大模型审核，videoId: {}", framePaths.size(), videoId);

            for (String framePath : framePaths) {
                try {
                    AiAuditResult frameResult = auditFrame(framePath);
                    if (!frameResult.getPassed()) {
                        cleanFrames(frameDir);
                        log.info("视频审核未通过，命中违规帧: {}", framePath);
                        return frameResult; // 一旦发现违规，立即阻断
                    }
                } catch (Exception e) {
                    log.error("帧审核异常: {}", framePath, e);
                    cleanFrames(frameDir);
                    return createDefaultResult(false, "图片审核异常");
                }
            }

            cleanFrames(frameDir);
            return createDefaultResult(true, "审核通过");

        } catch (Exception e) {
            log.error("视频帧审核异常，videoId: {}", videoId, e);
            return createDefaultResult(false, "图片审核异常");
        }
    }

    private AiAuditResult auditFrame(String framePath) {
        log.info("调用火山引擎 API 审核帧: {}", framePath);
        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return createDefaultResult(false, "图片审核密钥未配置");
            }
            File file = new File(framePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(fileContent);

            // 1. 设置 HTTP 头，包含 Bearer Token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey); 

            // 2. 构建严谨的提示词
            String promptText = "请作为内容审核员，检查这张图片是否属于明显且严重的违规内容（如真实色情行为、极端血腥暴力、违禁品展示等），对于衣着时尚或适度暴露（如泳装、运动装、内衣展示等，只要不涉及明显性行为或生殖器特写）、艺术化或非真实的血腥元素（如游戏、影视、动漫中的战斗画面）、日常场景中可能出现的物品（如刀具用于烹饪、健身器材、普通药品等）、以及正常社交或生活记录（如伤口愈合、健身锻炼、街头艺术等），请判定为合规；请优先保护用户正常表达，仅在内容明显违反主流社区规范（类似抖音/小红书标准）时标记违规。" +
                                "如果完全安全，请仅回复 'PASS'。" +
                                "如果违规，请回复 'FAIL: [具体违规原因]'。";
            
            Map<String, Object> requestBody = buildVolcenginePayload(promptText, base64Image);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 3. 发起同步请求
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            // 4. 解析结果
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseVolcengineResponse(response.getBody());
            }
            log.error("API 调用失败，状态码: {}", response.getStatusCode());
            return createDefaultResult(false, "图片审核API调用失败");

        } catch (Exception e) {
            log.error("调用大模型异常", e);
            return createDefaultResult(false, "图片审核异常");
        }
    }

    private Map<String, Object> buildVolcenginePayload(String prompt, String base64Image) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", imageModelId); 

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user"); 

        List<Map<String, Object>> contentList = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        contentList.add(textContent);

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, Object> imageUrlMap = new HashMap<>();
        imageUrlMap.put("url", base64Image); 
        imageContent.put("image_url", imageUrlMap);
        contentList.add(imageContent);

        userMessage.put("content", contentList);
        messages.add(userMessage);

        payload.put("messages", messages);
        payload.put("stream", false); 
        payload.put("temperature", 0.1); // 降低随机性，提升审核准确度

        return payload;
    }

    private AiAuditResult parseVolcengineResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String modelOutput = rootNode.path("choices")
                    .get(0).path("message").path("content").asText().trim();

            log.info("模型审核返回结果: {}", modelOutput);

            if (modelOutput.toUpperCase().startsWith("FAIL")) {
                String reason = modelOutput.length() > 5 ? modelOutput.substring(5).trim() : "包含违规内容";
                return createDefaultResult(false, reason);
            }
            
            return createDefaultResult(true, null);

        } catch (Exception e) {
            log.error("解析模型响应失败: {}", responseBody, e);
            return createDefaultResult(false, "解析响应异常");
        }
    }

    @Override
    public AiAuditResult auditAudio(String videoId, String audioText) {
        log.info("开始审核音频文本，videoId: {}, 文本长度: {}", videoId, audioText != null ? audioText.length() : 0);
        try {
            if (audioText == null || audioText.trim().isEmpty()) {
                log.info("音频文本为空，视为审核通过，videoId: {}", videoId);
                return createDefaultResult(true, "音频文本为空");
            }
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return createDefaultResult(false, "文本审核密钥未配置");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String promptText = "你现在是短视频内容安全平台的核心音频转文本(ASR)审核引擎。你的唯一任务是精准拦截极端违规内容，同时对口语化、语音识别错误、游戏解说或日常夸张表达保持绝对的容忍，遵循“无罪推定”原则，绝不盲目联想或推理潜在动机。仅当文本在字面逻辑上无歧义地构成以下三种严重违规时才允许拦截：1）直接且露骨地描写真实的性交动作或具体性器官；2）明确提供制造爆炸物、毒品等违禁品的物理配方或实操步骤；3）直接使用极端侮辱性词汇谩骂，或明确煽动对特定种族、宗教、地域群体的现实暴力攻击。特别注意“谐音违规”的校验逻辑：只有当谐音词与上下文组合后清晰连贯地表达了上述三种极端违规的完整语义时，才判定违规。除此之外，所有情况一律强制放行，包括但不限于：逻辑不通的ASR识别乱码或错别字；包含“杀”、“死”、“血”、“毒”等词汇的游戏解说、文娱场景、歌词或剧本；“笑死”、“打死你”、“卧槽”等带有情绪但无现实伤害威胁的日常夸张表达与口头禅；以及任何不涉及真实血腥的恐怖悬疑题材。任何让你觉得“疑似”、“可能”、“暗示”、“打擦边球”或无法完全确定的内容，都必须强制判定为安全。" +
                               "如果完全安全，请仅回复 'PASS'。" +
                               "如果违规，请回复 'FAIL: [具体违规原因]'。" +
                               "请审核的文本内容：\n" + audioText;

            Map<String, Object> requestBody = buildAudioAuditPayload(promptText);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseAudioAuditResponse(response.getBody());
            }
            log.error("音频审核 API 调用失败，状态码: {}", response.getStatusCode());
            return createDefaultResult(false, "音频审核API调用失败");

        } catch (Exception e) {
            log.error("音频审核异常，videoId: {}", videoId, e);
            return createDefaultResult(false, "音频审核异常");
        }
    }

    @Override
    public AiAuditResult auditText(String bizId, String text) {
        log.info("开始审核文本，bizId: {}, 文本长度: {}", bizId, text != null ? text.length() : 0);
        try {
            if (text == null || text.trim().isEmpty()) {
                return createDefaultResult(true, "文本为空");
            }
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return createDefaultResult(false, "文本审核密钥未配置");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String promptText = "作为严格的内容审核员，请检查以下文本内容是否包含违规内容（如色情、血腥、暴力、违禁品、敏感话题等）。" +
                    "如果完全安全，请仅回复 'PASS'。" +
                    "如果违规，请回复 'FAIL: [具体违规原因]'。" +
                    "请审核的文本内容：\n" + text;

            Map<String, Object> requestBody = buildAudioAuditPayload(promptText);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseAudioAuditResponse(response.getBody());
            }
            log.error("文本审核 API 调用失败，状态码: {}", response.getStatusCode());
            return createDefaultResult(false, "文本审核API调用失败");
        } catch (Exception e) {
            log.error("文本审核异常，bizId: {}", bizId, e);
            return createDefaultResult(false, "文本审核异常");
        }
    }

    private Map<String, Object> buildAudioAuditPayload(String prompt) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", audioModelId);

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        payload.put("messages", messages);
        payload.put("stream", false);
        payload.put("temperature", 0.1);

        return payload;
    }

    private AiAuditResult parseAudioAuditResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String modelOutput = rootNode.path("choices")
                    .get(0).path("message").path("content").asText().trim();

            log.info("文本审核模型返回结果: {}", modelOutput);

            if (modelOutput.toUpperCase().startsWith("FAIL")) {
                String reason = modelOutput.length() > 5 ? modelOutput.substring(5).trim() : "包含违规内容";
                return createDefaultResult(false, reason);
            }

            return createDefaultResult(true, null);

        } catch (Exception e) {
            log.error("解析音频审核模型响应失败: {}", responseBody, e);
            return createDefaultResult(false, "解析响应异常");
        }
    }

    @Override
    public String speechToText(String audioFilePath) {
        log.info("开始使用Faster-Whisper进行语音转文字，audioFilePath: {}", audioFilePath);
        try {
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists() || audioFile.length() == 0) {
                log.error("音频文件不存在或为空: {}", audioFilePath);
                return null;
            }

            String escapedAudioPath = audioFilePath.replace("\\", "\\\\").replace("'", "\\'");

            String pythonScript;
            if (WHISPER_MODEL_PATH != null && !WHISPER_MODEL_PATH.isEmpty()) {
                String escapedModelPath = WHISPER_MODEL_PATH.replace("\\", "\\\\").replace("'", "\\'");
                pythonScript = String.format(
                    "import sys; import io; sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8'); " +
                    "from faster_whisper import WhisperModel; " +
                    "model = WhisperModel('%s', device='cpu', compute_type='int8'); " +
                    "segments, _ = model.transcribe('%s', language='%s'); " +
                    "result = ' '.join([s.text for s in segments]); " +
                    "print(result)",
                    escapedModelPath, escapedAudioPath, WHISPER_LANGUAGE
                );
            } else {
                pythonScript = String.format(
                    "import sys; import io; sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8'); " +
                    "from faster_whisper import WhisperModel; " +
                    "model = WhisperModel('%s', device='cpu', compute_type='int8'); " +
                    "segments, _ = model.transcribe('%s', language='%s'); " +
                    "result = ' '.join([s.text for s in segments]); " +
                    "print(result)",
                    WHISPER_MODEL, escapedAudioPath, WHISPER_LANGUAGE
                );
            }

            ProcessBuilder pb = new ProcessBuilder("python", "-c", pythonScript);
            pb.redirectErrorStream(true);
            Map<String, String> env = pb.environment();
            env.put("HF_ENDPOINT", "https://hf-mirror.com");
            env.put("KMP_DUPLICATE_LIB_OK", "TRUE");
            env.put("PYTHONIOENCODING", "UTF-8");
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            int exitCode = process.waitFor();
            String result = output.toString().trim();

            if (exitCode == 0 && !result.isEmpty()) {
                log.info("Faster-Whisper语音转文字成功，文本长度: {}", result.length());
                String audioDir = new File(audioFilePath).getParent();
                String textFilePath = audioDir + "/audio_text.txt";
                try {
                    java.nio.file.Files.write(
                        java.nio.file.Paths.get(textFilePath),
                        result.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                    );
                    log.info("转写文本已保存到: {}", textFilePath);
                } catch (Exception e) {
                    log.warn("保存转写文本失败: {}", e.getMessage());
                }
                return result;
            } else {
                log.error("Faster-Whisper语音转文字失败，exitCode: {}, output: {}", exitCode, result);
                return null;
            }

        } catch (Exception e) {
            log.error("Faster-Whisper语音转文字异常，audioFilePath: {}", audioFilePath, e);
            return null;
        }
    }

    private void cleanFrames(String frameDir) {
        try {
            File dir = new File(frameDir);
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                dir.delete();
            }
        } catch (Exception e) {
            log.error("清理抽帧目录失败", e);
        }
    }

    private AiAuditResult createDefaultResult(boolean passed, String reason) {
        return new AiAuditResult(passed, reason);
    }
}
