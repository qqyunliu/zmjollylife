package com.easyjava.service;

import com.easyjava.entity.po.UserAction;
import com.easyjava.entity.query.UserActionquery;
import com.easyjava.exception.BusinessException;
import java.util.List;

/**
 * @Description:用户行为Service接口
 */
public interface UserActionService {
    
    void doAction(String userId, String videoId, String videoUserId, Integer actionType, Integer actionCount) throws BusinessException;
    
    UserAction getUserAction(String userId, String videoId, Integer actionType);
    
    Integer getActionCount(String videoId, Integer actionType);
    
    List<UserAction> getUserActionList(String userId, Integer actionType);
}
