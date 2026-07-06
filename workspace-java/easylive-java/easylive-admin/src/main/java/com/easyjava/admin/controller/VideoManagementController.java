package com.easyjava.admin.controller;

import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.query.VideoInfoquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.VideoInfoMapper;
import com.easyjava.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/video")
@Slf4j
public class VideoManagementController extends ABaseController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoquery> videoInfoMapper;

    /**
     * 获取所有已发布视频列表
     */
    @RequestMapping("/management/list")
    public ResponseVO getVideoList(VideoInfoquery query) throws BusinessException {
        query.setOrderBy("v.create_time desc");
        if (query.getPageNo() == null || query.getPageNo() < 1) {
            query.setPageNo(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        // 只查询已发布的视频（审核通过）
        query.setStatus(3);
        // 查询用户信息
        query.setQueryUserInfo(true);
        PaginationResultVO<VideoInfo> result = videoInfoService.findListByPage(query);
        return getSuccessResponseVO(result);
    }

    /**
     * 设置视频推荐状态
     */
    @RequestMapping("/management/recommend")
    public ResponseVO setRecommend(@NotEmpty String videoId, Integer recommendType) throws BusinessException {
        if (recommendType == null) {
            recommendType = 1; // 默认设为推荐
        }
        VideoInfo video = new VideoInfo();
        video.setRecommendType(recommendType);
        videoInfoMapper.updateByVideoId(video, videoId);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除视频（管理员删除，用户端不可见）
     */
    @RequestMapping("/management/delete")
    public ResponseVO deleteVideo(@NotEmpty String videoId) {
        try {
            videoInfoService.deleteVideoByAdmin(videoId);
            return getSuccessResponseVO(null);
        } catch (BusinessException e) {
            return getErrorResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("删除视频失败，videoId: {}", videoId, e);
            return getErrorResponseVO("删除视频失败");
        }
    }

    private ResponseVO getErrorResponseVO(String message) {
        ResponseVO responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_ERROR);
        responseVO.setCode(ResponseCodeEnum.CODE_500.getCode());
        responseVO.setInfo(message);
        return responseVO;
    }

    /**
     * 获取视频统计数据
     */
    @RequestMapping("/management/stats")
    public ResponseVO getVideoStats() {
        VideoInfoquery query = new VideoInfoquery();
        // 总视频数
        Integer totalCount = videoInfoMapper.selectCount(query);
        // 推荐视频数
        query.setRecommendType(1);
        Integer recommendCount = videoInfoMapper.selectCount(query);

        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("total", totalCount != null ? totalCount : 0);
        stats.put("recommended", recommendCount != null ? recommendCount : 0);
        return getSuccessResponseVO(stats);
    }
}
