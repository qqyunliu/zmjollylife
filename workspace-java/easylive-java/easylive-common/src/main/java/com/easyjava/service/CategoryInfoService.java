package com.easyjava.service;

import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.CategoryInfo;
import com.easyjava.entity.query.CategoryInfoquery;
import com.easyjava.exception.BusinessException;

import java.util.List;
/**
 * @Description:分类信息Service
 * @auther:哈哈哈
 * @date:2025/08/08
 */public interface CategoryInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<CategoryInfo> findListByParam(CategoryInfoquery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(CategoryInfoquery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoquery query);

	/**
	 * 新增
	 */
	Integer add(CategoryInfo Bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<CategoryInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<CategoryInfo> listBean);

	/**
	 * 根据CategoryId查询
	 */
	CategoryInfo getCategoryInfoByCategoryId(Integer categoryId);

	/**
	 * 根据CategoryId更新
	 */
	Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId);

	/**
	 * 根据CategoryId删除
	 */
	Integer deleteCategoryInfoByCategoryId(Integer categoryId);

	/**
	 * 根据CategoryCode查询
	 */
	CategoryInfo getCategoryInfoByCategoryCode(String categoryCode);

	/**
	 * 根据CategoryCode更新
	 */
	Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode);

	/**
	 * 根据CategoryCode删除
	 */
	Integer deleteCategoryInfoByCategoryCode(String categoryCode);

	void saveCategoryInfo (CategoryInfo categoryInfo) throws BusinessException;
	void delCategoryInfo (Integer categoryId);
	void changeSort (Integer categoryId,String categoryIds);

	List<CategoryInfo> getCategoryList();

}