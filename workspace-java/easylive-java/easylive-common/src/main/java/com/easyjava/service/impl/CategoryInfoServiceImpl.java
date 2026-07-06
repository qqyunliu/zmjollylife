package com.easyjava.service.impl;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.enums.PageSize;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.CategoryInfo;
import com.easyjava.entity.query.CategoryInfoquery;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.CategoryInfoMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;

import org.apache.http.impl.execchain.TunnelRefusedException;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import com.easyjava.service.CategoryInfoService;
/**
 * @Description:分类信息Service
 * @auther:哈哈哈
 * @date:2025/08/08
 */
@Service("categoryInfoservice")
public class CategoryInfoServiceImpl implements CategoryInfoService{

	@Resource
	private CategoryInfoMapper<CategoryInfo,CategoryInfoquery> categoryInfoMapper;
	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	public List<CategoryInfo> findListByParam(CategoryInfoquery param){
		List<CategoryInfo> categoryInfoList = this.categoryInfoMapper.selectList(param);

		// 临时注释掉，直接返回平铺数据
    /*
    if(param.getConvert2Tree() != null && param.getConvert2Tree()){
        categoryInfoList = convertLine2Tree(categoryInfoList, Constants.ZERO);
    }
    */

		return categoryInfoList;
	}

	private List<CategoryInfo> convertLine2Tree(List<CategoryInfo > dataList,Integer pid){
		List<CategoryInfo> children=new ArrayList<>();
		for(CategoryInfo m:dataList){
			if(m.getCategoryId()!=null && m.getCategoryId()!=null && m.getCategoryId().equals(pid)){
				m.setChildren(convertLine2Tree(dataList,m.getCategoryId()));
				children.add(m);
			}
		}
		return  children;
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(CategoryInfoquery query){
		return this.categoryInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoquery query){
		Integer count=this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null ? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page= new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<CategoryInfo> List=this.findListByParam(query);
		PaginationResultVO<CategoryInfo> result =new PaginationResultVO(count,page.getPageNo(),page.getPageSize(),page.getPageTotal(),List);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(CategoryInfo bean){
		return this.categoryInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<CategoryInfo> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.categoryInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<CategoryInfo> listBean){
		if(listBean==null || listBean.isEmpty()) {
			return 0;
		}
			return this.categoryInfoMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据CategoryId查询
	 */
	public CategoryInfo getCategoryInfoByCategoryId(Integer categoryId){
		return this.categoryInfoMapper.selectByCategoryId(categoryId);
	}

	/**
	 * 根据CategoryId更新
	 */
	public Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId){
		return this.categoryInfoMapper.updateByCategoryId(bean,categoryId);
	}

	/**
	 * 根据CategoryId删除
	 */
	public Integer deleteCategoryInfoByCategoryId(Integer categoryId){
		return this.categoryInfoMapper.deleteByCategoryId(categoryId);
	}
	/**
	 * 根据CategoryCode查询
	 */
	public CategoryInfo getCategoryInfoByCategoryCode(String categoryCode){
		return this.categoryInfoMapper.selectByCategoryCode(categoryCode);
	}

	/**
	 * 根据CategoryCode更新
	 */
	public Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode){
		return this.categoryInfoMapper.updateByCategoryCode(bean,categoryCode);
	}

	/**
	 * 根据CategoryCode删除
	 */
	public Integer deleteCategoryInfoByCategoryCode(String categoryCode){
		return this.categoryInfoMapper.deleteByCategoryCode(categoryCode);
	}

	@Override
	public void saveCategoryInfo(CategoryInfo bean) throws BusinessException {
		CategoryInfo dbBean=this.categoryInfoMapper.selectByCategoryCode(bean.getCategoryCode());
		if(bean.getCategoryId()==null && dbBean!=null|| bean.getCategoryId() != null && dbBean !=null && !bean.getCategoryId().equals(dbBean.getCategoryCode())){
			throw  new BusinessException("分类编号已经存在");
		}
		if(bean.getCategoryId()==null){
			Integer maxSort=this.categoryInfoMapper.selectMaxSort(bean.getPCategoryId());
			bean.setSort(maxSort+1);
			this.categoryInfoMapper.insert(bean);
		}else {
			this.categoryInfoMapper.updateByCategoryId(bean,bean.getCategoryId());
		}
		save2Redis();
	}

	@Override
	public void delCategoryInfo(Integer categoryId) {
		//TODO查询分类下是否有视频
		CategoryInfoquery categoryInfoquery=new CategoryInfoquery();
		categoryInfoquery.getCategoryIdOrPCategoryId(categoryId);
		categoryInfoMapper.deleteByParam(categoryInfoquery);

		//刷新缓存
		save2Redis();
	}

	@Override
	public void changeSort(Integer pcategoryId, String categoryIds) {
		String[] categoryIdArray =categoryIds.split(",");
		List<CategoryInfo> categoryInfoList=new ArrayList<>();
		Integer sort =0;
		for(String categoryId : categoryIdArray){
			CategoryInfo categoryInfo=new CategoryInfo();
			categoryInfo.setCategoryId(Integer.parseInt(categoryId));
			categoryInfo.setPCategoryId(pcategoryId);
			categoryInfo.setSort(++sort);
			categoryInfoList.add(categoryInfo);

		}
		categoryInfoMapper.updateSortBatch(categoryInfoList);
		//刷新缓存
		save2Redis();
	}
	private void save2Redis(){
		CategoryInfoquery query=new CategoryInfoquery();
		query.setOrderBy("sort asc");
		query.setConvert2Tree(true);
		List<CategoryInfo> categoryInfoList=findListByParam(query);
		redisComponent.saveCategoryList(categoryInfoList);
	}

	@Override
	public List<CategoryInfo> getCategoryList() {
		// 1. 从Redis获取数据
		List<CategoryInfo> categoryInfoList = redisComponent.getCategoryList();

		// 2. 判断是否为null或空
		if (categoryInfoList == null || categoryInfoList.isEmpty()) {
			save2Redis(); // 重新加载数据到Redis
			categoryInfoList = redisComponent.getCategoryList(); // 再次获取
		}

		// 3. 最终返回（确保非null）
		return categoryInfoList != null ? categoryInfoList : Collections.emptyList();
	}

}