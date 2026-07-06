package com.easyjava.mappers;

import com.easyjava.entity.query.VideoDanmUquery;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:视频弹幕Mapper
 */
public interface VideoDanmuMapper<T, P> extends BaseMapper {
    
    List<T> selectListByVideoId(@Param("bean") VideoDanmUquery query);
    
    List<T> selectListByFileId(@Param("bean") VideoDanmUquery query);

    List<T> selectListByUserId(@Param("bean") VideoDanmUquery query);

    Integer selectCountByUserId(@Param("userId") String userId, @Param("content") String content);

    Integer deleteById(@Param("danmuId") Integer danmuId);
    
    Integer deleteByVideoId(@Param("videoId") String videoId);

    List<T> selectAdminList(@Param("bean") VideoDanmUquery query);

    Integer selectAdminCount(@Param("bean") VideoDanmUquery query);

    Integer updateIsShow(@Param("danmuId") Integer danmuId, @Param("isShow") Integer isShow);
}