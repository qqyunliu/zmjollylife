package com.easyjava.mappers;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description:用户信息Mapper
 * @auther:哈哈哈
 * @date:2025/07/31
 */
public interface InfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据UserId查询
	 */
	T selectByUserId(@Param("userId") String userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteByUserId(@Param("userId") String userId);

	/**
	 * 根据Email查询
	 */
	T selectByEmail(@Param("email") String email);

	/**
	 * 根据Email更新
	 */
	Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteByEmail(@Param("email") String email);

	/**
	 * 根据NickId查询
	 */
	T selectByNickId(@Param("nickId") String nickId);

	/**
	 * 根据NickId更新
	 */
	Integer updateByNickId(@Param("bean") T t, @Param("nickId") String nickId);

	/**
	 * 根据NickId删除
	 */
	Integer deleteByNickId(@Param("nickId") String nickId);

	List<Map<String, Object>> selectJoinCountByDateRange(@Param("startDate") String startDate,
	                                                    @Param("endDate") String endDate);

	Integer selectTotalUserCountBefore(@Param("endDateExclusive") String endDateExclusive);

}
