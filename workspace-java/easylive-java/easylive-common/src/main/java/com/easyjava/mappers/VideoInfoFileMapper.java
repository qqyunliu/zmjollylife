package com.easyjava.mappers;


import org.apache.ibatis.annotations.Param;

/**
 * @Description:视频文件信息Mapper
 * @auther:哈哈哈
 * @date:2025/08/16
 */
public interface VideoInfoFileMapper<T, P> extends BaseMapper {
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
	 * 根据VideoId删除
	 */
	Integer deleteByVideoId(@Param("videoId") String videoId);


	void deleteByParam(@Param("query") P videoInfoFileQuery);
}
