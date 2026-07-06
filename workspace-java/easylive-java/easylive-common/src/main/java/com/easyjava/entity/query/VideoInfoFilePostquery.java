package com.easyjava.entity.query;

import java.util.Date;


/**
 * @Description:视频文件信息查询对象
 * @auther:哈哈哈
 * @date:2025/08/16
 */public class VideoInfoFilePostquery extends BaseQuery {
	/**
	 * 唯一ID
	 */
	private String fileId;

	private String fileIdFuzzy;

	/**
	 * 上传ID
	 */
	private String uploadId;

	private String uploadIdFuzzy;

	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 视频ID
	 */
	private String videoId;

	private String videoIdFuzzy;

	/**
	 * 文件索引
	 */
	private Integer fileIndex;

	/**
	 * 文件名
	 */
	private String fileName;

	private String fileNameFuzzy;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件路径
	 */
	private String filePath;

	private String filePathFuzzy;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	private String updateTimeStart;

	private String updateTimeEed;

	/**
	 * 0:无更新 1:有更新
	 */
	private Integer transferResult;

	/**
	 * 持续时间（秒）
	 */
	private Integer duration;

	/**
	 * 0:转码中 1:转码成功 2:转码失败
	 */
	private Integer postType;


	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileId() {
		return this.fileId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public String getUploadId() {
		return this.uploadId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoId() {
		return this.videoId;
	}

	public void setFileIndex(Integer fileIndex) {
		this.fileIndex = fileIndex;
	}

	public Integer getFileIndex() {
		return this.fileIndex;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Long getFileSize() {
		return this.fileSize;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setTransferResult(Integer transferResult) {
		this.transferResult = transferResult;
	}

	public Integer getTransferResult() {
		return this.transferResult;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getDuration() {
		return this.duration;
	}

	public void setPostType(Integer postType) {
		this.postType = postType;
	}

	public Integer getPostType() {
		return this.postType;
	}

	public void setFileIdFuzzy(String fileIdFuzzy) {
		this.fileIdFuzzy = fileIdFuzzy;
	}

	public String getFileIdFuzzy() {
		return this.fileIdFuzzy;
	}

	public void setUploadIdFuzzy(String uploadIdFuzzy) {
		this.uploadIdFuzzy = uploadIdFuzzy;
	}

	public String getUploadIdFuzzy() {
		return this.uploadIdFuzzy;
	}

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy() {
		return this.userIdFuzzy;
	}

	public void setVideoIdFuzzy(String videoIdFuzzy) {
		this.videoIdFuzzy = videoIdFuzzy;
	}

	public String getVideoIdFuzzy() {
		return this.videoIdFuzzy;
	}

	public void setFileNameFuzzy(String fileNameFuzzy) {
		this.fileNameFuzzy = fileNameFuzzy;
	}

	public String getFileNameFuzzy() {
		return this.fileNameFuzzy;
	}

	public void setFilePathFuzzy(String filePathFuzzy) {
		this.filePathFuzzy = filePathFuzzy;
	}

	public String getFilePathFuzzy() {
		return this.filePathFuzzy;
	}

	public void setUpdateTimeStart(String updateTimeStart) {
		this.updateTimeStart = updateTimeStart;
	}

	public String getUpdateTimeStart() {
		return this.updateTimeStart;
	}

	public void setUpdateTimeEed(String updateTimeEed) {
		this.updateTimeEed = updateTimeEed;
	}

	public String getUpdateTimeEed() {
		return this.updateTimeEed;
	}

}