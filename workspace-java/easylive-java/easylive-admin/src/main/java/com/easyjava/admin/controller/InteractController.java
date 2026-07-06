package com.easyjava.admin.controller;

import com.easyjava.entity.po.VideoComment;
import com.easyjava.entity.po.VideoDanmu;
import com.easyjava.entity.query.VideoCommentquery;
import com.easyjava.entity.query.VideoDanmUquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.mappers.VideoCommentMapper;
import com.easyjava.mappers.VideoDanmuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/interact")
@Slf4j
public class InteractController extends ABaseController {

    @Resource
    private VideoCommentMapper<VideoComment, VideoCommentquery> videoCommentMapper;

    @Resource
    private VideoDanmuMapper<VideoDanmu, VideoDanmUquery> videoDanmuMapper;

    /**
     * 加载评论列表
     */
    @RequestMapping("/loadComment")
    public ResponseVO loadComment(VideoCommentquery query) {
        if (query.getPageNo() == null || query.getPageNo() < 1) {
            query.setPageNo(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setOffset((query.getPageNo() - 1) * query.getPageSize());

        List<VideoComment> list = videoCommentMapper.selectAdminList(query);
        Integer totalCount = videoCommentMapper.selectAdminCount(query);

        PaginationResultVO<VideoComment> result = new PaginationResultVO<>();
        result.setList(list);
        result.setTotalCount(totalCount);
        result.setPageNo(query.getPageNo());
        result.setPageSize(query.getPageSize());
        return getSuccessResponseVO(result);
    }

    /**
     * 删除评论（软删除，用户端不可见）
     */
    @RequestMapping("/delComment")
    public ResponseVO delComment(Integer commentId) {
        if (commentId == null) {
            return getErrorResponseVO("评论ID不能为空");
        }
        try {
            videoCommentMapper.updateStatus(commentId, -1);
            return getSuccessResponseVO(null);
        } catch (Exception e) {
            log.error("删除评论失败, commentId: {}", commentId, e);
            return getErrorResponseVO("删除评论失败");
        }
    }

    /**
     * 加载弹幕列表
     */
    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(VideoDanmUquery query) {
        if (query.getPageNo() == null || query.getPageNo() < 1) {
            query.setPageNo(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setOffset((query.getPageNo() - 1) * query.getPageSize());

        List<VideoDanmu> list = videoDanmuMapper.selectAdminList(query);
        Integer totalCount = videoDanmuMapper.selectAdminCount(query);

        PaginationResultVO<VideoDanmu> result = new PaginationResultVO<>();
        result.setList(list);
        result.setTotalCount(totalCount);
        result.setPageNo(query.getPageNo());
        result.setPageSize(query.getPageSize());
        return getSuccessResponseVO(result);
    }

    /**
     * 删除弹幕（软删除，用户端不可见）
     */
    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(Integer danmuId) {
        if (danmuId == null) {
            return getErrorResponseVO("弹幕ID不能为空");
        }
        try {
            videoDanmuMapper.updateIsShow(danmuId, 0);
            return getSuccessResponseVO(null);
        } catch (Exception e) {
            log.error("删除弹幕失败, danmuId: {}", danmuId, e);
            return getErrorResponseVO("删除弹幕失败");
        }
    }

    private ResponseVO getErrorResponseVO(String message) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(STATUC_ERROR);
        responseVO.setCode(com.easyjava.enums.ResponseCodeEnum.CODE_500.getCode());
        responseVO.setInfo(message);
        return responseVO;
    }
}
