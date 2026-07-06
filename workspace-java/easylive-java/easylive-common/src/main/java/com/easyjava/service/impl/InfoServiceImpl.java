package com.easyjava.service.impl;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.enums.PageSize;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.Info;
import com.easyjava.entity.query.Infoquery;
import com.easyjava.enums.UserSexENUM;
import com.easyjava.enums.UserStatusEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.InfoMapper;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.easyjava.utlis.CopyTools;
import com.easyjava.utlis.StringTools;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import com.easyjava.service.InfoService;

/**
 * @Description:用户信息Service
 * @auther:哈哈哈
 * @date:2025/07/31
 */
@Service("infoservice")
public class InfoServiceImpl implements InfoService {

    @Resource
    private InfoMapper<Info, Infoquery> infoMapper;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 根据条件查询列表
     */
    public List<Info> findListByParam(Infoquery query) {
        return this.infoMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    public Integer findCountByParam(Infoquery query) {
        return this.infoMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    public PaginationResultVO<Info> findListByPage(Infoquery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<Info> List = this.findListByParam(query);
        PaginationResultVO<Info> result = new PaginationResultVO(count, page.getPageNo(), page.getPageSize(), page.getPageTotal(), List);
        return result;
    }

    /**
     * 新增
     */
    public Integer add(Info bean) {
        return this.infoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    public Integer addBatch(List<Info> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.infoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    public Integer addOrUpdateBatch(List<Info> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.infoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据UserId查询
     */
    public Info getInfoByUserId(String userId) {
        return this.infoMapper.selectByUserId(userId);
    }

    /**
     * 根据UserId更新
     */
    public Integer updateInfoByUserId(Info bean, String userId) {
        return this.infoMapper.updateByUserId(bean, userId);
    }

    /**
     * 根据UserId删除
     */
    public Integer deleteInfoByUserId(String userId) {
        return this.infoMapper.deleteByUserId(userId);
    }

    /**
     * 根据Email查询
     */
    public Info getInfoByEmail(String email) {
        return this.infoMapper.selectByEmail(email);
    }

    /**
     * 根据Email更新
     */
    public Integer updateInfoByEmail(Info bean, String email) {
        return this.infoMapper.updateByEmail(bean, email);
    }

    /**
     * 根据Email删除
     */
    public Integer deleteInfoByEmail(String email) {
        return this.infoMapper.deleteByEmail(email);
    }

    /**
     * 根据NickId查询
     */
    public Info getInfoByNickId(String nickId) {
        return this.infoMapper.selectByNickId(nickId);
    }

    /**
     * 根据NickId更新
     */
    public Integer updateInfoByNickId(Info bean, String nickId) {
        return this.infoMapper.updateByNickId(bean, nickId);
    }

    /**
     * 根据NickId删除
     */
    public Integer deleteInfoByNickId(String nickId) {
        return this.infoMapper.deleteByNickId(nickId);
    }

    @SneakyThrows
    @Override
    public void register(String email, String nickName, String registerPassword) {

        Info info = this.infoMapper.selectByEmail(email);
        if (info != null) {
            throw new BusinessException("邮箱已经存在");
        }
        Info nickNameUser = this.infoMapper.selectByNickId(nickName);
        if(nickNameUser!=null){
            throw  new BusinessException("昵称已经存在");
        }
        info = new Info();
        String userId= StringTools.getRandomNumber(Constans.LENGTH_10);
        info.setUserId(userId);
        info.setEmail(email);
        info.setNickId(nickName);
        info.setPassword(StringTools.encodeByMd5(registerPassword));
        info.setJoinTime(new Date());
        info.setStatus(UserStatusEnum.ENABLE.getStatus());
        info.setSex(UserSexENUM.UNKNOWN.getType());
        info.setTheme(Constans.ONE);
        //初始化送硬币
        info.setTotalCoinCount(10);
        info.setCurrentCoinCount(10);
        this.infoMapper.insert(info);

    }

    @Override
    public TokenUserInfoDto login(String email, String password, String ip) throws BusinessException {
        Info info = this.infoMapper.selectByEmail(email);
        if(info==null||!info.getPassword().equals(password)){
            throw new BusinessException("账号或密码错误");
        }
        if(!UserStatusEnum.ENABLE.getStatus().equals(info.getStatus())){
            throw new BusinessException("账号已禁用或已注销");
        }
        Info updateInfo=new Info();
        updateInfo.setLastLoginIp(ip);
        updateInfo.setLastLoginTime(new Date());
        this.infoMapper.updateByUserId(updateInfo,info.getUserId());


        TokenUserInfoDto tokenUserInfoDto= CopyTools.copy(info,TokenUserInfoDto.class);
        redisComponent.saveTokenInfo(tokenUserInfoDto);

       return tokenUserInfoDto;
    }
}
