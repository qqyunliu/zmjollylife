package com.easyjava.mappers;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:用户行为Mapper
 */
public interface UserActionMapper<T, P> extends BaseMapper {
    
    T selectByVideoIdAndUserIdAndType(@Param("videoId") String videoId, 
                                       @Param("userId") String userId, 
                                       @Param("actionType") Integer actionType);
    
    Integer deleteByVideoIdAndUserIdAndType(@Param("videoId") String videoId, 
                                             @Param("userId") String userId, 
                                             @Param("actionType") Integer actionType);
    
    Integer selectCountByVideoIdAndType(@Param("videoId") String videoId, 
                                         @Param("actionType") Integer actionType);

    Integer deleteByVideoId(@Param("videoId") String videoId);
    
    List<T> selectListByUserIdAndType(@Param("userId") String userId, 
                                        @Param("actionType") Integer actionType);
}
