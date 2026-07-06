package com.easyjava.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
/*这是一个系统设置数据传输对象 SysSettingDto，它定义了应用的各种系统参数和限制配置
*  */
@JsonIgnoreProperties(ignoreUnknown = true)// JSON反序列化时忽略未知字段
public class SysSettingDto implements Serializable {

    private static final long serialVersionUID = 8831552587682209371L;
    private Integer registerCoinCount = 10;
    private Integer postVideoCoinCOUNT = 5;
    private Integer videoSize = 100 ;
    private Integer videoCount = 10;
    private Integer videoPCount = 10;
    private Integer commentCount = 20;
    private Integer danmuCount = 20;

    public Integer getVideoPCount() {
        return videoPCount;
    }

    public void setVideoPCount(Integer videoPCount) {
        this.videoPCount = videoPCount;
    }

    public Integer getRegisterCoinCount() {
        return registerCoinCount;
    }

    public void setRegisterCoinCount(Integer registerCoinCount) {
        this.registerCoinCount = registerCoinCount;
    }

    public Integer getPostVideoCoinCOUNT() {
        return postVideoCoinCOUNT;
    }

    public void setPostVideoCoinCOUNT(Integer postVideoCoinCOUNT) {
        this.postVideoCoinCOUNT = postVideoCoinCOUNT;
    }

    public Integer getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(Integer videoSize) {
        this.videoSize = videoSize;
    }

    public Integer getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(Integer videoCount) {
        this.videoCount = videoCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getDanmuCount() {
        return danmuCount;
    }

    public void setDanmuCount(Integer danmuCount) {
        this.danmuCount = danmuCount;
    }
}
