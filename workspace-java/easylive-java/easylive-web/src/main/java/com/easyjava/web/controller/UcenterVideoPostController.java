package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.VideoComment;
import com.easyjava.entity.po.VideoDanmu;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.po.VideoInfoPost;
import com.easyjava.entity.query.VideoInfoPostquery;
import com.easyjava.entity.query.VideoInfoquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.entity.vo.VideoStatusCountInfoVO;
import com.easyjava.enums.VideoStatusEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.VideoInfoFilePostService;
import com.easyjava.service.VideoInfoPostService;
import com.easyjava.service.VideoInfoService;
import com.easyjava.service.VideoCommentService;
import com.easyjava.service.VideoDanmuService;
import com.easyjava.service.UserActionService;
import com.easyjava.service.UserFocusService;
import com.easyjava.utlis.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
/*
这是一个用户中心视频投稿控制器，负责处理用户发布和管理自己视频的相关操作
*/
@RequestMapping("/ucenter")
@RestController
@Slf4j
@Validated
public class UcenterVideoPostController extends ABaseController {
    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @Resource
    private VideoInfoService videoInfoService;
    
    @Resource
    private VideoCommentService videoCommentService;
    
    @Resource
    private VideoDanmuService videoDanmuService;
    
    @Resource
    private UserActionService userActionService;

    @Resource
    private UserFocusService userFocusService;

    /*用户上传发布新视频或编辑已有视频
    处理流程：
    获取当前登录用户信息 (getTokenUserInfoDto)
    将JSON字符串转为文件列表对象
    封装VideoInfoPost对象
    设置用户ID（从token获取）
    调用service保存视频信息和文件列表
    返回成功响应

    支持新增和编辑（videoId为空则新增）*/
    @RequestMapping("/postVideo")
    public ResponseVO postVideo(String videoId,
                                @NotEmpty String videoCover,
                                @NotEmpty @Size(max = 100) String videoName,
                                @NotNull Integer pCategoryId,
                                Integer categoryId,
                                @NotNull Integer postType,
                                @NotEmpty @Size(max = 300) String tags,
                                @Size(max = 2000) String introduction,
                                @Size(max = 3) String interaction,
                                @NotEmpty String uploadFileList
    ) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        List<VideoInfoFilePost> filePostList = JsonUtils.convertJsonArray2list(uploadFileList, VideoInfoFilePost.class);
        VideoInfoPost videoInfoPost = new VideoInfoPost();
        videoInfoPost.setVideoId(videoId);
        videoInfoPost.setVideoName(videoName);
        videoInfoPost.setVideoCover(videoCover);
        videoInfoPost.setCategoryId(categoryId);
        videoInfoPost.setPCategoryId(pCategoryId);
        videoInfoPost.setPostType(postType);
        videoInfoPost.setTags(tags);
        videoInfoPost.setIntroduction(introduction);
        videoInfoPost.setInteraction(interaction);

        videoInfoPost.setUserId(tokenUserInfoDto.getUserId());
        videoInfoPostService.saveVideoInfo(videoInfoPost, filePostList);
        return getSuccessResponseVO(null);
    }

/* 查看自己发布的视频列表（支持筛选和分页）
*  获取当前用户ID
2️⃣ 构建查询条件：
   ├─ 只查当前用户的视频 (userId)
   ├─ 按创建时间倒序
   └─ 查询统计信息

3️⃣ 状态筛选逻辑（重点！）：
   ┌─ status = null    → 查询所有
   ├─ status = -1      → 排除审核通过(3)和审核失败(4)
   │                      即：查询"进行中"的视频
   └─ status = 其他值  → 查询指定状态

4️⃣ 支持视频名称模糊搜索
5️⃣ 返回分页结果
*/
    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList
            (Integer status, Integer pageNo, String videoNameFuzzy) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoPostquery videoInfoQuery = new VideoInfoPostquery();

        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setOrderBy("v.create_time desc");
        videoInfoQuery.setPageNo(pageNo);
        if (status != null) {
            if (status == -1) {
                videoInfoQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(),VideoStatusEnum.STATUS4.getStatus()});

            }else {
                videoInfoQuery.setStatus(status);
            }
        }
        videoInfoQuery.setVideoNameFuzzy(videoNameFuzzy);
        videoInfoQuery.setQueryCountInfo(true);
       PaginationResultVO resultVO= videoInfoPostService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

