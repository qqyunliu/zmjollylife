package com.easyjava.service.impl;

import com.easyjava.entity.po.UserAction;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.query.UserActionquery;
import com.easyjava.exception.BusinessException;
import com.easyjava.mappers.UserActionMapper;
import com.easyjava.mappers.VideoInfoMapper;
import com.easyjava.service.UserActionService;
import com.easyjava.utlis.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:用户行为Service实现
 */
@Service
public class UserActionServiceImpl implements UserActionService {

    @Resource
    private UserActionMapper<UserAction, UserActionquery> userActionMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, com.easyjava.entity.query.VideoInfoquery> videoInfoMapper;

    @Override
    public void doAction(String userId, String videoId, String videoUserId, Integer actionType, Integer actionCount) throws BusinessException {
        if (StringTools.isEmpty(userId)) {
            throw new BusinessException("请先登录");
        }

        UserAction existingAction = userActionMapper.selectByVideoIdAndUserIdAndType(videoId, userId, actionType);
        boolean hasAction = existingAction != null;

        if (actionType == 0) {
            if (hasAction) {
                userActionMapper.deleteByVideoIdAndUserIdAndType(videoId, userId, actionType);
            } else {
                UserAction userAction = new UserAction();
                userAction.setVideoId(videoId);
                userAction.setVideoUserId(videoUserId);
                userAction.setUserId(userId);
                userAction.setActionType(0);
                userAction.setCommentId(0);
                userAction.setActionTime(new Date());
                userActionMapper.insert(userAction);
            }
            updateVideoCount(videoId, "like_count", hasAction ? -1 : 1);
        } else if (actionType == 1) {
            if (hasAction) {
                userActionMapper.deleteByVideoIdAndUserIdAndType(videoId, userId, actionType);
            } else {
                UserAction userAction = new UserAction();
                userAction.setVideoId(videoId);
                userAction.setVideoUserId(videoUserId);
                userAction.setUserId(userId);
                userAction.setActionType(1);
                userAction.setCommentId(0);
                userAction.setActionTime(new Date());
                userActionMapper.insert(userAction);
            }
        } else if (actionType == 3) {
            if (hasAction) {
                userActionMapper.deleteByVideoIdAndUserIdAndType(videoId, userId, actionType);
            } else {
                UserAction userAction = new UserAction();
                userAction.setVideoId(videoId);
                userAction.setVideoUserId(videoUserId);
                userAction.setUserId(userId);
                userAction.setActionType(3);
                userAction.setCommentId(0);
                userAction.setActionTime(new Date());
                userActionMapper.insert(userAction);
            }
            updateVideoCount(videoId, "collect_count", hasAction ? -1 : 1);
        } else if (actionType == 4) {
            if (!hasAction) {
                UserAction userAction = new UserAction();
                userAction.setVideoId(videoId);
                userAction.setVideoUserId(videoUserId);
                userAction.setUserId(userId);
                userAction.setActionType(4);
                userAction.setCommentId(0);
                userAction.setActionTime(new Date());
                userActionMapper.insert(userAction);
                updateVideoCount(videoId, "coin_count", actionCount != null ? actionCount : 1);
            }
        }
    }

    private void updateVideoCount(String videoId, String field, int delta) {
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            return;
        }

        VideoInfo updateInfo = new VideoInfo();
        if ("like_count".equals(field)) {
            updateInfo.setLikeCount((videoInfo.getLikeCount() == null ? 0 : videoInfo.getLikeCount()) + delta);
        } else if ("coin_count".equals(field)) {
            updateInfo.setCoinCount((videoInfo.getCoinCount() == null ? 0 : videoInfo.getCoinCount()) + delta);
        } else if ("collect_count".equals(field)) {
            updateInfo.setCollectCount((videoInfo.getCollectCount() == null ? 0 : videoInfo.getCollectCount()) + delta);
        }
        videoInfoMapper.updateByVideoId(updateInfo, videoId);
    }

    @Override
    public UserAction getUserAction(String userId, String videoId, Integer actionType) {
        if (StringTools.isEmpty(userId)) {
            return null;
        }
        return userActionMapper.selectByVideoIdAndUserIdAndType(videoId, userId, actionType);
    }

    @Override
    public Integer getActionCount(String videoId, Integer actionType) {
        return userActionMapper.selectCountByVideoIdAndType(videoId, actionType);
    }

    @Override
    public List<UserAction> getUserActionList(String userId, Integer actionType) {
        return userActionMapper.selectListByUserIdAndType(userId, actionType);
    }
}
