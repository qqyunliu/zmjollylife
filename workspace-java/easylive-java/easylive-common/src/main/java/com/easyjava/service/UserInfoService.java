package com.easyjava.service;

import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoquery;
import java.util.List;

/**
 * @Description:用户信息Service接口
 */
public interface UserInfoService {
    
    List<UserInfo> findListByParam(UserInfoquery query);
    
    void updateUserInfoByUserId(UserInfo userInfo, String userId);
    
    UserInfo getUserInfoByUserId(String userId);
}
