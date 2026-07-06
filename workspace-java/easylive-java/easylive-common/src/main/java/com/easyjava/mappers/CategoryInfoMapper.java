package com.easyjava.mappers;


import com.easyjava.entity.po.CategoryInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:分类信息Mapper
 * @auther:哈哈哈
 * @date:2025/08/08
 */
public interface CategoryInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据CategoryId查询
	 */
	T selectByCategoryId(@Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryId更新
	 */
	Integer updateByCategoryId(@Param("bean") T t, @Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryId删除
	 */
	Integer deleteByCategoryId(@Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryCode查询
	 */
	T selectByCategoryCode(@Param("categoryCode") String categoryCode);

	/**
	 * 根据CategoryCode更新
	 */
	Integer updateByCategoryCode(@Param("bean") T t, @Param("categoryCode") String categoryCode);

	/**
	 * 根据CategoryCode删除
	 */
	Integer deleteByCategoryCode(@Param("categoryCode") String categoryCode);
	//根据categorycode获取对象
	//T selectByCategoryCode(@Param("categoryCode") String categoryCode);




	Integer selectMaxSort(@Param("pCategoryId") Integer pCategoryId);


	void deleteByParam(P categoryInfoquery);
	void updateSortBatch(@Param("categoryInfoList")List<CategoryInfo> categoryInfoList);
}