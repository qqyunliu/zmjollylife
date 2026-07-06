package com.easyjava.service;

import com.easyjava.entity.po.UserMessage;
import java.util.List;
import java.util.Map;

/**
 * @Description:用户消息Service接口
 */
public interface UserMessageService {
    
    void sendMessage(Integer messageType, String fromUserId, String videoId, Integer commentId, String content);
    
    List<UserMessage> getMessageList(String userId);
    
    Integer getNoReadCount(String userId);
    
    void readAllMessage(String userId);
    
    void deleteMessage(Integer messageId);
    
    Map<Integer, Integer> getNoReadCountGroup();
}
