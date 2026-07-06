package com.easyjava.web.controller;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.utlis.TokenContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Repeatable;

public class ABaseController {
    @Resource
    private RedisComponent redisComponent;/*Redis操作组件，用于Token管理*/
    protected static final String SIATUC_SUCCESS = "success";

    protected static final String STATUC_ERROR = "error";

    protected <T> ResponseVO getSuccessResponseVO(T t) {
        /*创建标准化的成功响应对象*/
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(SIATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected ResponseVO getErrorResponseVO(String message) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(STATUC_ERROR);
        responseVO.setCode(ResponseCodeEnum.CODE_500.getCode());
        responseVO.setInfo(message);
        return responseVO;
    }

    protected String getIpAddr() {
        /*客户端IP获取*/
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    protected void saveToken2Cookie(HttpServletResponse response, String token) {
        /*保存Token到Cookie：
        * 创建名为TOKEN_WEB的Cookie
        设置7天有效期（TIME_SECONDS_DAY * 7）
        路径设置为根路径"/"，使整个应用可访问*/
        Cookie cookie = new Cookie(Constans.TOKEN_WEB, token);
        cookie.setMaxAge((Constans.TIME_SECONDS_DAY * 7));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected TokenUserInfoDto getTokenUserInfoDto() {
        /*获取Token用户信息：
        * 从请求头获取Token（request.getHeader(Constans.TOKEN_WEB)）
        通过Redis查询Token对应的用户信息
        问题：缺少空值检查和异常处理*/
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(Constans.TOKEN_WEB);
        TokenUserInfoDto userInfo = (TokenUserInfoDto)redisComponent.getTokenInfo(token);
        if (userInfo != null) {
            TokenContext.set(userInfo);
        }
        return userInfo;
    }
    protected void cleanCookie(HttpServletResponse response){
        /*清理Cookie和Token：
        遍历请求中的所有Cookie
        找到Token对应的Cookie
        从Redis删除Token信息
        设置Cookie过期时间为0（立即失效）*/
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        Cookie[] cookies=request.getCookies();
        if(cookies==null){
            return;
        }
        for(Cookie cookie:cookies){
            if(cookie.getName().equals((Constans.TOKEN_WEB))){
                redisComponent.cleanCheckCode(cookie.getValue());
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                break;
            }
        }

    }
}
