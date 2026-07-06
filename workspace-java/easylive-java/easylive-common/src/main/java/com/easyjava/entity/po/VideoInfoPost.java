package com.easyjava.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.easyjava.enums.VideoStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.elasticsearch.index.query.RankFeatureQueryBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.utlis.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:视频信息
 * @auther:哈哈哈
 * @date:2025/08/16
 */public class VideoInfoPost  extends  VideoInfo implements Serializable{
	/**
	 * 视频ID
	 */
	private String videoId;

	/**
	 * 视频封面
	 */
	private String videoCover;

	/**
	 * 视频名称
	 */
	private String videoName;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH-mm-ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH-mm-ss")
	private Date createTime;

	/**
	 * 最后更新时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH-mm-ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH-mm-ss")
	private Date lastUpdateTime;

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

	/**
	 * 标签
	 */
	private String tags;

	/**
	 * 简介
	 */
	private String introduction;

	/**
	 * 互动设置
	 */
	private String interaction;

	/**
	 * 持续时间（秒）
	 */
	private Integer duration;

	/**
	 * AI审核状态: 0-待审核, 1-通过, 2-不通过
	 */
	private Integer aiAuditStatus;

	/**
	 * AI审核返回的详细结果(JSON)
	 */
	private String aiAuditResult;

	/**
	 * AI审核时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date aiAuditTime;

	/**
	 * AI审核不通过原因
	 */
	private String aiAuditReason;

	/**
	 * 人工复核状态: 0-待复核, 1-通过, 2-不通过
	 */
	private Integer reviewStatus;

	/**
	 * 人工复核时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date reviewTime;

	/**
	 * 复核人ID
	 */
	private String reviewerId;

	/**
	 * 人工复核不通过原因
	 */
	private String reviewReason;


	private String statusName;

	public String getStatusName() {
		VideoStatusEnum videoStatusEnum=VideoStatusEnum.getByStatus(status);
		return videoStatusEnum==null ? "" : videoStatusEnum.getDesc();
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
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

	public void setAiAuditResult(String aiAuditResult) {
		this.aiAuditResult = aiAuditResult;
	}

	public String getAiAuditResult() {
		return this.aiAuditResult;
	}

	public void setAiAuditTime(Date aiAuditTime) {
		this.aiAuditTime = aiAuditTime;
	}

	public Date getAiAuditTime() {
		return this.aiAuditTime;
	}

	public void setAiAuditReason(String aiAuditReason) {
		this.aiAuditReason = aiAuditReason;
	}

	public String getAiAuditReason() {
		return this.aiAuditReason;
	}

	public void setReviewStatus(Integer reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public Integer getReviewStatus() {
		return this.reviewStatus;
	}

	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	public Date getReviewTime() {
		return this.reviewTime;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public String getReviewerId() {
		return this.reviewerId;
	}

	public void setReviewReason(String reviewReason) {
		this.reviewReason = reviewReason;
	}

	public String getReviewReason() {
		return this.reviewReason;
	}

	@Override
	 public String toString () {
		return "视频ID:" + (videoId == null ? "空" : videoId) + ",视频封面:" + (videoCover == null ? "空" : videoCover) + ",视频名称:" + (videoName == null ? "空" : videoName) + ",用户ID:" + (userId == null ? "空" : userId) + ",创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime,DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",最后更新时间:" + (lastUpdateTime == null ? "空" : DateUtils.format(lastUpdateTime,DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",父级分类ID:" + (pCategoryId == null ? "空" : pCategoryId) + ",分类ID:" + (categoryId == null ? "空" : categoryId) + ",0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败:" + (status == null ? "空" : status) + ",0:自制 1:转载:" + (postType == null ? "空" : postType) + ",原资源说明:" + (originInfo == null ? "空" : originInfo) + ",标签:" + (tags == null ? "空" : tags) + ",简介:" + (introduction == null ? "空" : introduction) + ",互动设置:" + (interaction == null ? "空" : interaction) + ",持续时间（秒）:" + (duration == null ? "空" : duration);
	}
}