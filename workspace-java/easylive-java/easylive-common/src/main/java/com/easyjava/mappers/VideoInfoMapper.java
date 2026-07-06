package com.easyjava.mappers;


import org.apache.ibatis.annotations.Param;

/**
 * @Description:视频信息Mapper
 * @auther:哈哈哈
 * @date:2025/08/16
 */
public interface VideoInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据VideoId查询
	 */
	T selectByVideoId(@Param("videoId") String videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateByVideoId(@Param("bean") T t, @Param("videoId") String videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteByVideoId(@Param("videoId") String videoId);

	Integer sumPlayCountByUserId(@Param("userId") String userId);

	Integer sumPlayCountAll();

	Integer sumLikeCountAll();

	Integer incrementPlayCountByVideoId(@Param("videoId") String videoId);


}
