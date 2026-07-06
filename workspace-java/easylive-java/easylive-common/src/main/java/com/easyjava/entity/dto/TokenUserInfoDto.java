package com.easyjava.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
/*这是一个用户令牌信息数据传输对象 TokenUserInfoDto，它封装了用户认证令牌相关的所有信息*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenUserInfoDto implements Serializable{

    private static final long serialVersionUID = 3979560847374678327L;
    private String userId;
    private String nickId;
    private String avatar;
    private String token;
    private long expireAt;// 令牌过期时间戳（毫秒）

    private Integer fansCount;
    private Integer currentCoinCount;
    private Integer focusCount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickId() {
        return nickId;
    }

    public void setNickId(String nickId) {
        this.nickId = nickId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public Integer getFansCount() {
        return fansCount;
    }

    public void setFansCount(Integer fansCount) {
        this.fansCount = fansCount;
    }

    public Integer getCurrentCoinCount() {
        return currentCoinCount;
    }

    public void setCurrentCoinCount(Integer currentCoinCount) {
        this.currentCoinCount = currentCoinCount;
    }

    public Integer getFocusCount() {
        return focusCount;
    }

    public void setFocusCount(Integer focusCount) {
        this.focusCount = focusCount;
    }
}
