package com.easyjava.mappers;

import com.easyjava.entity.po.VideoSeries;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:视频系列Mapper
 */
public interface VideoSeriesMapper<T, P> extends BaseMapper {
    
    List<T> selectListByUserId(@Param("userId") String userId);
    
    T selectById(@Param("seriesId") Integer seriesId);
    
    Integer deleteById(@Param("seriesId") Integer seriesId);
    
    Integer update(VideoSeries videoSeries);
}