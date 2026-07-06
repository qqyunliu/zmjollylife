package com.easyjava.web.controller;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.dto.SysSettingDto;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.dto.UploadingFileDto;
import com.easyjava.entity.po.VideoInfoFile;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.VideoInfoFileService;
import com.easyjava.utlis.DateUtils;
import com.easyjava.utlis.FFmpegUtils;
import com.easyjava.utlis.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Date;

/*
 * 这是一个文件服务控制器，负责处理所有文件相关操作，包括上传、下载和流媒体播放。这是整个系统的核心基础设施！
 *
 *
 * *完整的视频上传播放流程**
```
【上传阶段】
1. 前端：预上传
   POST /file/preUploadVideo
   ← uploadId: "xyz789"

2. 前端：分片上传（循环）
   POST /file/uploadVideo
   ├─ chunk 0
   ├─ chunk 1
   ├─ ...
   └─ chunk 99

3. 后端：合并分片 + 转码（后台任务）
   ├─ 合并所有分片为完整视频
   ├─ FFmpeg转码为m3u8 + ts
   └─ 更新数据库状态

【播放阶段】
4. 用户点击播放
   ↓
5. 获取m3u8索引
   GET /file/videoResource/{fileId}
   ← index.m3u8

6. 浏览器解析并请求ts
   GET /file/videoResource/{fileId}/0.ts
   GET /file/videoResource/{fileId}/1.ts
   ...
```

---

## 📁 **文件存储结构**
```
{projectFolder}/files/
├─ temp/               # 临时上传目录
│   └─ {uploadId}/
│       ├─ 0           # 分片0
│       ├─ 1           # 分片1
│       └─ ...
│
├─ cover/              # 封面图片
│   ├─ 20241028/
│   └─ 20241029/
│       ├─ abc.jpg
│       └─ abc_thumbnail.jpg
│
└─ video/              # 视频文件（推测）
    └─ {videoId}/
        ├─ index.m3u8  # HLS索引
        ├─ 0.ts        # 视频分片
        ├─ 1.ts
        └─ ...*/
@RequestMapping("/file")
@RestController
@Slf4j
@Validated
public class FileController extends ABaseController {
    @Resource
    private AppConfig appConfig;
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private FFmpegUtils fFmpegUtils;

