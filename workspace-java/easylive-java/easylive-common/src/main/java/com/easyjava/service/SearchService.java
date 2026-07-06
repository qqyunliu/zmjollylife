package com.easyjava.service;

import com.easyjava.entity.po.SearchKeyword;
import java.util.List;

/**
 * @Description:搜索Service接口
 */
public interface SearchService {
    
    List<SearchKeyword> getHotKeywordList();
    
    void recordSearchKeyword(String keyword);
}