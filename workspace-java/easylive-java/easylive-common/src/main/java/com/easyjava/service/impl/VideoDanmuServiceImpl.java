package com.easyjava.service.impl;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.VideoDanmu;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.query.VideoDanmUquery;
import com.easyjava.mappers.VideoDanmuMapper;
import com.easyjava.service.VideoDanmuService;
import com.easyjava.utlis.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:视频弹幕Service实现
 */
@Service
public class VideoDanmuServiceImpl implements VideoDanmuService {

    @Resource
    private VideoDanmuMapper<VideoDanmu, com.easyjava.entity.query.VideoDanmUquery> videoDanmuMapper;
    
    @Resource
    private RedisComponent redisComponent;

    @Override
    public void saveDanmu(String videoId, String fileId, String content, String color, Integer time, String userId) {
        VideoDanmu danmu = new VideoDanmu();
        danmu.setVideoId(videoId);
        danmu.setFileId(fileId);
        danmu.setUserId(userId);
        danmu.setPostTime(new Date());
        danmu.setContent(content);
        danmu.setIsShow(1);
        danmu.setColor(color == null ? "#ffffff" : color);
        danmu.setTime(time == null ? 0 : time);
        videoDanmuMapper.insert(danmu);
    }

    @Override
    public List<VideoDanmu> getDanmuList(String videoId) {
        VideoDanmUquery query = new VideoDanmUquery();
        query.setVideoId(videoId);
        return videoDanmuMapper.selectListByVideoId(query);
    }

    @Override
    public List<VideoDanmu> getDanmuListByFileId(String fileId) {
        VideoDanmUquery query = new VideoDanmUquery();
        query.setFileId(fileId);
        return videoDanmuMapper.selectListByFileId(query);
    }

    @Override
    public List<VideoDanmu> getDanmuListByUserId(String userId, String keyword, Integer pageNo, Integer pageSize) {
        VideoDanmUquery query = new VideoDanmUquery();
        query.setUserId(userId);
        query.setContent(keyword);
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setOffset((pageNo - 1) * pageSize);
        return videoDanmuMapper.selectListByUserId(query);
    }

    @Override
    public Integer getDanmuCountByUserId(String userId, String keyword) {
        return videoDanmuMapper.selectCountByUserId(userId, keyword);
    }

    @Override
    public void deleteDanmu(Integer danmuId) {
        videoDanmuMapper.deleteById(danmuId);
    }
}