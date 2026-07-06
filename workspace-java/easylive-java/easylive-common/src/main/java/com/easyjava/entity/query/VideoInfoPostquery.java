package com.easyjava.entity.query;

import java.util.Date;


/**
 * @Description:视频信息查询对象
 * @auther:哈哈哈
 * @date:2025/08/16
 */public class VideoInfoPostquery extends BaseQuery {
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
	 * 0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败
	 */
	private Integer status;

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
	 * AI审核状态: 0-待审核, 1-通过, 2-不通过
	 */
	private Integer aiAuditStatus;

	/**
	 * 人工复核状态: 0-待复核, 1-通过, 2-不通过
	 */
	private Integer reviewStatus;

	private Integer[] excludeStatusArray;

	private Integer[] statusArray;

	public Integer[] getStatusArray() {
		return statusArray;
	}

	public void setStatusArray(Integer[] statusArray) {
		this.statusArray = statusArray;
	}

	private boolean queryCountInfo;
	private boolean queryUserInfo;

	public boolean isQueryUserInfo() {
		return queryUserInfo;
	}

	public void setQueryUserInfo(boolean queryUserInfo) {
		this.queryUserInfo = queryUserInfo;
	}

	public Integer getpCategoryId() {
		return pCategoryId;
	}

	public void setpCategoryId(Integer pCategoryId) {
		this.pCategoryId = pCategoryId;
	}

	public Integer[] getExcludeStatusArray() {
		return excludeStatusArray;
	}

	public void setExcludeStatusArray(Integer[] excludeStatusArray) {
		this.excludeStatusArray = excludeStatusArray;
	}

	public boolean isQueryCountInfo() {
		return queryCountInfo;
	}

	public void setQueryCountInfo(boolean queryCountInfo) {
		this.queryCountInfo = queryCountInfo;
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

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
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

	public void setAiAuditStatus(Integer aiAuditStatus) {
		this.aiAuditStatus = aiAuditStatus;
	}

	public Integer getAiAuditStatus() {
		return this.aiAuditStatus;
	}

	public void setReviewStatus(Integer reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public Integer getReviewStatus() {
		return this.reviewStatus;
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

}