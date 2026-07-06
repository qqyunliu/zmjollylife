package com.easyjava.mappers;

import com.easyjava.entity.query.VideoCommentquery;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Description:视频评论Mapper
 */
public interface VideoCommentMapper<T, P> extends BaseMapper {
    
    List<T> selectListByVideoId(@Param("bean") VideoCommentquery query);
    
    Integer selectCountByVideoId(@Param("videoId") String videoId);

    Integer selectAllCountByVideoId(@Param("videoId") String videoId);

    Integer selectAllCount();
    
    List<T> selectListByPCommentId(@Param("pCommentId") Integer pCommentId);

    List<T> selectListByUserId(@Param("bean") VideoCommentquery query);

    Integer selectCountByUserId(@Param("userId") String userId, @Param("content") String content);

    T selectById(@Param("commentId") Integer commentId);
    
    Integer deleteById(@Param("commentId") Integer commentId);

    Integer deleteByVideoId(@Param("videoId") String videoId);
    
    Integer updateLikeCount(@Param("commentId") Integer commentId, @Param("count") Integer count);
    
    Integer updateHateCount(@Param("commentId") Integer commentId, @Param("count") Integer count);
    
    Integer updateTopType(@Param("commentId") Integer commentId, @Param("topType") Integer topType);

    List<T> selectAdminList(@Param("bean") VideoCommentquery query);

    Integer selectAdminCount(@Param("bean") VideoCommentquery query);

    Integer updateStatus(@Param("commentId") Integer commentId, @Param("status") Integer status);

    @Insert("INSERT INTO video_comment_audit (comment_id, video_id, user_id, p_comment_id, reply_user_id, content, audit_status, audit_reason, ai_model, audit_time) " +
            "VALUES (#{commentId}, #{videoId}, #{userId}, #{pCommentId}, #{replyUserId}, #{content}, #{auditStatus}, #{auditReason}, #{aiModel}, NOW())")
    Integer insertCommentAudit(@Param("commentId") Integer commentId,
                              @Param("videoId") String videoId,
                              @Param("userId") String userId,
                              @Param("pCommentId") Integer pCommentId,
                              @Param("replyUserId") String replyUserId,
                              @Param("content") String content,
                              @Param("auditStatus") Integer auditStatus,
                              @Param("auditReason") String auditReason,
                              @Param("aiModel") String aiModel);
}
