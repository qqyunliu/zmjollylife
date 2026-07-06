package com.easyjava.entity.query;

import java.util.Date;


/**
 * @Description:视频信息查询对象
 * @auther:哈哈哈
 * @date:2025/08/29
 */public class VideoInfoquery extends BaseQuery {
	/**
	 * 视频ID
	 */
	private String videoId;

	private String videoIdFuzzy;

	/**
	 * 视频封面
	 */
	private String videoCover;

	private String videoCoverFuzzy;

	/**
	 * 视频名称
	 */
	private String videoName;

	private String videoNameFuzzy;

	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEed;

	/**
	 * 最后更新时间
	 */
	private Date lastUpdateTime;

	private String lastUpdateTimeStart;

	private String lastUpdateTimeEed;

	/**
	 * 父级分类ID
	 */
	private Integer pCategoryId;

	/**
	 * 分类ID
	 */
	private Integer categoryId;

	/**
	 * 0:自制 1:转载
	 */
	private Integer postType;

	/**
	 * 原资源说明
	 */
	private String originInfo;

	private String originInfoFuzzy;

	/**
	 * 标签
	 */
	private String tags;

	private String tagsFuzzy;

	/**
	 * 简介
	 */
	private String introduction;

	private String introductionFuzzy;

	/**
	 * 互动设置
	 */
	private String interaction;

	private String interactionFuzzy;

	/**
	 * 持续时间（秒）
	 */
	private Integer duration;

	/**
	 * 播放数量
	 */
	private Integer playCount;

	/**
	 * 点赞数量
	 */
	private Integer likeCount;

	/**
	 * 弹幕数量
	 */
	private Integer dankuCount;

	/**
	 * 评论数量
	 */
	private Integer commentCount;

	/**
	 * 投币数量
	 */
	private Integer coinCount;

	/**
	 * 收藏数量
	 */
	private Integer collectCount;

	/**
	 * 是否推荐0:未推荐 1:已推荐
	 */
	private Integer recommendType;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 最后播放时间
	 */
	private Date lastPlayTime;

	private String lastPlayTimeStart;

	private String lastPlayTimeEed;

	private boolean queryUserInfo;

	private String nickNameFuzzy;

	public boolean isQueryUserInfo() {
		return queryUserInfo;
	}

	public void setQueryUserInfo(boolean queryUserInfo) {
		this.queryUserInfo = queryUserInfo;
	}

	public String getNickNameFuzzy() {
		return nickNameFuzzy;
	}

	public void setNickNameFuzzy(String nickNameFuzzy) {
		this.nickNameFuzzy = nickNameFuzzy;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoId() {
		return this.videoId;
	}

	public void setVideoCover(String videoCover) {
		this.videoCover = videoCover;
	}

	public String getVideoCover() {
		return this.videoCover;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoName() {
		return this.videoName;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setPCategoryId(Integer pCategoryId) {
		this.pCategoryId = pCategoryId;
	}

	public Integer getPCategoryId() {
		return this.pCategoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setPostType(Integer postType) {
		this.postType = postType;
	}

	public Integer getPostType() {
		return this.postType;
	}

	public void setOriginInfo(String originInfo) {
		this.originInfo = originInfo;
	}

	public String getOriginInfo() {
		return this.originInfo;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return this.tags;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getIntroduction() {
		return this.introduction;
	}

	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	public String getInteraction() {
		return this.interaction;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getDuration() {
		return this.duration;
	}

	public void setPlayCount(Integer playCount) {
		this.playCount = playCount;
	}

	public Integer getPlayCount() {
		return this.playCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getLikeCount() {
		return this.likeCount;
	}

	public void setDankuCount(Integer dankuCount) {
		this.dankuCount = dankuCount;
	}

	public Integer getDankuCount() {
		return this.dankuCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getCommentCount() {
		return this.commentCount;
	}

	public void setCoinCount(Integer coinCount) {
		this.coinCount = coinCount;
	}

	public Integer getCoinCount() {
		return this.coinCount;
	}

	public void setCollectCount(Integer collectCount) {
		this.collectCount = collectCount;
	}

	public Integer getCollectCount() {
		return this.collectCount;
	}

	public void setRecommendType(Integer recommendType) {
		this.recommendType = recommendType;
	}

	public Integer getRecommendType() {
		return this.recommendType;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setLastPlayTime(Date lastPlayTime) {
		this.lastPlayTime = lastPlayTime;
	}

	public Date getLastPlayTime() {
		return this.lastPlayTime;
	}

	public void setVideoIdFuzzy(String videoIdFuzzy) {
		this.videoIdFuzzy = videoIdFuzzy;
	}

	public String getVideoIdFuzzy() {
		return this.videoIdFuzzy;
	}

	public void setVideoCoverFuzzy(String videoCoverFuzzy) {
		this.videoCoverFuzzy = videoCoverFuzzy;
	}

	public String getVideoCoverFuzzy() {
		return this.videoCoverFuzzy;
	}

	public void setVideoNameFuzzy(String videoNameFuzzy) {
		this.videoNameFuzzy = videoNameFuzzy;
	}

	public String getVideoNameFuzzy() {
		return this.videoNameFuzzy;
	}

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy() {
		return this.userIdFuzzy;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart() {
		return this.createTimeStart;
	}

	public void setCreateTimeEed(String createTimeEed) {
		this.createTimeEed = createTimeEed;
	}

	public String getCreateTimeEed() {
		return this.createTimeEed;
	}

	public void setLastUpdateTimeStart(String lastUpdateTimeStart) {
		this.lastUpdateTimeStart = lastUpdateTimeStart;
	}

	public String getLastUpdateTimeStart() {
		return this.lastUpdateTimeStart;
	}

	public void setLastUpdateTimeEed(String lastUpdateTimeEed) {
		this.lastUpdateTimeEed = lastUpdateTimeEed;
	}

	public String getLastUpdateTimeEed() {
		return this.lastUpdateTimeEed;
	}

	public void setOriginInfoFuzzy(String originInfoFuzzy) {
		this.originInfoFuzzy = originInfoFuzzy;
	}

	public String getOriginInfoFuzzy() {
		return this.originInfoFuzzy;
	}

	public void setTagsFuzzy(String tagsFuzzy) {
		this.tagsFuzzy = tagsFuzzy;
	}

	public String getTagsFuzzy() {
		return this.tagsFuzzy;
	}

	public void setIntroductionFuzzy(String introductionFuzzy) {
		this.introductionFuzzy = introductionFuzzy;
	}

	public String getIntroductionFuzzy() {
		return this.introductionFuzzy;
	}

	public void setInteractionFuzzy(String interactionFuzzy) {
		this.interactionFuzzy = interactionFuzzy;
	}

	public String getInteractionFuzzy() {
		return this.interactionFuzzy;
	}

	public void setLastPlayTimeStart(String lastPlayTimeStart) {
		this.lastPlayTimeStart = lastPlayTimeStart;
	}

	public String getLastPlayTimeStart() {
		return this.lastPlayTimeStart;
	}

	public void setLastPlayTimeEed(String lastPlayTimeEed) {
		this.lastPlayTimeEed = lastPlayTimeEed;
	}

	public String getLastPlayTimeEed() {
		return this.lastPlayTimeEed;
	}

}