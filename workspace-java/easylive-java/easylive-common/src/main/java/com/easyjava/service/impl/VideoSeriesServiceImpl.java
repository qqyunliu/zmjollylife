package com.easyjava.service.impl;

import com.easyjava.entity.po.VideoSeries;
import com.easyjava.entity.po.VideoSeriesVideo;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.query.VideoSeriesquery;
import com.easyjava.entity.query.VideoSeriesVideoquery;
import com.easyjava.mappers.VideoSeriesMapper;
import com.easyjava.mappers.VideoSeriesVideoMapper;
import com.easyjava.service.VideoSeriesService;
import com.easyjava.service.VideoInfoService;
import com.easyjava.utlis.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:视频系列Service实现
 */
@Service
public class VideoSeriesServiceImpl implements VideoSeriesService {

    @Resource
    private VideoSeriesMapper<VideoSeries, VideoSeriesquery> videoSeriesMapper;
    
    @Resource
    private VideoSeriesVideoMapper<VideoSeriesVideo, VideoSeriesVideoquery> videoSeriesVideoMapper;
    
    @Resource
    private VideoInfoService videoInfoService;

    @Override
    public void saveVideoSeries(String userId, String seriesName, String seriesCover) {
        if (StringTools.isEmpty(userId)) {
            throw new RuntimeException("请先登录");
        }
        
        VideoSeries series = new VideoSeries();
        series.setUserId(userId);
        series.setSeriesName(seriesName);
        series.setSeriesCover(seriesCover);
        series.setCreateTime(new Date());
        series.setUpdateTime(new Date());
        videoSeriesMapper.insert(series);
    }

    @Override
    public void updateVideoSeries(String userId, Integer seriesId, String seriesName, String seriesCover) {
        VideoSeries series = videoSeriesMapper.selectById(seriesId);
        if (series == null) {
            throw new RuntimeException("系列不存在");
        }
        
        series.setSeriesName(seriesName);
        series.setSeriesCover(seriesCover);
        series.setUpdateTime(new Date());
        videoSeriesMapper.update(series);
    }

    @Override
    public void deleteVideoSeries(Integer seriesId) {
        videoSeriesVideoMapper.deleteBySeriesId(seriesId);
        videoSeriesMapper.deleteById(seriesId);
    }

    @Override
    public List<VideoSeries> getVideoSeriesList(String userId) {
        if (StringTools.isEmpty(userId)) {
            return null;
        }
        return videoSeriesMapper.selectListByUserId(userId);
    }

    @Override
    public VideoSeries getVideoSeriesDetail(Integer seriesId) {
        return videoSeriesMapper.selectById(seriesId);
    }

    @Override
    public List<VideoInfo> getSeriesVideoList(Integer seriesId) {
        List<VideoSeriesVideo> seriesVideos = videoSeriesVideoMapper.selectListBySeriesId(seriesId);
        return null;
    }

    @Override
    public void addVideoToSeries(Integer seriesId, String videoId) {
        VideoSeriesVideo seriesVideo = new VideoSeriesVideo();
        seriesVideo.setSeriesId(seriesId);
        seriesVideo.setVideoId(videoId);
        videoSeriesVideoMapper.insert(seriesVideo);
    }

    @Override
    public void removeVideoFromSeries(Integer seriesId, String videoId) {
        videoSeriesVideoMapper.deleteBySeriesIdAndVideoId(seriesId, videoId);
    }

    @Override
    public void changeVideoSeriesSort(Integer seriesId, String videoId, Integer sort) {
        videoSeriesVideoMapper.updateSort(seriesId, videoId, sort);
    }
}
