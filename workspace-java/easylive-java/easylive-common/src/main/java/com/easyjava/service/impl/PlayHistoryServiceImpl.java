package com.easyjava.service.impl;

import com.easyjava.entity.po.PlayHistory;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.mappers.PlayHistoryMapper;
import com.easyjava.mappers.VideoInfoMapper;
import com.easyjava.service.PlayHistoryService;
import com.easyjava.utlis.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:播放历史Service实现
 */
@Service
public class PlayHistoryServiceImpl implements PlayHistoryService {

    @Resource
    private PlayHistoryMapper<PlayHistory, com.easyjava.entity.query.PlayHistoryquery> playHistoryMapper;
    
    @Resource
    private VideoInfoMapper<VideoInfo, com.easyjava.entity.query.VideoInfoquery> videoInfoMapper;

    @Override
    public void savePlayHistory(String userId, String videoId, Integer progress) {
        if (StringTools.isEmpty(userId)) {
            return;
        }
        
        PlayHistory existing = playHistoryMapper.selectByVideoIdAndUserId(videoId, userId);
        if (existing != null) {
            existing.setPlayTime(new Date());
            existing.setProgress(progress);
            playHistoryMapper.update(existing);
        } else {
            PlayHistory history = new PlayHistory();
            history.setVideoId(videoId);
            history.setUserId(userId);
            history.setPlayTime(new Date());
            history.setProgress(progress);
            playHistoryMapper.insert(history);
        }
    }

    @Override
    public List<PlayHistory> getPlayHistoryList(String userId) {
        if (StringTools.isEmpty(userId)) {
            return null;
        }
        return playHistoryMapper.selectListByUserId(userId);
    }

    @Override
    public void deletePlayHistory(Integer id) {
        playHistoryMapper.deleteById(id);
    }

    @Override
    public void cleanPlayHistory(String userId) {
        if (StringTools.isEmpty(userId)) {
            return;
        }
        playHistoryMapper.deleteAllByUserId(userId);
    }

    @Override
    public Integer getPlayProgress(String userId, String videoId) {
        if (StringTools.isEmpty(userId)) {
            return 0;
        }
        PlayHistory history = playHistoryMapper.selectByVideoIdAndUserId(videoId, userId);
        return history != null ? history.getProgress() : 0;
    }
}
