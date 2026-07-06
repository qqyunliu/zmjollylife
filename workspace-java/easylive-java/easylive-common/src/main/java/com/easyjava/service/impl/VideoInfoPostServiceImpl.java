package com.easyjava.service.impl;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.dto.SysSettingDto;
import com.easyjava.entity.dto.UploadingFileDto;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.po.VideoInfoFile;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.query.*;
import com.easyjava.enums.*;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.VideoInfoPost;
import com.easyjava.entity.dto.AiAuditResult;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.VideoInfoFileMapper;
import com.easyjava.mappers.VideoCommentMapper;
import com.easyjava.mappers.VideoDanmuMapper;
import com.easyjava.mappers.UserActionMapper;
import com.easyjava.mappers.PlayHistoryMapper;
import com.easyjava.mappers.UserMessageMapper;
import com.easyjava.mappers.VideoSeriesVideoMapper;
import com.easyjava.service.AiAuditService;
import com.easyjava.mappers.VideoInfoFilePostMapper;
import com.easyjava.mappers.VideoInfoMapper;
import com.easyjava.mappers.VideoInfoPostMapper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.easyjava.utlis.CopyTools;
import com.easyjava.utlis.FFmpegUtils;
import com.easyjava.utlis.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import com.easyjava.service.VideoInfoPostService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * @Description:视频信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */
@Service("videoInfoPostService")
@Slf4j
public class VideoInfoPostServiceImpl implements VideoInfoPostService {

    @Resource
    private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostquery> videoInfoPostMapper;

    @Resource
    private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostquery> videoInfoFilePostMapper;


    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoquery> videoInfoMapper;

    @Resource
    private VideoInfoFileMapper<VideoInfoFile, VideoInfoFilequery> videoInfoFileMapper;
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    @Resource
    private VideoCommentMapper<com.easyjava.entity.po.VideoComment, com.easyjava.entity.query.VideoCommentquery> videoCommentMapper;

    @Resource
    private VideoDanmuMapper<com.easyjava.entity.po.VideoDanmu, com.easyjava.entity.query.VideoDanmUquery> videoDanmuMapper;

    @Resource
    private UserActionMapper<com.easyjava.entity.po.UserAction, Object> userActionMapper;

    @Resource
    private PlayHistoryMapper<com.easyjava.entity.po.PlayHistory, Object> playHistoryMapper;

    @Resource
    private UserMessageMapper<com.easyjava.entity.po.UserMessage, Object> userMessageMapper;

    @Resource
    private VideoSeriesVideoMapper<com.easyjava.entity.po.VideoSeriesVideo, Object> videoSeriesVideoMapper;

    @Resource
    private FFmpegUtils fFmpegUtils;

    @Resource
    private AiAuditService aiAuditService;


