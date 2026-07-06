package com.easyjava.service.impl;

import com.easyjava.entity.po.SearchKeyword;
import com.easyjava.entity.query.SearchKeywordquery;
import com.easyjava.mappers.SearchKeywordMapper;
import com.easyjava.service.SearchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:搜索Service实现
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private SearchKeywordMapper<SearchKeyword, SearchKeywordquery> searchKeywordMapper;

    @Override
    public List<SearchKeyword> getHotKeywordList() {
        return searchKeywordMapper.selectTopKeyword(10);
    }

    @Override
    public void recordSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        
        SearchKeyword existing = searchKeywordMapper.selectByKeyword(keyword);
        if (existing != null) {
            searchKeywordMapper.updateSearchCount(keyword);
        } else {
            SearchKeyword newKeyword = new SearchKeyword();
            newKeyword.setKeyword(keyword);
            newKeyword.setSearchCount(1);
            newKeyword.setCreateTime(new Date());
            searchKeywordMapper.insert(newKeyword);
        }
    }
}