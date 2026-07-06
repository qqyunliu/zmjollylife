package com.easyjava.mappers;

import com.easyjava.entity.po.PlayHistory;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:播放历史Mapper
 */
public interface PlayHistoryMapper<T, P> extends BaseMapper {
    
    List<T> selectListByUserId(@Param("userId") String userId);
    
    T selectByVideoIdAndUserId(@Param("videoId") String videoId, @Param("userId") String userId);
    
    Integer deleteById(@Param("id") Integer id);
    
    Integer deleteAllByUserId(@Param("userId") String userId);

    Integer deleteByVideoId(@Param("videoId") String videoId);
    
    Integer update(PlayHistory playHistory);
}
