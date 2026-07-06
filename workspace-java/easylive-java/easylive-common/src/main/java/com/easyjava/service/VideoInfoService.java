package com.easyjava.service;

import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.query.VideoInfoquery;
import com.easyjava.exception.BusinessException;

import java.util.List;
/**
 * @Description:视频信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */public interface VideoInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfo> findListByParam(VideoInfoquery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoInfoquery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfo> findListByPage(VideoInfoquery query);

	/**
	 * 新增
	 */
	Integer add(VideoInfo Bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoInfo> listBean);

	/**
	 * 根据VideoId查询
	 */
	VideoInfo getVideoInfoByVideoId(String videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateVideoInfoByVideoId(VideoInfo bean, String videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteVideoInfoByVideoId(String videoId);

	/**
	 * 搜索视频
	 */
	List<VideoInfo> searchVideo(String keyword);

	Integer getTotalPlayCountByUserId(String userId);

	void incrementPlayCountByVideoId(String videoId);

	/**
	 * 管理员删除视频（不通知用户）
	 */
	void deleteVideoByAdmin(String videoId) throws BusinessException;

}
