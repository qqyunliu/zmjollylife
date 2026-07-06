package com.easyjava.service;

import com.easyjava.entity.po.UserFocus;
import java.util.List;

/**
 * @Description:用户关注Service接口
 */
public interface UserFocusService {
    
    void focusUser(String focusUserId);
    
    void cancelFocus(String focusUserId);
    
    boolean isFocused(String focusUserId);
    
    List<UserFocus> getFocusList();
    
    List<UserFocus> getFansList();
    
    Integer getFocusCount();

    Integer getFocusCount(String userId);
    
    Integer getFansCount();

    Integer getFansCount(String userId);
}
