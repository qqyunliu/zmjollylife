package com.easyjava.entity.po;

import java.io.Serializable;

/**
 * @Description:系列视频关联
 */
public class VideoSeriesVideo implements Serializable {
    
    private Integer id;
    
    private Integer seriesId;
    
    private String videoId;
    
    private Integer sort;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}