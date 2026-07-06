package com.easyjava.mappers;


import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.query.VideoInfoFilePostquery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:视频文件信息Mapper
 * @auther:哈哈哈
 * @date:2025/08/16
 */
public interface VideoInfoFilePostMapper<T, P> extends BaseMapper {
	/**
	 * 根据FileId查询
	 */
	T selectByFileId(@Param("fileId") String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateByFileId(@Param("bean") T t, @Param("fileId") String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteByFileId(@Param("fileId") String fileId);

	/**
	 * 根据UploadIdAnd, UserId查询
	 */
	T selectByUploadIdAndUserId(@Param("uploadId") String uploadId,@Param("userId") String userId);

	/**
	 * 根据UploadIdAnd, UserId更新
	 */
	Integer updateByUploadIdAndUserId(@Param("bean") T t, @Param("uploadId") String uploadId,@Param("userId") String userId);

	/**
	 * 根据UploadIdAnd, UserId删除
	 */
	Integer deleteByUploadIdAndUserId(@Param("uploadId") String uploadId,@Param("userId") String userId);

	Integer deleteByVideoIdAndUserId(@Param("videoId") String videoId, @Param("userId") String userId);

	Integer deleteByVideoId(@Param("videoId") String videoId);

	void deleteBatchByFileId(@Param("fileIfdList")List<String> fileIdList,@Param("userId") String userId);


    Integer sumDuration(@Param("videoId") String videoId);


//没有xml
	void updateByParam(VideoInfoFilePost videoInfoPost, VideoInfoFilePostquery filePostQuery);
}
