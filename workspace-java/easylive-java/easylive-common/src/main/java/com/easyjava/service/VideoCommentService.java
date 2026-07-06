package com.easyjava.service;

import com.easyjava.entity.po.VideoComment;
import com.easyjava.exception.BusinessException;
import java.util.List;

/**
 * @Description:视频评论Service接口
 */
public interface VideoCommentService {
    
    void saveComment(String videoId, String content, Integer pCommentId, String replyUserId, String userId) throws BusinessException;
    
    List<VideoComment> getCommentList(String videoId, Integer pageNo, Integer pageSize, Integer sortType);
    
    Integer getCommentCount(String videoId);

    Integer getAllCommentCount(String videoId);
    
    List<VideoComment> getReplyList(Integer pCommentId);

    List<VideoComment> getCommentListByUserId(String userId, String keyword, Integer pageNo, Integer pageSize);

    Integer getCommentCountByUserId(String userId, String keyword);

    void deleteComment(Integer commentId);
    
    void likeComment(Integer commentId);
    
    void hateComment(Integer commentId);
    
    void topComment(Integer commentId);
    
    void cancelTopComment(Integer commentId);
}
