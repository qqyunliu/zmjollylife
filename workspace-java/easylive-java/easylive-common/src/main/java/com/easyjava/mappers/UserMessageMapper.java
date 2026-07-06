package com.easyjava.mappers;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:用户消息Mapper
 */
public interface UserMessageMapper<T, P> extends BaseMapper {
    
    List<T> selectListByUserId(@Param("userId") String userId);
    
    Integer selectNoReadCount(@Param("userId") String userId);
    
    Integer updateIsRead(@Param("userId") String userId);
    
    Integer deleteById(@Param("messageId") Integer messageId);

    Integer deleteByVideoId(@Param("videoId") String videoId);
}