/*统计当前用户各状态视频的数量
* 执行3次查询，分别统计：

📊 第1次查询：审核通过数量
   ├─ status = STATUS3（审核通过）
   └─ 结果：auditPassCount

📊 第2次查询：审核失败数量
   ├─ status = STATUS4（审核失败）
   └─ 结果：auditFailCount

📊 第3次查询：进行中数量
   ├─ 排除 STATUS3 和 STATUS4
   └─ 结果：inProgress（待审核+审核中+其他中间状态）

最后封装成：
VideoStatusCountInfoVO {
    auditPassCount: 10,    // 审核通过
    auditFailCount: 2,     // 审核失败
    inProgress: 5          // 进行中
}
```
*/
    @RequestMapping("/getVideoCountInfo")
    public ResponseVO getVideoCountInfo()throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoPostquery videoInfoPostquery=new VideoInfoPostquery();
        videoInfoPostquery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoPostquery.setStatus(VideoStatusEnum.STATUS3.getStatus());
        Integer auditPassCount=videoInfoPostService.findCountByParam(videoInfoPostquery);

        videoInfoPostquery.setStatus(VideoStatusEnum.STATUS4.getStatus());
        Integer auditFailCount=videoInfoPostService.findCountByParam(videoInfoPostquery);

        videoInfoPostquery.setStatus(null);
        videoInfoPostquery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(),VideoStatusEnum.STATUS4.getStatus()});
        Integer inProgress=videoInfoPostService.findCountByParam(videoInfoPostquery);

        VideoStatusCountInfoVO countInfoVO=new VideoStatusCountInfoVO();
        countInfoVO.setAuditFailCount(auditFailCount);
        countInfoVO.setAuditPassCount(auditPassCount);
        countInfoVO.setInProgress(inProgress);
        return getSuccessResponseVO(countInfoVO);

    }

    /*加载用户所有视频（审核通过的）*/
    @RequestMapping("/loadAllVideo")
    public ResponseVO loadAllVideo(Integer pageNo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoquery videoInfoQuery = new VideoInfoquery();
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setStatus(3);
        videoInfoQuery.setOrderBy("create_time desc");
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setPageSize(20);
        PaginationResultVO resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    /*加载用户的评论*/
    @RequestMapping("/loadComment")
    public ResponseVO loadComment(Integer pageNo, String keyword) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(null);
        }
        Integer pageSize = 10;
        List<VideoComment> list = videoCommentService.getCommentListByUserId(
            tokenUserInfoDto.getUserId(),
            keyword,
            pageNo,
            pageSize
        );
        Integer totalCount = videoCommentService.getCommentCountByUserId(
            tokenUserInfoDto.getUserId(),
            keyword
        );
        PaginationResultVO result = new PaginationResultVO(totalCount, pageSize, pageNo, list);
        return getSuccessResponseVO(result);
    }

    /*删除用户评论*/
    @RequestMapping("/delComment")
    public ResponseVO delComment(Integer commentId) {
        videoCommentService.deleteComment(commentId);
        return getSuccessResponseVO(null);
    }

    /*加载用户弹幕*/
    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(Integer pageNo, String keyword) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(null);
        }
        Integer pageSize = 10;
        List<VideoDanmu> list = videoDanmuService.getDanmuListByUserId(
            tokenUserInfoDto.getUserId(),
            keyword,
            pageNo,
            pageSize
        );
        Integer totalCount = videoDanmuService.getDanmuCountByUserId(
            tokenUserInfoDto.getUserId(),
            keyword
        );
        PaginationResultVO result = new PaginationResultVO(totalCount, pageSize, pageNo, list);
        return getSuccessResponseVO(result);
    }

    /*删除用户弹幕*/
    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(Integer danmuId) {
        videoDanmuService.deleteDanmu(danmuId);
        return getSuccessResponseVO(null);
    }

    /*获取实时统计信息*/
    @RequestMapping("/getActualTimeStatisticsInfo")
    public ResponseVO getActualTimeStatisticsInfo() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        VideoInfoquery videoInfoQuery = new VideoInfoquery();
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setStatus(3);
        Integer videoCount = videoInfoService.findCountByParam(videoInfoQuery);
        Integer playCount = videoInfoService.getTotalPlayCountByUserId(tokenUserInfoDto.getUserId());
        Integer fansCount = userFocusService.getFansCount();
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("videoCount", videoCount);
        stats.put("playCount", playCount != null ? playCount : 0);
        stats.put("fansCount", fansCount != null ? fansCount : 0);
        return getSuccessResponseVO(stats);
    }

    /*获取周统计信息*/
    @RequestMapping("/getWeekStatisticsInfo")
    public ResponseVO getWeekStatisticsInfo() {
        return getSuccessResponseVO(null);
    }

    /*删除视频*/
    @RequestMapping("/deleteVideo")
    public ResponseVO deleteVideo(String videoId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        try {
            videoInfoPostService.deleteVideo(videoId, tokenUserInfoDto.getUserId());
            return getSuccessResponseVO(null);
        } catch (BusinessException e) {
            return getErrorResponseVO(e.getMessage());
        }
    }

    /*获取视频信息*/
    @RequestMapping("/getVideoByVideoId")
    public ResponseVO getVideoByVideoId(String videoId) {
        VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
        return getSuccessResponseVO(videoInfo);
    }

    /*保存视频互动信息*/
    @RequestMapping("/saveVideoInteraction")
    public ResponseVO saveVideoInteraction(String videoId, String interaction) {
        return getSuccessResponseVO(null);
    }
}