    /**
     * 根据条件查询列表
     */
    public List<VideoInfoPost> findListByParam(VideoInfoPostquery query) {
        return this.videoInfoPostMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    public Integer findCountByParam(VideoInfoPostquery query) {
        return this.videoInfoPostMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    public PaginationResultVO<VideoInfoPost> findListByPage(VideoInfoPostquery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<VideoInfoPost> List = this.findListByParam(query);
        PaginationResultVO<VideoInfoPost> result = new PaginationResultVO(count, page.getPageNo(), page.getPageSize(), page.getPageTotal(), List);
        return result;
    }

    /**
     * 新增
     */
    public Integer add(VideoInfoPost bean) {
        return this.videoInfoPostMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    public Integer addBatch(List<VideoInfoPost> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoInfoPostMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    public Integer addOrUpdateBatch(List<VideoInfoPost> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoInfoPostMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据VideoId查询
     */
    public VideoInfoPost getVideoInfoPostByVideoId(String videoId) {
        return this.videoInfoPostMapper.selectByVideoId(videoId);
    }

    /**
     * 根据VideoId更新
     */
    public Integer updateVideoInfoPostByVideoId(VideoInfoPost bean, String videoId) {
        return this.videoInfoPostMapper.updateByVideoId(bean, videoId);
    }

    /**
     * 根据VideoId删除
     */
    public Integer deleteVideoInfoPostByVideoId(String videoId) {
        return this.videoInfoPostMapper.deleteByVideoId(videoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(String videoId, String userId) throws BusinessException {
        if (StringTools.isEmpty(videoId) || StringTools.isEmpty(userId)) {
            throw new BusinessException("参数错误");
        }

        VideoInfoPost videoPost = videoInfoPostMapper.selectByVideoId(videoId);
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoId);

        String ownerUserId = videoPost != null ? videoPost.getUserId() : (videoInfo != null ? videoInfo.getUserId() : null);
        if (StringTools.isEmpty(ownerUserId)) {
            throw new BusinessException("视频不存在");
        }
        if (!ownerUserId.equals(userId)) {
            throw new BusinessException("无权限删除该视频");
        }

        VideoInfoFilePostquery filePostQuery = new VideoInfoFilePostquery();
        filePostQuery.setVideoId(videoId);
        filePostQuery.setUserId(userId);
        List<VideoInfoFilePost> filePostList = videoInfoFilePostMapper.selectList(filePostQuery);
        if (filePostList != null) {
            for (VideoInfoFilePost filePost : filePostList) {
                if (filePost == null || StringTools.isEmpty(filePost.getFilePath())) {
                    continue;
                }
                File folder = new File(appConfig.getProjectFolder() + Constans.FILE_FOLDER + filePost.getFilePath());
                if (folder.exists()) {
                    try {
                        FileUtils.deleteDirectory(folder);
                    } catch (IOException e) {
                        log.error("删除视频文件失败，videoId: {}, path: {}", videoId, filePost.getFilePath(), e);
                    }
                }
            }
        }
        videoInfoFilePostMapper.deleteByVideoIdAndUserId(videoId, userId);

        VideoInfoFilequery videoInfoFileQuery = new VideoInfoFilequery();
        videoInfoFileQuery.setVideoId(videoId);
        videoInfoFileMapper.deleteByParam(videoInfoFileQuery);

        videoSeriesVideoMapper.deleteByVideoId(videoId);
        videoCommentMapper.deleteByVideoId(videoId);
        videoDanmuMapper.deleteByVideoId(videoId);
        userActionMapper.deleteByVideoId(videoId);
        playHistoryMapper.deleteByVideoId(videoId);
        userMessageMapper.deleteByVideoId(videoId);

        videoInfoMapper.deleteByVideoId(videoId);
        videoInfoPostMapper.deleteByVideoId(videoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoInfo(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> uploadFileList) throws BusinessException {
        // 添加调试日志
        log.info("=== 开始保存视频信息 ===");
        log.info("传入的videoInfoPost.getVideoId(): {}", videoInfoPost.getVideoId());
        log.info("uploadFileList size: {}", uploadFileList != null ? uploadFileList.size() : 0);

        if (uploadFileList.size() > redisComponent.getSysSettingDto().getVideoCount()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (!StringTools.isEmpty(videoInfoPost.getVideoId())) {
            VideoInfoPost videoInfoPostDb = this.videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
            if (videoInfoPostDb == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getStatus(), VideoStatusEnum.STATUS2.getStatus()}, videoInfoPostDb.getStatus())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        Date curDate = new Date();
        String videoId = videoInfoPost.getVideoId();
        List<VideoInfoFilePost> deleteFileList = new ArrayList<>();
        List<VideoInfoFilePost> addFileList = uploadFileList;

        if (StringTools.isEmpty(videoId)) {
            // 新增视频情况
            videoId = StringTools.getRandomString(Constans.LENGTH_10);
            log.info("生成新的videoId: {}", videoId);

            videoInfoPost.setVideoId(videoId);
            videoInfoPost.setCreateTime(curDate);
            videoInfoPost.setLastUpdateTime(curDate);
            videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());

            log.info("准备插入视频信息，videoId: {}", videoInfoPost.getVideoId());
            this.videoInfoPostMapper.insert(videoInfoPost);
            log.info("视频信息插入完成");
        } else {
            // 更新视频情况
            log.info("更新视频，videoId: {}", videoId);

            VideoInfoFilePostquery fileQuery = new VideoInfoFilePostquery();
            fileQuery.setVideoId(videoId);
            fileQuery.setUserId(videoInfoPost.getUserId());
            List<VideoInfoFilePost> dbInfoFileList = this.videoInfoFilePostMapper.selectList(fileQuery);
            Map<String, VideoInfoFilePost> uploadFileMap = uploadFileList.stream().collect(Collectors.toMap(item -> item.getUploadId(), Function.identity(),
                    (date1, date2) -> date2));
            boolean updateFileName = false;
            for (VideoInfoFilePost fileInfo : dbInfoFileList) {
                VideoInfoFilePost updateFile = uploadFileMap.get(fileInfo.getUploadId());
                if (updateFile == null) {
                    deleteFileList.add(fileInfo);
                } else if (updateFile.getFileName().equals(fileInfo.getFileName())) {
                    updateFileName = true;
                }
            }

            addFileList = uploadFileList.stream()
                    .filter(item -> item.getFileId() == null)
                    .collect(Collectors.toList());

            videoInfoPost.setLastUpdateTime(curDate);
            Boolean changeVideoInfo = this.changeVideoInfo(videoInfoPost);
            if (addFileList != null && !addFileList.isEmpty()) {
                videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            } else if (changeVideoInfo || updateFileName) {
                videoInfoPost.setStatus(VideoStatusEnum.STATUS2.getStatus());
            }
            this.videoInfoPostMapper.updateByVideoId(videoInfoPost, videoInfoPost.getVideoId());
        }

        // 处理删除文件
        if (!deleteFileList.isEmpty()) {
            List<String> delFileIdList = deleteFileList.stream()
                    .map(item -> item.getFileId())
                    .collect(Collectors.toList());
            this.videoInfoFilePostMapper.deleteBatchByFileId(delFileIdList, videoInfoPost.getUserId());

            List<String> delFilePathList = deleteFileList.stream()
                    .map(item -> item.getFilePath())
                    .collect(Collectors.toList());
            redisComponent.addFile2DelList(videoId, delFilePathList);
        }

        // 处理文件列表
        log.info("=== 开始处理文件列表 ===");
        log.info("当前videoId: {}", videoId);
        log.info("videoInfoPost.getVideoId(): {}", videoInfoPost.getVideoId());

        int index = 1;
        for (VideoInfoFilePost videoInfoFile : uploadFileList) {
            log.info("=== 处理第{}个文件 ===", index);
            log.info("处理前 - fileId: '{}'", videoInfoFile.getFileId());
            log.info("处理前 - uploadId: {}", videoInfoFile.getUploadId());
            log.info("处理前 - fileName: {}", videoInfoFile.getFileName());
            log.info("处理前 - videoId: {}", videoInfoFile.getVideoId());

            videoInfoFile.setFileIndex(index++);
            videoInfoFile.setVideoId(videoId); // 使用确定不为null的videoId变量
            videoInfoFile.setUserId(videoInfoPost.getUserId());

            log.info("设置videoId和userId后 - fileId: '{}'", videoInfoFile.getFileId());

            // 修复：使用StringTools.isEmpty()同时检查null和空字符串
            if (StringTools.isEmpty(videoInfoFile.getFileId())) {
                log.info("fileId为空（null或空字符串），开始生成新的fileId");
                String newFileId = StringTools.getRandomString(Constans.LENGTH_20);
                log.info("生成的新fileId: {}", newFileId);

                videoInfoFile.setFileId(newFileId);
                log.info("设置fileId后: '{}'", videoInfoFile.getFileId());

                videoInfoFile.setPostType(VideoFileUpdateTypeEnum.UPDATE.getStatus());
                videoInfoFile.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());

                log.info("设置postType和transferResult后 - fileId: '{}'", videoInfoFile.getFileId());
            } else {
                log.info("fileId不为空，值为: '{}'", videoInfoFile.getFileId());
            }

            log.info("处理后最终状态:");
            log.info("- fileId: '{}'", videoInfoFile.getFileId());
            log.info("- uploadId: {}", videoInfoFile.getUploadId());
            log.info("- userId: {}", videoInfoFile.getUserId());
            log.info("- videoId: {}", videoInfoFile.getVideoId());
            log.info("- fileIndex: {}", videoInfoFile.getFileIndex());
            log.info("- fileName: {}", videoInfoFile.getFileName());
            log.info("- postType: {}", videoInfoFile.getPostType());
            log.info("- transferResult: {}", videoInfoFile.getTransferResult());
        }

        // 执行数据库操作
        log.info("=== 处理完所有文件，准备执行数据库操作 ===");
        for (int i = 0; i < uploadFileList.size(); i++) {
            VideoInfoFilePost file = uploadFileList.get(i);
            log.info("文件{}最终状态 - fileId: '{}', uploadId: {}, videoId: {}",
                    i+1, file.getFileId(), file.getUploadId(), file.getVideoId());
        }

        log.info("准备执行insertOrUpdateBatch，uploadFileList: {}", uploadFileList);
        try {
            this.videoInfoFilePostMapper.insertOrUpdateBatch(uploadFileList);
            log.info("insertOrUpdateBatch执行成功");
        } catch (Exception e) {
            log.error("insertOrUpdateBatch执行失败", e);
            throw e;
        }

        // 处理文件转换队列
        if (addFileList != null && !addFileList.isEmpty()) {
            log.info("将文件添加到转换队列，addFileList: {}", addFileList);
            for (VideoInfoFilePost file : addFileList) {
                file.setUserId(videoInfoPost.getUserId());
                file.setVideoId(videoId); // 确保使用正确的videoId
            }
            redisComponent.addFile2TransferQueue(addFileList);
            log.info("文件已添加到转换队列");
        } else {
            log.info("没有新文件需要添加到转换队列");
        }

        log.info("=== 视频信息保存完成 ===");
    }

    private Boolean changeVideoInfo(VideoInfoPost videoInfoPost) {
        VideoInfoPost dbInfo = this.videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
        // 标题，封面，标签，简介
        if (!(videoInfoPost.getVideoName().equals(dbInfo.getVideoName())
                && videoInfoPost.getVideoCover().equals(dbInfo.getVideoCover())
                && videoInfoPost.getTags().equals(dbInfo.getTags())
                && videoInfoPost.getIntroduction().equals(dbInfo.getIntroduction()))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void transferVideoFile(VideoInfoFilePost videoInfoFilePost) {
        VideoInfoFilePost updateVideoInfo = new VideoInfoFilePost();
        try {
            UploadingFileDto fileDto = redisComponent.getUploadVideoFile(videoInfoFilePost.getUserId(), videoInfoFilePost.getUploadId());


            if (fileDto == null) {
                log.error("从Redis获取文件信息失败，uploadId: {}", videoInfoFilePost.getUploadId());
                throw new RuntimeException("文件信息不存在");
            }

            if (StringTools.isEmpty(fileDto.getFilePath())) {
                log.error("文件路径为空，uploadId: {}", videoInfoFilePost.getUploadId());
                throw new RuntimeException("文件路径为空");
            }


            String tempFilePath = appConfig.getProjectFolder() + Constans.FILE_FOLDER + Constans.FILE_FOLDER_TEMP + fileDto.getFilePath();
            File tempFile = new File(tempFilePath);
            String targetFilePath = appConfig.getProjectFolder() + Constans.FILE_FOLDER + Constans.FILE_VIDEO + fileDto.getFilePath();
            File targetFile = new File(targetFilePath);

            FileUtils.copyDirectory(tempFile, targetFile);

            // 删除临时目录
            FileUtils.forceDelete(tempFile);
            redisComponent.delVideoFileInfo(videoInfoFilePost.getUserId(), videoInfoFilePost.getUploadId());

            // 合并文件
            String completeVideo = targetFilePath + Constans.TEMP_VIDEO_NAME;
            this.union(targetFilePath, completeVideo, true);

            Integer duration = fFmpegUtils.getVideoInfoDuration(completeVideo);
            updateVideoInfo.setDuration(duration);
            updateVideoInfo.setFileSize(new File(completeVideo).length());
            updateVideoInfo.setFilePath(Constans.FILE_VIDEO + fileDto.getFilePath());
            updateVideoInfo.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getStatus());

            String audioFilePath = targetFilePath + Constans.AUDIO_SUFFIX;
            fFmpegUtils.extractAudio(completeVideo, audioFilePath);

            this.convertVideo2Ts(completeVideo);

        } catch (Exception e) {
            log.error("文件转移失败", e);
            updateVideoInfo.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
        } finally {
            videoInfoFilePostMapper.updateByUploadIdAndUserId(updateVideoInfo, videoInfoFilePost.getUploadId(), videoInfoFilePost.getUserId());
            VideoInfoFilePostquery FilePostQuery = new VideoInfoFilePostquery();
            FilePostQuery.setVideoId(videoInfoFilePost.getVideoId());
            FilePostQuery.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
            Integer fileCount = videoInfoFilePostMapper.selectCount(FilePostQuery);
            if (fileCount > 0) {
                VideoInfoPost videoUpdate = new VideoInfoPost();
                videoUpdate.setStatus(VideoStatusEnum.STATUS1.getStatus());
                videoInfoPostMapper.updateByVideoId(videoUpdate, videoInfoFilePost.getVideoId());
                return;
            }
            FilePostQuery.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
            Integer transferCount = videoInfoFilePostMapper.selectCount(FilePostQuery);
            if (transferCount == 0) {
                VideoInfoPost currentVideo = videoInfoPostMapper.selectByVideoId(videoInfoFilePost.getVideoId());
                if (currentVideo != null && currentVideo.getStatus() != null && 
                    currentVideo.getStatus() == VideoStatusEnum.STATUS5.getStatus()) {
                    log.info("视频已被标记为待人工复核，不更新状态，videoId: {}", videoInfoFilePost.getVideoId());
                    return;
                }

                Integer duration = videoInfoFilePostMapper.sumDuration(videoInfoFilePost.getVideoId());
                String videoId = videoInfoFilePost.getVideoId();
                VideoInfoPost videoUpdate = new VideoInfoPost();
                videoUpdate.setStatus(VideoStatusEnum.STATUS2.getStatus());
                videoUpdate.setDuration(duration);
                videoInfoPostMapper.updateByVideoId(videoUpdate, videoId);

                auditVideoAfterTranscode(videoId);
            }

        }

    }

    private void auditVideoAfterTranscode(String videoId) {
        try {
            VideoInfoPost video = videoInfoPostMapper.selectByVideoId(videoId);
            if (video == null) {
                log.error("审核失败：视频不存在，videoId: {}", videoId);
                return;
            }

            if (video.getStatus() != null && video.getStatus() == VideoStatusEnum.STATUS5.getStatus()) {
                log.info("视频已被标记为待人工复核，跳过AI审核，videoId: {}", videoId);
                return;
            }

            VideoInfoFilePostquery fileQuery = new VideoInfoFilePostquery();
            fileQuery.setVideoId(videoId);
            fileQuery.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getStatus());
            List<VideoInfoFilePost> successFiles = videoInfoFilePostMapper.selectList(fileQuery);

            if (successFiles == null || successFiles.isEmpty()) {
                log.error("审核失败：无成功转码的文件，videoId: {}", videoId);
                return;
            }

            VideoInfoFilePost firstFile = successFiles.get(0);
            String videoPath = appConfig.getProjectFolder() + Constans.FILE_FOLDER + firstFile.getFilePath() + Constans.M3U8_NAME;
            String audioPath = appConfig.getProjectFolder() + Constans.FILE_FOLDER + firstFile.getFilePath() + Constans.AUDIO_SUFFIX;

            log.info("开始AI审核视频，videoId: {}, path: {}", videoId, videoPath);
            AiAuditResult frameResult = aiAuditService.auditVideoFrames(videoId, videoPath);

            AiAuditResult finalResult = frameResult;
            StringBuilder auditDetails = new StringBuilder();
            auditDetails.append("图片审核: ").append(frameResult.toString());

            if (frameResult.getPassed()) {
                log.info("图片帧审核通过，开始音频审核，videoId: {}", videoId);
                String audioText = aiAuditService.speechToText(audioPath);
                if (audioText != null && !audioText.isEmpty()) {
                    log.info("语音转文字成功，开始审核音频内容，videoId: {}, 文字长度: {}", videoId, audioText.length());
                    AiAuditResult audioResult = aiAuditService.auditAudio(videoId, audioText);
                    auditDetails.append("; 音频审核: ").append(audioResult.toString());
                    finalResult = audioResult;
                } else {
                    log.info("音频为空或语音转文字失败，跳过音频审核，videoId: {}", videoId);
                    auditDetails.append("; 音频审核: 无音频内容");
                }
            }

            if (finalResult.getPassed()) {
                String titleAndIntro = "标题: " + (video.getVideoName() == null ? "" : video.getVideoName())
                        + "\n简介: " + (video.getIntroduction() == null ? "" : video.getIntroduction());
                AiAuditResult textResult = aiAuditService.auditText(videoId, titleAndIntro);
                auditDetails.append("; 标题简介审核: ").append(textResult.toString());
                finalResult = textResult;
            }

            VideoInfoPost auditUpdate = new VideoInfoPost();
            auditUpdate.setAiAuditTime(new Date());
            auditUpdate.setAiAuditResult(auditDetails.toString());

            if (finalResult.getPassed()) {
                auditUpdate.setAiAuditStatus(1);
                auditUpdate.setStatus(VideoStatusEnum.STATUS3.getStatus());
                log.info("视频AI审核通过，videoId: {}", videoId);

                // 审核通过后，同步到正式视频表
                transferToVideoInfo(videoId);
            } else {
                auditUpdate.setAiAuditStatus(2);
                auditUpdate.setAiAuditReason(finalResult.getReason());
                auditUpdate.setStatus(VideoStatusEnum.STATUS5.getStatus());
                log.warn("视频AI审核不通过，videoId: {}, reason: {}", videoId, finalResult.getReason());
            }

            videoInfoPostMapper.updateByVideoId(auditUpdate, videoId);

        } catch (Exception e) {
            log.error("AI审核异常，videoId: {}", videoId, e);
        }
    }

    /**
     * 将审核通过的视频从投稿表同步到正式视频表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void transferToVideoInfo(String videoId) throws BusinessException {
        try {
            VideoInfoPost videoInfoPost = videoInfoPostMapper.selectByVideoId(videoId);
            if (videoInfoPost == null) {
                log.error("同步失败：投稿视频不存在，videoId: {}", videoId);
                return;
            }

            if (videoInfoPost.getStatus() != null && videoInfoPost.getStatus() == VideoStatusEnum.STATUS5.getStatus()) {
                log.warn("视频处于待人工复核状态，不能发布，videoId: {}", videoId);
                return;
            }

            // 2. 检查正式表是否已存在
            VideoInfo existingVideo = videoInfoMapper.selectByVideoId(videoId);
            if (existingVideo != null) {
                log.info("视频已存在于正式表，跳过同步，videoId: {}", videoId);
                return;
            }

            // 3. 复制视频信息到正式表
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setVideoId(videoInfoPost.getVideoId());
            videoInfo.setVideoCover(videoInfoPost.getVideoCover());
            videoInfo.setVideoName(videoInfoPost.getVideoName());
            videoInfo.setUserId(videoInfoPost.getUserId());
            videoInfo.setCreateTime(videoInfoPost.getCreateTime());
            videoInfo.setLastUpdateTime(new Date());
            videoInfo.setPCategoryId(videoInfoPost.getPCategoryId());
            videoInfo.setCategoryId(videoInfoPost.getCategoryId());
            videoInfo.setPostType(videoInfoPost.getPostType());
            videoInfo.setOriginInfo(videoInfoPost.getOriginInfo());
            videoInfo.setTags(videoInfoPost.getTags());
            videoInfo.setIntroduction(videoInfoPost.getIntroduction());
            videoInfo.setInteraction(videoInfoPost.getInteraction());
            videoInfo.setDuration(videoInfoPost.getDuration());
            videoInfo.setPlayCount(0);
            videoInfo.setLikeCount(0);
            videoInfo.setDankuCount(0);
            videoInfo.setCommentCount(0);
            videoInfo.setCoinCount(0);
            videoInfo.setCollectCount(0);
            videoInfo.setRecommendType(1);
            videoInfo.setLastPlayTime(null);

            videoInfoMapper.insert(videoInfo);
            log.info("视频信息同步到正式表成功，videoId: {}", videoId);

            // 4. 同步文件信息
            VideoInfoFilePostquery fileQuery = new VideoInfoFilePostquery();
            fileQuery.setVideoId(videoId);
            List<VideoInfoFilePost> filePostList = videoInfoFilePostMapper.selectList(fileQuery);

            if (filePostList != null && !filePostList.isEmpty()) {
                for (VideoInfoFilePost filePost : filePostList) {
                    VideoInfoFile videoInfoFile = new VideoInfoFile();
                    videoInfoFile.setFileId(filePost.getFileId());
                    videoInfoFile.setUserId(filePost.getUserId());
                    videoInfoFile.setVideoId(filePost.getVideoId());
                    videoInfoFile.setFileName(filePost.getFileName());
                    videoInfoFile.setFileIndex(filePost.getFileIndex());
                    videoInfoFile.setFileSize(filePost.getFileSize());
                    videoInfoFile.setFilePath(filePost.getFilePath());
                    videoInfoFile.setDuration(filePost.getDuration());
                    videoInfoFileMapper.insert(videoInfoFile);
                }
                log.info("视频文件信息同步成功，videoId: {}，文件数: {}", videoId, filePostList.size());
            }

        } catch (Exception e) {
            log.error("同步视频到正式表失败，videoId: {}", videoId, e);
            throw new BusinessException("同步视频到正式表失败");
        }
    }

    private void convertVideo2Ts(String completeVideo) throws BusinessException {

        File videoFile = new File(completeVideo);
        File tsFolder = videoFile.getParentFile();
        String codec = fFmpegUtils.getVideoCodec(completeVideo);
        if (Constans.VIDEO_CODE_HEVC.equals(codec)) {
            String tempFileName = completeVideo + Constans.VIDEO_CODE_TEMP_FILE_SUFFIX;
            new File(completeVideo).renameTo(new File(tempFileName));
            fFmpegUtils.convertHevc2Mp4(tempFileName, completeVideo);
            new File(tempFileName).delete();
        }

        fFmpegUtils.convertVideo2Ts(tsFolder, completeVideo);
        videoFile.delete();
    }


    private void union(String dirPath, String toFilePath, Boolean delSource) throws BusinessException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        try (RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                // 拼接块文件路径
                File chunkFile = new File(dirPath + File.separator + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败", e);
                    throw new BusinessException("合并分片失败");
                } finally {
                    if (readFile != null) {
                        readFile.close();
                    }
                }
            }
        } catch (Exception e) {
            log.error("合并文件" + dirPath + "出错了", e);
            throw new BusinessException("合并文件" + dirPath + "出错了");
        }
        if (delSource) {
            for (int i = 0; i < fileList.length; i++) {
                fileList[i].delete();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditVideo(String videoId, Integer status, String reason) throws BusinessException {
        VideoStatusEnum videoStatusEnum = VideoStatusEnum.getByStatus(status);
        if (videoStatusEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        VideoInfoPost videoInfoPost = new VideoInfoPost();
        videoInfoPost.setStatus(status);
        VideoInfoPostquery videoInfoPostquery = new VideoInfoPostquery();
        videoInfoPostquery.setStatus(videoStatusEnum.STATUS2.getStatus());
        videoInfoPostquery.setVideoId(videoId);
        Integer auditCount = this.videoInfoPostMapper.updateByParam(videoInfoPost, videoInfoPostquery);
        if (auditCount == 0) {
            throw new BusinessException("审核失败,请稍后再试");

        }
        VideoInfoFilePost videoInfoFilePost = new VideoInfoFilePost();
        videoInfoFilePost.setUpdateType(VideoFileUpdateTypeEnum.NO_UPDATE.getStatus());

        VideoInfoFilePostquery filePostQuery = new VideoInfoFilePostquery();
        filePostQuery.setVideoId(videoId);
        this.videoInfoFilePostMapper.updateByParam(videoInfoFilePost, filePostQuery);
        if (VideoStatusEnum.STATUS4 == videoStatusEnum) {
            return;
        }
        VideoInfoPost infoPost = this.videoInfoPostMapper.selectByVideoId(videoId);

        VideoInfo dbVideoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (dbVideoInfo == null) {
            SysSettingDto sysSettingDto = new SysSettingDto();
            //  TODO 给用户加硬币

        }
        // 更新视频信息到正式表，先删除在添加
        VideoInfoFilequery videoInfoFileQuery = new VideoInfoFilequery();
        videoInfoFileQuery.setVideoId(videoId);
        this.videoInfoFileMapper.deleteByParam(videoInfoFileQuery);

        VideoInfoFilePostquery videoInfoFilePostQuery = new VideoInfoFilePostquery();
        videoInfoFilePostQuery.setVideoId(videoId);
        List<VideoInfoFilePost> videoInfoFilePostList = this.videoInfoFilePostMapper.selectList(videoInfoFilePostQuery);

        List<VideoInfoFile> videoInfoFileList = CopyTools.copyList(videoInfoFilePostList, VideoInfoFile.class);
        this.videoInfoFileMapper.insertBatch(videoInfoFileList);

        // 删除文件
        List<String> filePathList = redisComponent.getDelFileList(videoId);
        if (filePathList != null) {
            for (String path : filePathList) {
                File file = new File(appConfig.getProjectFolder() + Constans.FILE_FOLDER + path);
                if (file.exists()) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        log.error("删除文件失败", e);
                    }
                }
            }
        }

        redisComponent.cleanDelFileList(videoId);

        //TODO 保存信息到es

    }

}
