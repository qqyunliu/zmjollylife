package com.easyjava.mappers;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:搜索关键词Mapper
 */
public interface SearchKeywordMapper<T, P> extends BaseMapper {
    
    List<T> selectTopKeyword(@Param("limit") Integer limit);
    
    T selectByKeyword(@Param("keyword") String keyword);
    
    Integer updateSearchCount(@Param("keyword") String keyword);
}