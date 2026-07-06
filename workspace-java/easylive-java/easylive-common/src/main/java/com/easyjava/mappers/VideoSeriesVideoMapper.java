package com.easyjava.mappers;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:系列视频关联Mapper
 */
public interface VideoSeriesVideoMapper<T, P> extends BaseMapper {
    
    List<T> selectListBySeriesId(@Param("seriesId") Integer seriesId);
    
    Integer deleteBySeriesIdAndVideoId(@Param("seriesId") Integer seriesId, @Param("videoId") String videoId);
    
    Integer deleteBySeriesId(@Param("seriesId") Integer seriesId);

    Integer deleteByVideoId(@Param("videoId") String videoId);
    
    Integer updateSort(@Param("seriesId") Integer seriesId, @Param("videoId") String videoId, @Param("sort") Integer sort);
}
