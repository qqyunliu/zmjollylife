package com.easyjava.service;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.Info;
import com.easyjava.entity.query.Infoquery;
import com.easyjava.exception.BusinessException;

import java.util.List;
/**
 * @Description:用户信息Service
 * @auther:哈哈哈
 * @date:2025/07/31
 */public interface InfoService{

	/**
	 * 根据条件查询列表
	 */
	List<Info> findListByParam(Infoquery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(Infoquery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Info> findListByPage(Infoquery query);

	/**
	 * 新增
	 */
	Integer add(Info Bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Info> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<Info> listBean);

	/**
	 * 根据UserId查询
	 */
	Info getInfoByUserId(String userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateInfoByUserId(Info bean, String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteInfoByUserId(String userId);

	/**
	 * 根据Email查询
	 */
	Info getInfoByEmail(String email);

	/**
	 * 根据Email更新
	 */
	Integer updateInfoByEmail(Info bean, String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteInfoByEmail(String email);

	/**
	 * 根据NickId查询
	 */
	Info getInfoByNickId(String nickId);

	/**
	 * 根据NickId更新
	 */
	Integer updateInfoByNickId(Info bean, String nickId);

	/**
	 * 根据NickId删除
	 */
	Integer deleteInfoByNickId(String nickId);

	void register (String email,String nickId,String registerPassword);
	TokenUserInfoDto login (String email, String password, String ip) throws BusinessException;


}