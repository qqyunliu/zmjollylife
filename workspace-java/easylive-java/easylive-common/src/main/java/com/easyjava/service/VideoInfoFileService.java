package com.easyjava.service;

import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.VideoInfoFile;
import com.easyjava.entity.query.VideoInfoFilequery;
import java.util.List;
/**
 * @Description:视频文件信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */public interface VideoInfoFileService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfoFile> findListByParam(VideoInfoFilequery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoInfoFilequery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfoFile> findListByPage(VideoInfoFilequery query);

	/**
	 * 新增
	 */
	Integer add(VideoInfoFile Bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfoFile> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoInfoFile> listBean);

	/**
	 * 根据FileId查询
	 */
	VideoInfoFile getVideoInfoFileByFileId(String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateVideoInfoFileByFileId(VideoInfoFile bean, String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteVideoInfoFileByFileId(String fileId);


}