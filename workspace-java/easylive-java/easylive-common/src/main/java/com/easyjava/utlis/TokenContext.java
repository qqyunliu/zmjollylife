package com.easyjava.utlis;

import com.easyjava.entity.dto.TokenUserInfoDto;

public class TokenContext {
    
    private static final ThreadLocal<TokenUserInfoDto> userInfoHolder = new ThreadLocal<>();
    
    public static void set(TokenUserInfoDto userInfo) {
        userInfoHolder.set(userInfo);
    }
    
    public static TokenUserInfoDto get() {
        return userInfoHolder.get();
    }
    
    public static void remove() {
        userInfoHolder.remove();
    }
    
    public static String getUserId() {
        TokenUserInfoDto userInfo = get();
        return userInfo != null ? userInfo.getUserId() : null;
    }
}
