package com.easyjava.mappers;

import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoquery;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:用户信息Mapper
 */
public interface UserInfoMapper<T, P> extends BaseMapper {
    
    UserInfo selectByUserId(@Param("userId") String userId);
    
    Integer updateByUserId(@Param("bean") UserInfo userInfo, @Param("userId") String userId);
}
