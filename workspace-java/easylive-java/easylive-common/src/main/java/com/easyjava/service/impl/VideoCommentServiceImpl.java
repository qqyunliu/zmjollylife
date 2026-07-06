package com.easyjava.service.impl;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.dto.AiAuditResult;
import com.easyjava.entity.po.UserMessage;
import com.easyjava.entity.po.VideoComment;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.query.VideoCommentquery;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.UserMessageMapper;
import com.easyjava.mappers.VideoCommentMapper;
import com.easyjava.mappers.VideoInfoMapper;
import com.easyjava.service.AiAuditService;
import com.easyjava.service.VideoCommentService;
import com.easyjava.utlis.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:视频评论Service实现
 */
@Service
public class VideoCommentServiceImpl implements VideoCommentService {

    @Resource
    private VideoCommentMapper<VideoComment, com.easyjava.entity.query.VideoCommentquery> videoCommentMapper;
    
    @Resource
    private UserMessageMapper<UserMessage, com.easyjava.entity.query.UserMessagequery> userMessageMapper;
    
    @Resource
    private RedisComponent redisComponent;
    
    @Resource
    private VideoInfoMapper<VideoInfo, com.easyjava.entity.query.VideoInfoquery> videoInfoMapper;

    @Resource
    private AiAuditService aiAuditService;

    @Override
    public void saveComment(String videoId, String content, Integer pCommentId, String replyUserId, String userId) throws BusinessException {
        if (StringTools.isEmpty(content)) {
            throw new BusinessException("评论内容不能为空");
        }
        Integer parentId = pCommentId == null ? 0 : pCommentId;
        AiAuditResult auditResult = aiAuditService.auditText(videoId, content);
        if (auditResult == null || !Boolean.TRUE.equals(auditResult.getPassed())) {
            String reason = auditResult == null ? "审核失败" : auditResult.getReason();
            try {
                videoCommentMapper.insertCommentAudit(null, videoId, userId, parentId, replyUserId, content, 2, reason, "deepseek-v3-2-251201");
            } catch (Exception e) {
            }
            throw new BusinessException("评论内容审核未通过" + (StringTools.isEmpty(reason) ? "" : (": " + reason)));
        }

        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoId);
        
        VideoComment comment = new VideoComment();
        comment.setpCommentId(parentId);
        comment.setVideoId(videoId);
        comment.setVideoUserId(videoInfo != null ? videoInfo.getUserId() : null);
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setReplyUserId(replyUserId);
        comment.setPostTime(new Date());
        videoCommentMapper.insert(comment);

        try {
            videoCommentMapper.insertCommentAudit(comment.getCommentId(), videoId, userId, parentId, replyUserId, content, 1, null, "deepseek-v3-2-251201");
        } catch (Exception e) {
        }
        
        if (pCommentId == null || pCommentId == 0) {
            if (videoInfo != null && !userId.equals(videoInfo.getUserId())) {
                UserMessage message = new UserMessage();
                message.setUserId(videoInfo.getUserId());
                message.setMessageType(5);
                message.setFromUserId(userId);
                message.setVideoId(videoId);
                message.setMessageContent("评论了你的视频");
                message.setCreateTime(new Date());
                userMessageMapper.insert(message);
            }
        } else {
            VideoComment parentComment = videoCommentMapper.selectById(pCommentId);
            if (parentComment != null && !userId.equals(parentComment.getUserId())) {
                UserMessage message = new UserMessage();
                message.setUserId(parentComment.getUserId());
                message.setMessageType(5);
                message.setFromUserId(userId);
                message.setVideoId(videoId);
                message.setCommentId(pCommentId);
                message.setMessageContent("回复了你的评论");
                message.setCreateTime(new Date());
                userMessageMapper.insert(message);
            }
        }
    }

    @Override
    public List<VideoComment> getCommentList(String videoId, Integer pageNo, Integer pageSize, Integer sortType) {
        VideoCommentquery query = new VideoCommentquery();
        query.setVideoId(videoId);
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setSortType(sortType);
        query.setOffset((pageNo - 1) * pageSize);
        return videoCommentMapper.selectListByVideoId(query);
    }

    @Override
    public Integer getCommentCount(String videoId) {
        return videoCommentMapper.selectCountByVideoId(videoId);
    }

    @Override
    public Integer getAllCommentCount(String videoId) {
        return videoCommentMapper.selectAllCountByVideoId(videoId);
    }

    @Override
    public List<VideoComment> getReplyList(Integer pCommentId) {
        return videoCommentMapper.selectListByPCommentId(pCommentId);
    }

    @Override
    public List<VideoComment> getCommentListByUserId(String userId, String keyword, Integer pageNo, Integer pageSize) {
        VideoCommentquery query = new VideoCommentquery();
        query.setUserId(userId);
        query.setContent(keyword);
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setOffset((pageNo - 1) * pageSize);
        return videoCommentMapper.selectListByUserId(query);
    }

    @Override
    public Integer getCommentCountByUserId(String userId, String keyword) {
        return videoCommentMapper.selectCountByUserId(userId, keyword);
    }

    @Override
    public void deleteComment(Integer commentId) {
        videoCommentMapper.deleteById(commentId);
    }

    @Override
    public void likeComment(Integer commentId) {
        videoCommentMapper.updateLikeCount(commentId, 1);
    }

    @Override
    public void hateComment(Integer commentId) {
        videoCommentMapper.updateHateCount(commentId, 1);
    }

    @Override
    public void topComment(Integer commentId) {
        videoCommentMapper.updateTopType(commentId, 1);
    }

    @Override
    public void cancelTopComment(Integer commentId) {
        videoCommentMapper.updateTopType(commentId, 0);
    }
}
