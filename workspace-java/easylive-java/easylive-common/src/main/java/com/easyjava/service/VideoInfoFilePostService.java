package com.easyjava.service;

import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.query.VideoInfoFilePostquery;
import java.util.List;
/**
 * @Description:视频文件信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */public interface VideoInfoFilePostService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfoFilePost> findListByParam(VideoInfoFilePostquery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoInfoFilePostquery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfoFilePost> findListByPage(VideoInfoFilePostquery query);

	/**
	 * 新增
	 */
	Integer add(VideoInfoFilePost Bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfoFilePost> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoInfoFilePost> listBean);

	/**
	 * 根据FileId查询
	 */
	VideoInfoFilePost getVideoInfoFilePostByFileId(String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateVideoInfoFilePostByFileId(VideoInfoFilePost bean, String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteVideoInfoFilePostByFileId(String fileId);

	/**
	 * 根据UploadIdAnd, UserId查询
	 */
	VideoInfoFilePost getVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId);

	/**
	 * 根据UploadIdAnd, UserId更新
	 */
	Integer updateVideoInfoFilePostByUploadIdAndUserId(VideoInfoFilePost bean, String uploadId,String userId);

	/**
	 * 根据UploadIdAnd, UserId删除
	 */
	Integer deleteVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId);


}