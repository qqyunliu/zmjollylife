package com.easyjava.service.impl;

import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.po.*;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.entity.query.VideoInfoFilequery;
import com.easyjava.entity.query.VideoInfoquery;
import com.easyjava.enums.PageSize;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.*;
import com.easyjava.service.VideoInfoService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * @Description:视频信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */
@Service("videoInfoservice")
public class VideoInfoServiceImpl implements VideoInfoService{

	private static final Logger logger = LoggerFactory.getLogger(VideoInfoServiceImpl.class);

	@Resource
	private VideoInfoMapper<VideoInfo,VideoInfoquery> videoInfoMapper;

	@Resource
	private VideoInfoFileMapper<VideoInfoFile, VideoInfoFilequery> videoInfoFileMapper;

	@Resource
	private VideoCommentMapper<VideoComment, Object> videoCommentMapper;

	@Resource
	private VideoDanmuMapper<VideoDanmu, Object> videoDanmuMapper;

	@Resource
	private UserActionMapper<UserAction, Object> userActionMapper;

	@Resource
	private PlayHistoryMapper<PlayHistory, Object> playHistoryMapper;

	@Resource
	private VideoSeriesVideoMapper<VideoSeriesVideo, Object> videoSeriesVideoMapper;

	@Resource
	private VideoInfoPostMapper<com.easyjava.entity.po.VideoInfoPost, Object> videoInfoPostMapper;

	@Resource
	private VideoInfoFilePostMapper<VideoInfoFilePost, Object> videoInfoFilePostMapper;

	@Resource
	private AppConfig appConfig;

	/**
	 * 根据条件查询列表
	 */
	public List<VideoInfo> findListByParam(VideoInfoquery query){
		return this.videoInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoInfoquery query){
		return this.videoInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoInfo> findListByPage(VideoInfoquery query){
		Integer count=this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null ? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page= new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoInfo> List=this.findListByParam(query);
		PaginationResultVO<VideoInfo> result =new PaginationResultVO(count,page.getPageNo(),page.getPageSize(),page.getPageTotal(),List);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoInfo bean){
		return this.videoInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoInfo> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.videoInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoInfo> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.videoInfoMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据VideoId查询
	 */
	public VideoInfo getVideoInfoByVideoId(String videoId){
		return this.videoInfoMapper.selectByVideoId(videoId);
	}

	/**
	 * 根据VideoId更新
	 */
	public Integer updateVideoInfoByVideoId(VideoInfo bean, String videoId){
		return this.videoInfoMapper.updateByVideoId(bean,videoId);
	}

	/**
	 * 根据VideoId删除
	 */
	public Integer deleteVideoInfoByVideoId(String videoId){
		return this.videoInfoMapper.deleteByVideoId(videoId);
	}

	/**
	 * 搜索视频
	 */
	@Override
	public List<VideoInfo> searchVideo(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return new ArrayList<>();
		}
		VideoInfoquery query = new VideoInfoquery();
		query.setVideoNameFuzzy(keyword);
		query.setOrderBy("create_time desc");
		query.setPageNo(1);
		query.setPageSize(50);
		PaginationResultVO<VideoInfo> result = findListByPage(query);
		return result != null ? result.getList() : new ArrayList<>();
	}

	@Override
	public Integer getTotalPlayCountByUserId(String userId) {
		if (userId == null || userId.trim().isEmpty()) {
			return 0;
		}
		Integer count = videoInfoMapper.sumPlayCountByUserId(userId);
		return count != null ? count : 0;
	}

	@Override
	public void incrementPlayCountByVideoId(String videoId) {
		if (videoId == null || videoId.trim().isEmpty()) {
			return;
		}
		videoInfoMapper.incrementPlayCountByVideoId(videoId);
	}

	/**
	 * 管理员删除视频（不通知用户）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteVideoByAdmin(String videoId) throws BusinessException {
		if (videoId == null || videoId.trim().isEmpty()) {
			throw new BusinessException("视频ID不能为空");
		}

		VideoInfo video = videoInfoMapper.selectByVideoId(videoId);
		if (video == null) {
			throw new BusinessException("视频不存在");
		}

		// 删除视频文件
		deleteVideoFiles(videoId);

		// 删除关联数据
		videoCommentMapper.deleteByVideoId(videoId);
		videoDanmuMapper.deleteByVideoId(videoId);
		userActionMapper.deleteByVideoId(videoId);
		playHistoryMapper.deleteByVideoId(videoId);
		videoSeriesVideoMapper.deleteByVideoId(videoId);

		// 删除视频记录
		videoInfoMapper.deleteByVideoId(videoId);

		// 同时删除投稿表中的记录和相关文件（这样用户端也看不到了）
		deleteVideoPostFiles(videoId);

		logger.info("管理员删除了视频，videoId: {}", videoId);
	}

	private void deleteVideoFiles(String videoId) {
		try {
			VideoInfoFilequery fileQuery = new VideoInfoFilequery();
			fileQuery.setVideoId(videoId);
			List<VideoInfoFile> files = videoInfoFileMapper.selectList(fileQuery);
			if (files != null) {
				for (VideoInfoFile file : files) {
					if (file.getFilePath() != null && !file.getFilePath().isEmpty()) {
						File folder = new File(appConfig.getProjectFolder() + Constans.FILE_FOLDER + file.getFilePath());
						if (folder.exists()) {
							FileUtils.deleteDirectory(folder);
							logger.info("删除视频文件夹: {}", folder.getAbsolutePath());
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("删除视频文件失败，videoId: {}", videoId, e);
		}
		videoInfoFileMapper.deleteByVideoId(videoId);
	}

	/**
	 * 删除投稿表中的视频记录和文件（用户端看不到）
	 */
	private void deleteVideoPostFiles(String videoId) {
		try {
			// 查询投稿表的文件
			com.easyjava.entity.query.VideoInfoFilePostquery filePostQuery = new com.easyjava.entity.query.VideoInfoFilePostquery();
			filePostQuery.setVideoId(videoId);
			List<com.easyjava.entity.po.VideoInfoFilePost> postFiles = videoInfoFilePostMapper.selectList(filePostQuery);

			if (postFiles != null) {
				for (com.easyjava.entity.po.VideoInfoFilePost file : postFiles) {
					if (file.getFilePath() != null && !file.getFilePath().isEmpty()) {
						File folder = new File(appConfig.getProjectFolder() + Constans.FILE_FOLDER + file.getFilePath());
						if (folder.exists()) {
							FileUtils.deleteDirectory(folder);
							logger.info("删除投稿视频文件夹: {}", folder.getAbsolutePath());
						}
					}
				}
			}
			// 删除投稿文件记录
			videoInfoFilePostMapper.deleteByVideoId(videoId);
		} catch (IOException e) {
			logger.error("删除投稿视频文件失败，videoId: {}", videoId, e);
		}
		// 删除投稿表记录
		videoInfoPostMapper.deleteByVideoId(videoId);
	}
}
