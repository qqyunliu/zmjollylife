package com.easyjava.service;

import com.easyjava.entity.dto.AiAuditResult;
import com.easyjava.entity.po.VideoInfoPost;

public interface AiAuditService {
	AiAuditResult auditVideo(VideoInfoPost video);
	AiAuditResult auditVideoFrames(String videoId, String videoFilePath);
	AiAuditResult auditAudio(String videoId, String audioText);
	AiAuditResult auditText(String bizId, String text);
	String speechToText(String audioFilePath);
}
