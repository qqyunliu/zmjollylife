package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.VideoComment;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.VideoCommentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:视频评论Controller
 */
@RestController
@RequestMapping("/comment")
public class VideoCommentController extends ABaseController {

    @Resource
    private VideoCommentService videoCommentService;

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(String videoId, Integer pageNo, Integer pageSize, Integer sortType) {
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        if (sortType == null) sortType = 0;
        
        List<VideoComment> commentList = videoCommentService.getCommentList(videoId, pageNo, pageSize, sortType);
        Integer totalCount = videoCommentService.getCommentCount(videoId);
        Integer allCount = videoCommentService.getAllCommentCount(videoId);
        
        PaginationResultVO result = new PaginationResultVO();
        result.setList(commentList);
        result.setTotalCount(totalCount);
        result.setAllCount(allCount);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/loadReply")
    public ResponseVO loadReply(Integer pCommentId) {
        List<VideoComment> replyList = videoCommentService.getReplyList(pCommentId);
        return getSuccessResponseVO(replyList);
    }

    @RequestMapping("/postComment")
    public ResponseVO postComment(String videoId, String content, Integer pCommentId, String replyUserId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        try {
            videoCommentService.saveComment(videoId, content, pCommentId, replyUserId, tokenUserInfoDto.getUserId());
            return getSuccessResponseVO(null);
        } catch (BusinessException e) {
            return getErrorResponseVO(e.getMessage());
        }
    }

    @RequestMapping("/userDelComment")
    public ResponseVO userDelComment(Integer commentId) {
        videoCommentService.deleteComment(commentId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/likeComment")
    public ResponseVO likeComment(Integer commentId) {
        videoCommentService.likeComment(commentId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/hateComment")
    public ResponseVO hateComment(Integer commentId) {
        videoCommentService.hateComment(commentId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/topComment")
    public ResponseVO topComment(Integer commentId) {
        videoCommentService.topComment(commentId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cancelTopComment")
    public ResponseVO cancelTopComment(Integer commentId) {
        videoCommentService.cancelTopComment(commentId);
        return getSuccessResponseVO(null);
    }
}
