package com.easyjava.service.impl;

import com.easyjava.entity.query.SimplePage;
import com.easyjava.enums.PageSize;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.query.VideoInfoFilePostquery;import com.easyjava.mappers.VideoInfoFilePostMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easyjava.service.VideoInfoFilePostService;
/**
 * @Description:视频文件信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */
@Service("videoInfoFilePostservice")
public class VideoInfoFilePostServiceImpl implements VideoInfoFilePostService{

	@Resource
	private VideoInfoFilePostMapper<VideoInfoFilePost,VideoInfoFilePostquery> videoInfoFilePostMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<VideoInfoFilePost> findListByParam(VideoInfoFilePostquery query){
		return this.videoInfoFilePostMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoInfoFilePostquery query){
		return this.videoInfoFilePostMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoInfoFilePost> findListByPage(VideoInfoFilePostquery query){
		Integer count=this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null ? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page= new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoInfoFilePost> List=this.findListByParam(query);
		PaginationResultVO<VideoInfoFilePost> result =new PaginationResultVO(count,page.getPageNo(),page.getPageSize(),page.getPageTotal(),List);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoInfoFilePost bean){
		return this.videoInfoFilePostMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoInfoFilePost> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.videoInfoFilePostMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoInfoFilePost> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.videoInfoFilePostMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据FileId查询
	 */
	public VideoInfoFilePost getVideoInfoFilePostByFileId(String fileId){
		return this.videoInfoFilePostMapper.selectByFileId(fileId);
	}

	/**
	 * 根据FileId更新
	 */
	public Integer updateVideoInfoFilePostByFileId(VideoInfoFilePost bean, String fileId){
		return this.videoInfoFilePostMapper.updateByFileId(bean,fileId);
	}

	/**
	 * 根据FileId删除
	 */
	public Integer deleteVideoInfoFilePostByFileId(String fileId){
		return this.videoInfoFilePostMapper.deleteByFileId(fileId);
	}
	/**
	 * 根据UploadIdAnd, UserId查询
	 */
	public VideoInfoFilePost getVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId){
		return this.videoInfoFilePostMapper.selectByUploadIdAndUserId(uploadId,userId);
	}

	/**
	 * 根据UploadIdAnd, UserId更新
	 */
	public Integer updateVideoInfoFilePostByUploadIdAndUserId(VideoInfoFilePost bean, String uploadId,String userId){
		return this.videoInfoFilePostMapper.updateByUploadIdAndUserId(bean,uploadId,userId);
	}

	/**
	 * 根据UploadIdAnd, UserId删除
	 */
	public Integer deleteVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId){
		return this.videoInfoFilePostMapper.deleteByUploadIdAndUserId(uploadId,userId);
	}
}