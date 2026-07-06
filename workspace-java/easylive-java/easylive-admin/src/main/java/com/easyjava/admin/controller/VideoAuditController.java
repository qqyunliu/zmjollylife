package com.easyjava.admin.controller;

import com.easyjava.entity.dto.AiAuditResult;
import com.easyjava.entity.po.VideoInfoPost;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.query.VideoInfoFilePostquery;
import com.easyjava.entity.query.VideoInfoPostquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.enums.VideoStatusEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.VideoInfoPostMapper;
import com.easyjava.service.AiAuditService;
import com.easyjava.service.VideoInfoFilePostService;
import com.easyjava.service.VideoInfoPostService;
import com.easyjava.utlis.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/video")
public class VideoAuditController extends ABaseController {

	@Resource
	private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostquery> videoInfoPostMapper;

	@Resource
	private AiAuditService aiAuditService;

	@Resource
	private VideoInfoPostService videoInfoPostService;

	@Resource
	private VideoInfoFilePostService videoInfoFilePostService;

	@RequestMapping("/audit")
	public ResponseVO auditVideo(@NotEmpty String videoId) throws BusinessException {
		VideoInfoPost video = videoInfoPostMapper.selectByVideoId(videoId);
		if (video == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		if (!VideoStatusEnum.STATUS2.getStatus().equals(video.getStatus())) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		AiAuditResult result = aiAuditService.auditVideo(video);

		video.setAiAuditTime(new Date());
		video.setAiAuditResult(result.toString());

		if (result.getPassed()) {
			video.setAiAuditStatus(1);
			video.setStatus(VideoStatusEnum.STATUS3.getStatus());
		} else {
			video.setAiAuditStatus(2);
			video.setAiAuditReason(result.getReason());
			video.setStatus(VideoStatusEnum.STATUS5.getStatus());
		}

		videoInfoPostMapper.updateByVideoId(video, videoId);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/review/list")
	public ResponseVO getReviewList(VideoInfoPostquery query) throws BusinessException {
		query.setStatusArray(new Integer[]{
				VideoStatusEnum.STATUS2.getStatus(),
				VideoStatusEnum.STATUS3.getStatus(),
				VideoStatusEnum.STATUS4.getStatus(),
				VideoStatusEnum.STATUS5.getStatus()
		});
		query.setOrderBy("v.create_time desc");
		if (query.getPageNo() == null || query.getPageNo() < 1) {
			query.setPageNo(1);
		}
		if (query.getPageSize() == null || query.getPageSize() < 1) {
			query.setPageSize(10);
		}
		PaginationResultVO<VideoInfoPost> result = videoInfoPostService.findListByPage(query);
		return getSuccessResponseVO(result);
	}

	@RequestMapping("/review")
	public ResponseVO reviewVideo(@NotEmpty String videoId, @NotEmpty String action, String reason) throws BusinessException {
		VideoInfoPost video = videoInfoPostMapper.selectByVideoId(videoId);
		if (video == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		Integer currentStatus = video.getStatus();
		if (!VideoStatusEnum.STATUS2.getStatus().equals(currentStatus)
				&& !VideoStatusEnum.STATUS5.getStatus().equals(currentStatus)) {
			throw new BusinessException("该视频当前状态不允许审核");
		}

		if ("reject".equals(action) && StringTools.isEmpty(reason)) {
			throw new BusinessException("拒绝原因不能为空");
		}

		video.setReviewTime(new Date());

		if ("pass".equals(action)) {
			video.setReviewStatus(1);
			video.setReviewReason(null);
			video.setStatus(VideoStatusEnum.STATUS3.getStatus());
		} else if ("reject".equals(action)) {
			video.setReviewStatus(2);
			video.setReviewReason(reason);
			video.setStatus(VideoStatusEnum.STATUS4.getStatus());
		} else {
			throw new BusinessException("action参数错误");
		}

		videoInfoPostMapper.updateByVideoId(video, videoId);
		if ("pass".equals(action)) {
			videoInfoPostService.transferToVideoInfo(videoId);
		}
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/review/detail")
	public ResponseVO getReviewDetail(@NotEmpty String videoId) throws BusinessException {
		VideoInfoPostquery query = new VideoInfoPostquery();
		query.setVideoId(videoId);
		query.setQueryUserInfo(true);
		List<VideoInfoPost> list = videoInfoPostMapper.selectList(query);
		if (list == null || list.isEmpty()) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		VideoInfoPost video = list.get(0);

		VideoInfoFilePostquery fileQuery = new VideoInfoFilePostquery();
		fileQuery.setVideoId(videoId);
		fileQuery.setOrderBy("file_index asc");
		List<VideoInfoFilePost> fileList = videoInfoFilePostService.findListByParam(fileQuery);
		String playFileId = null;
		if (fileList != null && !fileList.isEmpty()) {
			playFileId = fileList.get(0).getFileId();
		}

		Map<String, Object> result = new HashMap<>();
		result.put("video", video);
		result.put("playFileId", playFileId);
		return getSuccessResponseVO(result);
	}

	@RequestMapping("/stats")
	public ResponseVO getAuditStats() {
		VideoInfoPostquery query = new VideoInfoPostquery();
		
		// 待审核数量
		query.setStatus(VideoStatusEnum.STATUS2.getStatus());
		Integer pendingCount = videoInfoPostMapper.selectCount(query);
		
		// 审核通过数量
		query.setStatus(VideoStatusEnum.STATUS3.getStatus());
		Integer passedCount = videoInfoPostMapper.selectCount(query);
		
		// 审核不通过数量
		query.setStatus(VideoStatusEnum.STATUS4.getStatus());
		Integer rejectedCount = videoInfoPostMapper.selectCount(query);
		
		// 待人工复核数量
		query.setStatus(VideoStatusEnum.STATUS5.getStatus());
		Integer reviewCount = videoInfoPostMapper.selectCount(query);
		
		java.util.Map<String, Integer> stats = new java.util.HashMap<>();
		stats.put("pending", pendingCount);
		stats.put("passed", passedCount);
		stats.put("rejected", rejectedCount);
		stats.put("review", reviewCount);
		
		return getSuccessResponseVO(stats);
	}
}
