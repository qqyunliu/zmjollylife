package com.easyjava.component;

import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.dto.SysSettingDto;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.dto.UploadingFileDto;
import com.easyjava.entity.po.CategoryInfo;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.redis.RedisUtils;
import com.easyjava.utlis.DateUtils;
import com.easyjava.utlis.StringTools;
import com.easyjava.utlis.TokenContext;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/*这个组件服务于：

用户认证系统：Token管理

安全验证：验证码服务

性能优化：分类数据缓存

文件处理：大文件上传、转码队列、清理队列

系统配置：系统设置缓存

通过Redis的高性能特性，为应用提供了高效的数据存储和队列服务。*/
@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private AppConfig appConfig;

/*使用UUID生成唯一key，避免冲突

验证码10分钟过期（1分钟×10）

完整的CRUD操作*/
    public String saveCheckCode(String code) {
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constans.REDIS_KEY_CHECK_CODE + checkCodeKey, code, Constans.REDIS_KEY_EXPIRES_ONE_MIN * 10);
        return checkCodeKey;
    }

    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(Constans.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.delete(Constans.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void saveTokenInfo(TokenUserInfoDto tokenUserInfoDto) {
        /*Web用户Token
        * 7天有效期
        Token作为key的一部分，避免冲突
        在DTO中记录过期时间*/
        String token = UUID.randomUUID().toString();
        tokenUserInfoDto.setExpireAt((System.currentTimeMillis() + Constans.REDIS_KEY_EXPIRES_ONE_DAY * 7));
        tokenUserInfoDto.setToken(token);
        redisUtils.setex(Constans.REDIS_KEY_TOKEN_WEB + token, tokenUserInfoDto, Constans.REDIS_KEY_EXPIRES_ONE_DAY * 7);

    }

    public void updateTokenInfo(TokenUserInfoDto tokenUserInfoDto) {
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getToken())) {
            return;
        }
        long ttl = tokenUserInfoDto.getExpireAt() - System.currentTimeMillis();
        if (ttl <= 0) {
            ttl = Constans.REDIS_KEY_EXPIRES_ONE_DAY;
        }
        redisUtils.setex(Constans.REDIS_KEY_TOKEN_WEB + tokenUserInfoDto.getToken(), tokenUserInfoDto, ttl);
    }

    public TokenUserInfoDto getTokenInfo(String token) {
        return (TokenUserInfoDto) redisUtils.get(Constans.REDIS_KEY_TOKEN_WEB + token);
    }

    public TokenUserInfoDto getTokenUserInfoDto() {
        return TokenContext.get();
    }

    public String saveTokenInfo4Admin(String account) {
        /*管理员Token
        * 1天有效期（比普通用户短）
        只存储账号信息*/
        String token = UUID.randomUUID().toString();
        redisUtils.setex(Constans.REDIS_KEY_TOKEN_ADMIN + token, account, Constans.REDIS_KEY_EXPIRES_ONE_DAY);
        return token;
    }

    public String getTokenInfo4Admin(String token) {
        return (String) redisUtils.get(Constans.REDIS_KEY_TOKEN_ADMIN + token);
    }

    public void cleanToken4Admin(String token) {
        redisUtils.delete(Constans.REDIS_KEY_TOKEN_ADMIN + token);
    }

    public void cleanTokenWeb(String token) {
        if (StringTools.isEmpty(token)) {
            return;
        }
        redisUtils.delete(Constans.REDIS_KEY_TOKEN_WEB + token);
    }

    public void saveCategoryList(List<CategoryInfo> categoryInfoList) {
        /*
         分类信息缓存
         缓存不经常变动的分类数据，减少数据库查询*/
        redisUtils.set(Constans.REDIS_KEY_CATEGORY_LIST, categoryInfoList);
    }

    public List<CategoryInfo> getCategoryList() {
        return (List<CategoryInfo>) redisUtils.get(Constans.REDIS_KEY_CATEGORY_LIST);
    }

    public String savePreVideoFileInfo(String userId, String fileName, Integer chunks) {
        /*文件上传管理（核心功能）
            保存上传文件信息
            支持大文件分片上传
            自动创建文件存储目录
            1天有效期，防止僵尸上传任务*/
        String uploadId = StringTools.getRandomString(Constans.LENGTH_15);
        UploadingFileDto fileDto = new UploadingFileDto();
        fileDto.setChunks(chunks);
        fileDto.setFileName(fileName);
        fileDto.setUploadId(uploadId);
        fileDto.setFileSize(0);
        String day = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        String filePath = day + "/" + userId + uploadId;
        String folder = appConfig.getProjectFolder() + Constans.FILE_FOLDER + Constans.FILE_FOLDER_TEMP + filePath;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        fileDto.setFilePath(filePath);
        redisUtils.setex(Constans.REDIS_KEY_UPLOADING_FILE + userId + uploadId, fileDto, Constans.REDIS_KEY_EXPIRES_ONE_DAY);
        return uploadId;

    }

    public UploadingFileDto getUploadVideoFile(String userId, String uploadId) {
        return (UploadingFileDto) redisUtils.get(Constans.REDIS_KEY_UPLOADING_FILE + userId + uploadId);

    }

    public SysSettingDto getSysSettingDto() {
       /* 系统设置缓存 空值保护，避免缓存穿透*/
        SysSettingDto sysSettingDto = (SysSettingDto) redisUtils.get(Constans.REDIS_KEY_SYS_SETTING);
        if (sysSettingDto == null) {
            sysSettingDto = new SysSettingDto();

        }
        return sysSettingDto;
    }

    public void updateVideoFileInfo(String userId, UploadingFileDto fileDto) {
        redisUtils.setex(Constans.REDIS_KEY_UPLOADING_FILE + userId + fileDto.getUploadId(), fileDto, Constans.REDIS_KEY_EXPIRES_ONE_DAY);

    }

    public void delVideoFileInfo(String userId, String uploadId) {
        redisUtils.delete(Constans.REDIS_KEY_UPLOADING_FILE + userId + uploadId);
    }
    public void addFile2DelList(String videoId, List<String> filePathList) {
        // 添加到删除队列
        redisUtils.lpushAll(Constans.REDIS_KEY_FILE_DEL+videoId,filePathList,Constans.REDIS_KEY_EXPIRES_ONE_DAY*7);
    }

    public List<String> getDelFileList(String videoId){
        return redisUtils.getQueueList(Constans.REDIS_KEY_FILE_DEL+videoId);
    }

    public void addFile2TransferQueue(List<VideoInfoFilePost> addFileList) {
        // 添加到转码队列
        redisUtils.lpushAll(Constans.REDIS_KEY_QUEUE_TRANSFER,addFileList,0);
    }

    public VideoInfoFilePost getFileFromTransferQueue(){
        // 从转码队列获取文件
        return (VideoInfoFilePost) redisUtils.rpop(Constans.REDIS_KEY_QUEUE_TRANSFER);
    }

    public void cleanDelFileList(String videoId) {
        redisUtils.delete(Constans.REDIS_KEY_FILE_DEL+videoId);
    }
}
