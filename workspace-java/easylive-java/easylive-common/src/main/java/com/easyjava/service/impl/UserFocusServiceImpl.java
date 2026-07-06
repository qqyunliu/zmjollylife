package com.easyjava.service.impl;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.UserFocus;
import com.easyjava.entity.po.UserMessage;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.mappers.UserFocusMapper;
import com.easyjava.mappers.UserMessageMapper;
import com.easyjava.service.UserFocusService;
import com.easyjava.service.UserMessageService;
import com.easyjava.utlis.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description:用户关注Service实现
 */
@Service
public class UserFocusServiceImpl implements UserFocusService {

    @Resource
    private UserFocusMapper<UserFocus, com.easyjava.entity.query.UserFocusquery> userFocusMapper;
    
    @Resource
    private UserMessageMapper<UserMessage, com.easyjava.entity.query.UserMessagequery> userMessageMapper;
    
    @Resource
    private RedisComponent redisComponent;

    @Override
    public void focusUser(String focusUserId) {
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            throw new RuntimeException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        
        if (userId.equals(focusUserId)) {
            throw new RuntimeException("不能关注自己");
        }
        
        UserFocus existing = userFocusMapper.selectByUserIdAndFocusUserId(userId, focusUserId);
        if (existing != null) {
            return;
        }
        
        UserFocus focus = new UserFocus();
        focus.setUserId(userId);
        focus.setFocusUserId(focusUserId);
        focus.setCreateTime(new Date());
        userFocusMapper.insert(focus);
        
        UserMessage message = new UserMessage();
        message.setUserId(focusUserId);
        message.setMessageType(4);
        message.setFromUserId(userId);
        message.setMessageContent("关注了你");
        message.setCreateTime(new Date());
        userMessageMapper.insert(message);
    }

    @Override
    public void cancelFocus(String focusUserId) {
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            throw new RuntimeException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        userFocusMapper.deleteByUserIdAndFocusUserId(userId, focusUserId);
    }

    @Override
    public boolean isFocused(String focusUserId) {
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            return false;
        }
        String userId = tokenUserInfoDto.getUserId();
        UserFocus focus = userFocusMapper.selectByUserIdAndFocusUserId(userId, focusUserId);
        return focus != null;
    }

    @Override
    public List<UserFocus> getFocusList() {
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            throw new RuntimeException("请先登录");
        }
        return userFocusMapper.selectListByUserId(tokenUserInfoDto.getUserId());
    }

    @Override
    public List<UserFocus> getFansList() {
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            throw new RuntimeException("请先登录");
        }
        return userFocusMapper.selectListByFocusUserId(tokenUserInfoDto.getUserId());
    }

    @Override
    public Integer getFocusCount() {
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            return 0;
        }
        Integer count = userFocusMapper.selectCountByUserId(tokenUserInfoDto.getUserId());
        return count != null ? count : 0;
    }

    @Override
    public Integer getFocusCount(String userId) {
        if (StringTools.isEmpty(userId)) {
            return 0;
        }
        Integer count = userFocusMapper.selectCountByUserId(userId);
        return count != null ? count : 0;
    }

    @Override
    public Integer getFansCount() {
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            return 0;
        }
        Integer count = userFocusMapper.selectCountByFocusUserId(tokenUserInfoDto.getUserId());
        return count != null ? count : 0;
    }

    @Override
    public Integer getFansCount(String userId) {
        if (StringTools.isEmpty(userId)) {
            return 0;
        }
        Integer count = userFocusMapper.selectCountByFocusUserId(userId);
        return count != null ? count : 0;
    }
}
