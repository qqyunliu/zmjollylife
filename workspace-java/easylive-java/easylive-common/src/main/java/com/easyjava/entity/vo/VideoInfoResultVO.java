package com.easyjava.entity.vo;

import com.easyjava.entity.po.VideoInfo;

import java.util.List;
/*这是一个视频信息结果封装类 VideoInfoResultVO，它用于组合视频基本信息和用户行为数据*/
public class VideoInfoResultVO {
    private VideoInfo videoInfo;

    private List userActionList;


    public VideoInfoResultVO() {
    }

    public List getUserActionList() {
        return userActionList;
    }

    public void setUserActionList(List userActionList) {
        this.userActionList = userActionList;
    }

    public VideoInfoResultVO(VideoInfo videoInfo, List userActionList) {
        this.videoInfo = videoInfo;
        this.userActionList = userActionList;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }
}
