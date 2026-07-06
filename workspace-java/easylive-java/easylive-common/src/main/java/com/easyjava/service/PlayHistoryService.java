package com.easyjava.service;

import com.easyjava.entity.po.PlayHistory;
import com.easyjava.exception.BusinessException;
import java.util.List;

/**
 * @Description:播放历史Service接口
 */
public interface PlayHistoryService {
    
    void savePlayHistory(String userId, String videoId, Integer progress);
    
    List<PlayHistory> getPlayHistoryList(String userId);
    
    void deletePlayHistory(Integer id);
    
    void cleanPlayHistory(String userId);
    
    Integer getPlayProgress(String userId, String videoId);
}
