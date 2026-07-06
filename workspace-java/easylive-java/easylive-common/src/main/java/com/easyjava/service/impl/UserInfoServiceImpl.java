package com.easyjava.service.impl;

import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoquery;
import com.easyjava.mappers.UserInfoMapper;
import com.easyjava.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:用户信息Service实现
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoquery> userInfoMapper;

    @Override
    public List<UserInfo> findListByParam(UserInfoquery query) {
        return userInfoMapper.selectList(query);
    }

    @Override
    public void updateUserInfoByUserId(UserInfo userInfo, String userId) {
        userInfoMapper.updateByUserId(userInfo, userId);
    }

    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return userInfoMapper.selectByUserId(userId);
    }
}