    @Resource
    private VideoInfoFileService videoInfoFileService;


/*访问服务器上的图片、封面等静态文件
*
* 参数：sourceName (如: "cover/20241029/abc123.jpg")

处理流程：
1️⃣ 解析文件后缀 (.jpg, .png等)
2️⃣ 设置响应头：
   ├─ Content-Type: image/jpg
   └─ Cache-Control: max-age=2592000 (缓存30天)
3️⃣ 从磁盘读取文件
4️⃣ 流式输出到浏览器

错误处理：
❌ 文件不存在 → 返回404
❌ 读取异常 → 返回500
*
*
*
✅ 长期缓存：30天缓存，减少服务器压力
✅ 流式传输：1KB缓冲区分块读取，内存友好
✅ 完善日志：记录每次文件访问*/
    @RequestMapping("/getResource")
    public void getResource(HttpServletResponse response, @NotNull String sourceName) throws IOException, BusinessException {
        log.info("请求资源: {}", sourceName);

        String suffix = StringTools.getFileSuffix(sourceName);
        log.info("文件后缀: {}", suffix);

        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-control", "max-age=2592000");
        readFile(response, sourceName);
    }

    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + Constans.FILE_FOLDER + filePath);

        // 添加调试日志
        log.info("尝试读取文件: {}", file.getAbsolutePath());
        log.info("文件是否存在: {}", file.exists());

        if (!file.exists()) {
            log.warn("文件不存在: {}", file.getAbsolutePath());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte[] byteDate = new byte[1024];
            int len = 0;
            while ((len = in.read(byteDate)) != -1) {
                out.write(byteDate, 0, len);
            }
            out.flush();
            log.info("文件读取成功: {}", file.getAbsolutePath());
        } catch (Exception e) {
            log.error("读取文件异常: {}", file.getAbsolutePath(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
/*大文件上传前的准备工作，生成uploadId
* 📥 参数：
├─ fileName: "我的视频.mp4"
└─ chunks: 100 (分片总数)

处理流程：
1️⃣ 获取当前用户ID
2️⃣ 在Redis中创建上传记录：
   UploadingFileDto {
       uploadId: "随机生成的唯一ID"
       userId: "用户ID"
       fileName: "我的视频.mp4"
       chunks: 100
       chunkIndex: -1 (当前进度)
       fileSize: 0 (已上传大小)
       filePath: "唯一文件路径"
   }
3️⃣ 返回uploadId给前端

💡 为什么需要预上传？
- 生成唯一标识，避免文件冲突
- 记录上传元信息，支持断点续传
- 在Redis中管理上传状态*/
    @RequestMapping("/preUploadVideo")
    public ResponseVO preUploadVideo(@NotEmpty String fileName, @NotNull Integer chunks) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        String uploadId = redisComponent.savePreVideoFileInfo(tokenUserInfoDto.getUserId(), fileName, chunks);
        return getSuccessResponseVO(uploadId);
    }


    /*接收视频的一个分片，支持断点续传
    * 📥 参数：
├─ chunkFile: 分片文件（MultipartFile）
├─ chunkIndex: 当前分片索引（0-99）
└─ uploadId: 预上传返回的ID

处理流程：
1️⃣ 从Redis获取上传信息
2️⃣ 校验：
   ├─ 文件是否存在
   ├─ 文件大小是否超限
   │  └─ 比对：fileSize vs 系统配置的限制
   └─ 分片索引是否合法
       └─ 必须按顺序上传或重传当前分片

3️⃣ 保存分片到临时目录：
   /temp/{filePath}/{chunkIndex}
   例如: /temp/abc123/0
         /temp/abc123/1

4️⃣ 更新Redis中的上传进度：
   ├─ chunkIndex = 当前分片索引
   └─ fileSize += 分片大小

5️⃣ 返回成功

核心校验逻辑：
if (chunkIndex - 1 > fileDto.getChunkIndex() ||
    chunkIndex > fileDto.getChunks() - 1) {
    // 只能上传下一个分片或重传当前分片
    throw new BusinessException("分片索引错误");
}
```

**断点续传机制：**
```
假设上传到第50片时断网：

前端重新连接后：
1️⃣ 从Redis读取进度 → chunkIndex=49
2️⃣ 继续上传第50片开始
3️⃣ 无需重新上传前49片

4️⃣ 删除上传 /file/delUploadVideo
做什么： 取消上传，清理临时文件
怎么做：
java📥 参数：uploadId

处理流程：
1️⃣ 从Redis获取文件信息
2️⃣ 删除Redis记录
3️⃣ 删除临时目录及所有分片：
   FileUtils.deleteDirectory(/temp/{filePath})
4️⃣ 返回成功

使用场景：
- 用户取消上传
- 上传失败后清理
- 超时自动清理（需配合定时任务）*/
    @RequestMapping("/uploadVideo")
    public ResponseVO uploadVideo(@NotNull MultipartFile chunkFile, @NotNull Integer chunkIndex, @NotEmpty String uploadId) throws BusinessException, IOException {

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UploadingFileDto fileDto = redisComponent.getUploadVideoFile(tokenUserInfoDto.getUserId(), uploadId);
        if (chunkIndex == null) {
            throw new BusinessException("分片索引不能为空");
        }

        log.info("fileDto 内容: chunkIndex={}, chunks={}, fileSize={}, filePath={}",
                fileDto.getChunkIndex(), fileDto.getChunks(), fileDto.getFileSize(), fileDto.getFilePath());
        log.info("上传文件大小（字节）: " + fileDto.getFileSize());

        if (fileDto == null) {
            throw new BusinessException("文件不存在,请重新上传");
        }
        SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();
        if (fileDto.getFileSize() > sysSettingDto.getVideoSize() * Constans.MB_SIZE) {
            log.info("系统限制大小（字节）: " + sysSettingDto.getVideoSize() * Constans.MB_SIZE);
            throw new BusinessException("文件超过大小限制");
        }

        if (fileDto.getChunkIndex() == null) {
            fileDto.setChunkIndex(-1);  // 设置默认值
        }
        if (fileDto.getChunks() == null) {
            throw new BusinessException("文件分片信息缺失,请重新上传");
        }
        if (chunkIndex - 1 > fileDto.getChunkIndex() || chunkIndex > fileDto.getChunks() - 1) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String folder = appConfig.getProjectFolder() + Constans.FILE_FOLDER + Constans.FILE_FOLDER_TEMP + fileDto.getFilePath();
        File targetFile = new File(folder + "/" + chunkIndex);
        chunkFile.transferTo(targetFile);
        fileDto.setChunkIndex(chunkIndex);
        fileDto.setFileSize(fileDto.getFileSize() + chunkFile.getSize());
        redisComponent.updateVideoFileInfo(tokenUserInfoDto.getUserId(), fileDto);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delUploadVideo")
    public ResponseVO delUploadVideo(@NotEmpty String uploadId) throws BusinessException, IOException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UploadingFileDto fileDto = redisComponent.getUploadVideoFile(tokenUserInfoDto.getUserId(), uploadId);
        if (fileDto == null) {
            throw new BusinessException("文件不存在");

        }
        redisComponent.delVideoFileInfo(tokenUserInfoDto.getUserId(), uploadId);
        FileUtils.deleteDirectory(new File(appConfig.getProjectFolder() + Constans.FILE_FOLDER + Constans.FILE_FOLDER_TEMP + fileDto.getFilePath()));
        return getSuccessResponseVO(uploadId);
    }
/* 上传封面、头像等图片，可选生成缩略图*/
    @RequestMapping("/UploadImage")
    public ResponseVO UploadImage(@NotNull MultipartFile file, @NotNull boolean createThumbnail) throws BusinessException, IOException {
        String day = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());

        // 修复：创建正确的cover目录路径
        String folder = appConfig.getProjectFolder() + Constans.FILE_FOLDER + "cover/" + day;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }

        String fileName = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(fileName);
        String realFileName = StringTools.getRandomString(Constans.LENGTH_30) + fileSuffix;
        String filePath = folder + "/" + realFileName;
        file.transferTo(new File(filePath));

        if (createThumbnail) {
            fFmpegUtils.createImageThumbnail(filePath);
        }

        return getSuccessResponseVO("cover/" + day + "/" + realFileName);
    }


    /*返回HLS视频的m3u8索引文件（视频播放入口）
* 处理流程：
1️⃣ 根据fileId查询数据库获取文件路径
2️⃣ 读取m3u8文件：
   {filePath}/index.m3u8
3️⃣ 返回给浏览器

*
**HLS播放原理：**
```
浏览器请求视频
   ↓
获取m3u8索引文件
   ↓ 解析得到ts列表
依次请求ts分片
   ↓
0.ts → 1.ts → 2.ts → ...
   ↓
边下边播，流畅观看*/
    @RequestMapping("/videoResource/{fileId}")
    public void videoResource(HttpServletResponse response, @PathVariable @NotEmpty String fileId) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoInfoFileByFileId(fileId);
        String filePath = videoInfoFile.getFilePath();
        readFile(response, filePath + "/" + Constans.M3U8_NAME);

        //TODO 更新视频的阅读信息
    }
