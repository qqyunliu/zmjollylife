package com.easyjava.service.impl;

import com.easyjava.entity.query.SimplePage;
import com.easyjava.enums.PageSize;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.VideoInfoFile;
import com.easyjava.entity.query.VideoInfoFilequery;import com.easyjava.mappers.VideoInfoFileMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.easyjava.service.VideoInfoFileService;
/**
 * @Description:视频文件信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */
@Service("videoInfoFileservice")
public class VideoInfoFileServiceImpl implements VideoInfoFileService{

	@Resource
	private VideoInfoFileMapper<VideoInfoFile,VideoInfoFilequery> videoInfoFileMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<VideoInfoFile> findListByParam(VideoInfoFilequery query){
		return this.videoInfoFileMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoInfoFilequery query){
		return this.videoInfoFileMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoInfoFile> findListByPage(VideoInfoFilequery query){
		Integer count=this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null ? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page= new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoInfoFile> List=this.findListByParam(query);
		PaginationResultVO<VideoInfoFile> result =new PaginationResultVO(count,page.getPageNo(),page.getPageSize(),page.getPageTotal(),List);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoInfoFile bean){
		return this.videoInfoFileMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoInfoFile> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.videoInfoFileMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoInfoFile> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.videoInfoFileMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据FileId查询
	 */
	public VideoInfoFile getVideoInfoFileByFileId(String fileId){
		return this.videoInfoFileMapper.selectByFileId(fileId);
	}

	/**
	 * 根据FileId更新
	 */
	public Integer updateVideoInfoFileByFileId(VideoInfoFile bean, String fileId){
		return this.videoInfoFileMapper.updateByFileId(bean,fileId);
	}

	/**
	 * 根据FileId删除
	 */
	public Integer deleteVideoInfoFileByFileId(String fileId){
		return this.videoInfoFileMapper.deleteByFileId(fileId);
	}
}