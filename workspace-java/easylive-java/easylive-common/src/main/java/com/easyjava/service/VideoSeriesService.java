package com.easyjava.service;

import com.easyjava.entity.po.VideoSeries;
import com.easyjava.entity.po.VideoSeriesVideo;
import com.easyjava.entity.po.VideoInfo;
import java.util.List;

/**
 * @Description:视频系列Service接口
 */
public interface VideoSeriesService {
    
    void saveVideoSeries(String userId, String seriesName, String seriesCover);
    
    void updateVideoSeries(String userId, Integer seriesId, String seriesName, String seriesCover);
    
    void deleteVideoSeries(Integer seriesId);
    
    List<VideoSeries> getVideoSeriesList(String userId);
    
    VideoSeries getVideoSeriesDetail(Integer seriesId);
    
    List<VideoInfo> getSeriesVideoList(Integer seriesId);
    
    void addVideoToSeries(Integer seriesId, String videoId);
    
    void removeVideoFromSeries(Integer seriesId, String videoId);
    
    void changeVideoSeriesSort(Integer seriesId, String videoId, Integer sort);
}