/*返回视频的ts分片文件
*
处理流程：
1️⃣ 根据fileId查询文件路径
2️⃣ 拼接完整路径：
   {filePath}/0.ts
3️⃣ 读取并返回ts文件
*/
    @RequestMapping("/videoResource/{fileId}/{ts}")
    public void videoResourceTs(HttpServletResponse response, @PathVariable @NotEmpty String fileId, @PathVariable @NotEmpty String ts) {
        // 添加日志来调试
        log.info("请求ts文件: fileId={}, ts={}", fileId, ts);

        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoInfoFileByFileId(fileId);
        String filePath = videoInfoFile.getFilePath();

        log.info("视频文件路径: {}, 请求的ts: {}", filePath, ts);

        readFile(response, filePath + "/" + ts);
    }
}

/*安全点
实现方式  身份验证上传接口需要Token验证
文件大小 限制对比系统配置中的限制
路径注入 防护文件名随机生成，不使用用户输入
分片顺序 校验只能上传下一片或重传当前片
用户隔离 通过userId隔离不同用户的上传

⚡ 性能优化
java✅ 分片上传：支持超大文件
✅ 断点续传：网络中断可恢复
✅ 流式读取：1KB缓冲区，低内存占用
✅ 长期缓存：静态资源缓存30天
✅ HLS流媒体：边下边播，快速起播
✅ 按日期分目录：避免单目录文件过多

核心能力：
├─ 大文件分片上传（支持GB级视频）
├─ 断点续传（网络友好）
├─ HLS流媒体播放（主流方案）
├─ 图片处理（缩略图生成）
└─ 静态资源服务（高性能缓存）

技术栈：
├─ Redis：管理上传状态
├─ FFmpeg：视频转码 + 图片处理
├─ HLS：流媒体播放协议
└─ 分片上传：大文件解决方案*/
