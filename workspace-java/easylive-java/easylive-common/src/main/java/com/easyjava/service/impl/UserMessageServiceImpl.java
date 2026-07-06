package com.easyjava.service.impl;

import com.easyjava.entity.po.UserMessage;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.mappers.UserMessageMapper;
import com.easyjava.service.UserMessageService;
import com.easyjava.utlis.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:用户消息Service实现
 */
@Service
public class UserMessageServiceImpl implements UserMessageService {

    @Resource
    private UserMessageMapper<UserMessage, com.easyjava.entity.query.UserMessagequery> userMessageMapper;

    @Override
    public void sendMessage(Integer messageType, String fromUserId, String videoId, Integer commentId, String content) {
        VideoInfo videoInfo = null;
        if (videoId != null) {
            videoInfo = new VideoInfo();
        }
        String receiveUserId = videoInfo != null ? videoInfo.getUserId() : null;
        if (receiveUserId == null || receiveUserId.equals(fromUserId)) {
            return;
        }
        
        UserMessage message = new UserMessage();
        message.setUserId(receiveUserId);
        message.setMessageType(messageType);
        message.setFromUserId(fromUserId);
        message.setVideoId(videoId);
        message.setCommentId(commentId);
        message.setMessageContent(content);
        message.setCreateTime(new Date());
        userMessageMapper.insert(message);
    }

    @Override
    public List<UserMessage> getMessageList(String userId) {
        if (StringTools.isEmpty(userId)) {
            return null;
        }
        return userMessageMapper.selectListByUserId(userId);
    }

    @Override
    public Integer getNoReadCount(String userId) {
        if (StringTools.isEmpty(userId)) {
            return 0;
        }
        Integer count = userMessageMapper.selectNoReadCount(userId);
        return count != null ? count : 0;
    }

    @Override
    public void readAllMessage(String userId) {
        if (StringTools.isEmpty(userId)) {
            return;
        }
        userMessageMapper.updateIsRead(userId);
    }

    @Override
    public void deleteMessage(Integer messageId) {
        userMessageMapper.deleteById(messageId);
    }

    @Override
    public Map<Integer, Integer> getNoReadCountGroup() {
        Map<Integer, Integer> result = new HashMap<>();
        return result;
    }
}
