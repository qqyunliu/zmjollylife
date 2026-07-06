package com.easyjava.admin.controller;


import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.entity.po.CategoryInfo;
import com.easyjava.entity.query.CategoryInfoquery;
import com.easyjava.service.CategoryInfoService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * @Description:分类信息Controller
 * @auther:哈哈哈
 * @date:2025/08/08
 */
@RestController
@RequestMapping("/categoryInfo")
public class CategoryInfoController extends ABaseController{

	@Resource
	private CategoryInfoService categoryInfoService;

	@RequestMapping("loadDataList")
	public ResponseVO loadDataList (CategoryInfoquery query) {
		return getSuccessResponseVO(categoryInfoService.findListByPage(query));
	}
	/**
	 * 新增
	 */

	@RequestMapping("add")
	public ResponseVO add(CategoryInfo bean){
		this.categoryInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */

	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<CategoryInfo> listBean){
			this.categoryInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增或修改
	 */

	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<CategoryInfo> listBean){
			this.categoryInfoService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}
	/**
	 * 根据CategoryId查询
	 */

	@RequestMapping("getCategoryInfoByCategoryId")
	public ResponseVO getCategoryInfoByCategoryId(Integer categoryId){
		return getSuccessResponseVO(this.categoryInfoService.getCategoryInfoByCategoryId(categoryId));
	}

	/**
	 * 根据CategoryId更新
	 */

	@RequestMapping("updateCategoryInfoByCategoryId")
	public ResponseVO updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId){
		this.categoryInfoService.updateCategoryInfoByCategoryId(bean,categoryId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryId删除
	 */

	@RequestMapping("deleteCategoryInfoByCategoryId")
	public ResponseVO deleteCategoryInfoByCategoryId(Integer categoryId){
		this.categoryInfoService.deleteCategoryInfoByCategoryId(categoryId);
		return getSuccessResponseVO(null);
	}
	/**
	 * 根据CategoryCode查询
	 */

	@RequestMapping("getCategoryInfoByCategoryCode")
	public ResponseVO getCategoryInfoByCategoryCode(String categoryCode){
		return getSuccessResponseVO(this.categoryInfoService.getCategoryInfoByCategoryCode(categoryCode));
	}

	/**
	 * 根据CategoryCode更新
	 */

	@RequestMapping("updateCategoryInfoByCategoryCode")
	public ResponseVO updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode){
		this.categoryInfoService.updateCategoryInfoByCategoryCode(bean,categoryCode);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryCode删除
	 */

	@RequestMapping("deleteCategoryInfoByCategoryCode")
	public ResponseVO deleteCategoryInfoByCategoryCode(String categoryCode){
		this.categoryInfoService.deleteCategoryInfoByCategoryCode(categoryCode);
		return getSuccessResponseVO(null);
	}
}