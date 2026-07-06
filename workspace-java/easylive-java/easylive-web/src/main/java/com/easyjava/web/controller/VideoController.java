package com.easyjava.web.controller;

import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.po.VideoInfoFile;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.query.VideoInfoFilequery;
import com.easyjava.entity.query.VideoInfoquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.entity.vo.VideoInfoResultVO;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.enums.VideoRecommendTypeEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.VideoInfoFileService;
import com.easyjava.service.VideoInfoService;
import com.easyjava.service.UserActionService;
import com.easyjava.service.SearchService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
/*
* 这是一个视频管理控制器，负责处理视频相关的HTTP请求*/
@RestController
@RequestMapping("/video")
@Validated
public class VideoController extends ABaseController {
    @Resource
    private VideoInfoService videoInfoService;
    @Resource
    private VideoInfoFileService videoInfoFileService;
    @Resource
    private UserActionService userActionService;
    @Resource
    private SearchService searchService;

    /*获取平台推荐的视频列表
    * - 创建查询对象
- 设置查询用户信息 (queryUserInfo=true)
- 按创建时间倒序排列
- 筛选推荐类型的视频 (RECOMMEND)
- 调用service查询并返回*/
    @RequestMapping("/loadRecommendVideo")
    public ResponseVO loadRecommendVideo(Integer pageNo, Integer pageSize) {
        VideoInfoquery videoInfoQuery = new VideoInfoquery();
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("create_time desc");
        videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.RECOMMEND.getType());
        if (pageNo != null) {
            videoInfoQuery.setPageNo(pageNo);
        }
        if (pageSize != null) {
            videoInfoQuery.setPageSize(pageSize);
        }
        PaginationResultVO resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }
/* 按分类分页查询非推荐视频
参数：
  - pCategoryId: 父分类ID
  - categoryId: 子分类ID
  - pageNo: 页码

处理逻辑：
  ✓ 设置分类条件
  ✓ 设置分页参数
  ✓ 排除推荐视频 (NO_RECOMMEND)
  ✓ 返回分页结果*/
    @RequestMapping("/loadVideo")
    public ResponseVO loadVideo(Integer pCategoryId, Integer categoryId, Integer pageNo) {
        VideoInfoquery videoInfoQuery = new VideoInfoquery();
        videoInfoQuery.setCategoryId(categoryId);
        videoInfoQuery.setPCategoryId(pCategoryId);
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("create_time desc");
        PaginationResultVO resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    /*
    根据videoId获取单个视频的详细信息
    - 参数校验 (@NotEmpty String videoId)
- 查询视频信息
- 如果不存在 → 抛出404异常
- TODO: 获取用户行为（点赞、收藏等）
            - 封装成ResultVO返回*/
    @RequestMapping("/getVideoInfo")
    public ResponseVO getVideoInfo(@NotEmpty String videoId) throws BusinessException {
        VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }

        videoInfoService.incrementPlayCountByVideoId(videoId);
        videoInfo.setPlayCount((videoInfo.getPlayCount() == null ? 0 : videoInfo.getPlayCount()) + 1);
        
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        String userId = tokenUserInfoDto != null ? tokenUserInfoDto.getUserId() : null;
        
        Boolean haveLike = userActionService.getUserAction(userId, videoId, 0) != null;
        Boolean haveCoin = userActionService.getUserAction(userId, videoId, 4) != null;
        Boolean haveCollect = userActionService.getUserAction(userId, videoId, 3) != null;
        
        videoInfo.setHaveLike(haveLike);
        videoInfo.setHaveCoin(haveCoin);
        videoInfo.setHaveCollect(haveCollect);
        
        VideoInfoResultVO resultVO = new VideoInfoResultVO(videoInfo,new ArrayList());
        return getSuccessResponseVO(resultVO);
    }

/*    获取某个视频下的所有分P文件（多集视频场景）
            - 根据videoId查询
- 按文件索引升序排列 (file_index asc)
- 返回文件列表*/
    @RequestMapping("/loadVideoPList")
    public ResponseVO loadVideoList(@NotEmpty String videoId)  {
        VideoInfoFilequery videoInfoFilequery=new VideoInfoFilequery();
        videoInfoFilequery.setVideoId(videoId);
        videoInfoFilequery.setOrderBy("file_index asc");
        List<VideoInfoFile> fileList =videoInfoFileService.findListByParam(videoInfoFilequery);
        return getSuccessResponseVO(fileList);
    }

    /*记录用户的在线播放行为*/
    @RequestMapping("/reportVideoPlayOnline")
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId,@NotEmpty String deviceId)  {

        return getSuccessResponseVO(null);
    }

    /*搜索视频*/
    @RequestMapping("/search")
    public ResponseVO search(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchService.recordSearchKeyword(keyword);
        }
        List<VideoInfo> videoList = videoInfoService.searchVideo(keyword);
        return getSuccessResponseVO(videoList);
    }

    /*获取搜索热词*/
    @RequestMapping("/getSearchKeywordTop")
    public ResponseVO getSearchKeywordTop() {
        return getSuccessResponseVO(searchService.getHotKeywordList());
    }

    /*获取推荐视频*/
    @RequestMapping("/getVideoRecommend")
    public ResponseVO getVideoRecommend() {
        VideoInfoquery videoInfoQuery = new VideoInfoquery();
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("create_time desc");
        videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.RECOMMEND.getType());
        List<VideoInfo> videoList = videoInfoService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO(videoList);
    }

    /*获取热门视频*/
    @RequestMapping("/loadHotVideoList")
    public ResponseVO loadHotVideoList() {
        VideoInfoquery videoInfoQuery = new VideoInfoquery();
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("play_count desc");
        videoInfoQuery.setStatus(3);
        videoInfoQuery.setPageNo(1);
        videoInfoQuery.setPageSize(20);
        PaginationResultVO resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO.getList());
    }
}
