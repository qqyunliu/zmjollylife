package com.easyjava.utlis;

import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
/*这是一个FFmpeg视频处理工具类 FFmpegUtils，它封装了常用的视频处理功能*/
@Component
@Slf4j
public class FFmpegUtils {
    @Resource
    private AppConfig appConfig;/*用于获取FFmpeg日志显示配置。*/

    public void createImageThumbnail(String filePath) throws BusinessException {
        /*为图片生成缩略图*/
        String CMD = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        CMD = String.format(CMD, filePath, filePath + Constans.IMAGE_THUMBNAIL_SUFFIX);
        ProcessUtils.executeCommand(CMD, appConfig.isShowFFmpegLog());
    }

    public Integer getVideoInfoDuration(String completeVideo) throws BusinessException {
        /*获取视频的总时长（秒）*/
        final String CMD_GET_CODE = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"%s\"";
        String cmd = String.format(CMD_GET_CODE, completeVideo);
        String result = ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());
        if (StringTools.isEmpty(result)) {
            return 0;
        }
        result = result.replace("\n", "");
        return new BigDecimal(result).intValue();
    }

    public String getVideoCodec(String videoFilePath) throws BusinessException {
        /*检测视频的编码格式*/
        final String CMD_GET_CODE = "ffprobe -v error -select_streams v:0 -show_entries stream=codec_name \"%s\"";
        String cmd = String.format(CMD_GET_CODE, videoFilePath);
        String result = ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());

        result = result.replace("\n", "");
        result = result.substring(result.indexOf("=") + 1);
        String codec = result.substring(0, result.indexOf("["));
        return codec;
    }

    public void convertHevc2Mp4(String newFileName, String videoFilePath) throws BusinessException {
        /*将HEVC(H.265)编码的视频转换为H.264编码的MP4*/
        String CMD_HEVC_264 = "ffmpeg -i \"%s\" -c:v libx264 -crf 20 \"%s\" -y";
        String cmd = String.format(CMD_HEVC_264, newFileName, videoFilePath);
        ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());
    }

    /**
     * 修复后的视频转HLS方法 - 直接一步生成HLS格式
     */
    public void convertVideo2Ts(File tsFolder, String videoFilePath) throws BusinessException {
        /*将视频转换为HLS（HTTP Live Streaming）格式，用于视频流媒体播放*/
        try {
            // 构建输出路径
            String m3u8Path = tsFolder.getPath() + File.separator + Constans.M3U8_NAME;
            String tsPattern = tsFolder.getPath() + File.separator + "%d.ts";

            // 方案1：优先尝试直接复制编码（速度快）
            String CMD_DIRECT_HLS = "ffmpeg -y -i \"%s\" -codec copy -start_number 0 -hls_time 10 -hls_list_size 0 -f hls \"%s\"";
            String cmd = String.format(CMD_DIRECT_HLS, videoFilePath, m3u8Path);

            try {
                ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());

                // 验证生成的文件
                File m3u8File = new File(m3u8Path);
                if (m3u8File.exists() && m3u8File.length() > 0) {
                    // 检查是否生成了ts文件
                    File[] tsFiles = tsFolder.listFiles((dir, name) -> name.endsWith(".ts"));
                    if (tsFiles != null && tsFiles.length > 0) {
                        // 直接复制成功
                        return;
                    }
                }
            } catch (Exception e) {
                // 直接复制失败，继续尝试重新编码
                System.out.println("直接复制编码失败，尝试重新编码: " + e.getMessage());
            }

            // 方案2：重新编码（兼容性好但速度慢）
            String CMD_REENCODE_HLS = "ffmpeg -y -i \"%s\" -c:v libx264 -c:a aac -ac 2 -ar 48000 -profile:v baseline -level 3.0 -start_number 0 -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s\" -f hls \"%s\"";
            cmd = String.format(CMD_REENCODE_HLS, videoFilePath, tsPattern, m3u8Path);

            ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());

            // 验证最终结果
            File m3u8File = new File(m3u8Path);
            if (!m3u8File.exists() || m3u8File.length() == 0) {
                throw new BusinessException("HLS转换失败：m3u8文件未生成或为空");
            }

            // 检查ts文件
            File[] tsFiles = tsFolder.listFiles((dir, name) -> name.endsWith(".ts"));
            if (tsFiles == null || tsFiles.length == 0) {
                throw new BusinessException("HLS转换失败：ts文件未生成");
            }

            System.out.println("HLS转换成功，生成 " + tsFiles.length + " 个ts文件");

        } catch (Exception e) {
            throw new BusinessException("视频转HLS失败: " + e.getMessage());
        }
    }

    /**
     * 备用的简化转换方法
     */
    public void convertVideo2TsSimple(File tsFolder, String videoFilePath) throws BusinessException {
        // 最简单的HLS转换命令
        String m3u8Path = tsFolder.getPath() + File.separator + Constans.M3U8_NAME;
        String CMD_SIMPLE_HLS = "ffmpeg -y -i \"%s\" -hls_time 10 -hls_list_size 0 -f hls \"%s\"";
        String cmd = String.format(CMD_SIMPLE_HLS, videoFilePath, m3u8Path);

        ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());

        // 验证结果
        File m3u8File = new File(m3u8Path);
        if (!m3u8File.exists()) {
            throw new BusinessException("简化HLS转换失败：m3u8文件未生成");
        }
    }

    public List<String> extractFramesEveryNSeconds(String videoFilePath, String outputDir, int intervalSeconds, int maxFrames) throws BusinessException {
        List<String> framePaths = new ArrayList<>();
        File videoFile = new File(videoFilePath);
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        if (!waitForFileReady(videoFile)) {
            log.error("视频文件不可用或正在被占用: {}", videoFilePath);
            return framePaths;
        }

        String CMD_EXTRACT_FRAME = "ffmpeg -i \"%s\" -vf fps=1/%d \"%s/frame_%%04d.jpg\" -y";
        String cmd = String.format(CMD_EXTRACT_FRAME, videoFilePath, intervalSeconds, outputDir);

        try {
            ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());

            File[] frames = outputFolder.listFiles((dir, name) -> name.startsWith("frame_") && name.endsWith(".jpg"));
            if (frames != null) {
                Arrays.sort(frames, Comparator.comparing(File::getName));
                int count = 0;
                for (File frame : frames) {
                    if (count >= maxFrames) {
                        frame.delete();
                        break;
                    }
                    framePaths.add(frame.getAbsolutePath());
                    count++;
                }
            }
        } catch (Exception e) {
            log.error("抽帧失败", e);
        }

        return framePaths;
    }

    public String extractAudio(String videoFilePath, String outputAudioPath) throws BusinessException {
        File videoFile = new File(videoFilePath);
        if (!waitForFileReady(videoFile)) {
            log.error("视频文件不可用或正在被占用，无法提取音频: {}", videoFilePath);
            return null;
        }

        File outputFile = new File(outputAudioPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        String CMD_EXTRACT_AUDIO = "ffmpeg -i \"%s\" -vn -acodec libmp3lame -q:a 2 \"%s\" -y";
        String cmd = String.format(CMD_EXTRACT_AUDIO, videoFilePath, outputAudioPath);

        try {
            ProcessUtils.executeCommand(cmd, appConfig.isShowFFmpegLog());
            if (outputFile.exists() && outputFile.length() > 0) {
                log.info("音频提取成功: {}", outputAudioPath);
                return outputAudioPath;
            } else {
                log.error("音频提取失败，文件不存在或为空: {}", outputAudioPath);
                return null;
            }
        } catch (Exception e) {
            log.error("提取音频失败", e);
            return null;
        }
    }

    private boolean waitForFileReady(File file) {
        if (!file.exists()) {
            log.error("视频文件不存在: {}", file.getAbsolutePath());
            return false;
        }
        if (file.length() == 0) {
            log.error("视频文件大小为0: {}", file.getAbsolutePath());
            return false;
        }
        int waitCount = 0;
        while (waitCount < 30) {
            if (file.canRead()) {
                return true;
            }
            try {
                Thread.sleep(1000);
                waitCount++;
                log.info("等待视频文件释放锁，当前重试次数: {}", waitCount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        log.error("视频文件在30秒内无法读取，可能被其他进程占用: {}", file.getAbsolutePath());
        return false;
    }
}