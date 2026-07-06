package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.UserAction;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.UserActionService;
import com.easyjava.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:用户行为Controller（点赞、投币、收藏）
 */
@RestController
@RequestMapping("/userAction")
public class UserActionController extends ABaseController {

    @Resource
    private UserActionService userActionService;

    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/doAction")
    public ResponseVO doAction(@NotEmpty String videoId, 
                                @NotEmpty Integer actionType,
                                Integer actionCount,
                                String videoUserId) {
        if (videoUserId == null) {
            VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
            if (videoInfo != null) {
                videoUserId = videoInfo.getUserId();
            }
        }
        
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        
        try {
            userActionService.doAction(tokenUserInfoDto.getUserId(), videoId, videoUserId, actionType, actionCount);
            return getSuccessResponseVO(null);
        } catch (BusinessException e) {
            return getErrorResponseVO(e.getMessage());
        }
    }

    @RequestMapping("/getUserAction")
    public ResponseVO getUserAction(@NotEmpty String videoId, @NotEmpty Integer actionType) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        String userId = tokenUserInfoDto != null ? tokenUserInfoDto.getUserId() : null;
        
        UserAction userAction = userActionService.getUserAction(userId, videoId, actionType);
        boolean hasAction = userAction != null;
        
        Map<String, Object> result = new HashMap<>();
        result.put("hasAction", hasAction);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/getVideoActionCount")
    public ResponseVO getVideoActionCount(@NotEmpty String videoId) {
        Integer likeCount = userActionService.getActionCount(videoId, 0);
        Integer coinCount = userActionService.getActionCount(videoId, 4);
        Integer collectCount = userActionService.getActionCount(videoId, 3);
        
        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount != null ? likeCount : 0);
        result.put("coinCount", coinCount != null ? coinCount : 0);
        result.put("collectCount", collectCount != null ? collectCount : 0);
        
        return getSuccessResponseVO(result);
    }
}