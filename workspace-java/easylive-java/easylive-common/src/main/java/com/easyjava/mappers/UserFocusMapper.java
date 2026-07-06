package com.easyjava.mappers;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:用户关注Mapper
 */
public interface UserFocusMapper<T, P> extends BaseMapper {
    
    List<T> selectListByUserId(@Param("userId") String userId);
    
    List<T> selectListByFocusUserId(@Param("focusUserId") String focusUserId);
    
    T selectByUserIdAndFocusUserId(@Param("userId") String userId, @Param("focusUserId") String focusUserId);
    
    Integer deleteByUserIdAndFocusUserId(@Param("userId") String userId, @Param("focusUserId") String focusUserId);
    
    Integer selectCountByUserId(@Param("userId") String userId);
    
    Integer selectCountByFocusUserId(@Param("focusUserId") String focusUserId);
}