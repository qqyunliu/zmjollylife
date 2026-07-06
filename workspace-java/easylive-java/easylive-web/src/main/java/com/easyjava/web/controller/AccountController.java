package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.component.RedisComponent;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.InfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.http.HttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;


/**
 * @Description:用户信息Controller
 * @auther:哈哈哈
 * @date:2025/07/31
 *
这个控制器提供了用户注册、登录、自动登录、退出登录和验证码获取等功能。*/
@Validated
@RestController
@RequestMapping("/account")
public class AccountController extends ABaseController {

    @Resource
    private InfoService infoService;

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/checkCode")
    public ResponseVO checkCode(HttpSession session) {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        String checkCodeBase64 = captcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("checkCode", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);

        return getSuccessResponseVO(result);
    }

    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty @Email @Size(max = 150) String email,
                               @NotEmpty @Size(max = 20) String nickName,
                               @NotEmpty @Pattern(regexp = Constans.REGEX_PASSWORD, message = "密码格式不正确") String registerPassword,
                               @NotEmpty String checkCodeKey,
                               @NotEmpty String checkCode) throws BusinessException {
        try {
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码不正确");
            }
            infoService.register(email, nickName, registerPassword);
            return getSuccessResponseVO(null);
        } finally {
            redisComponent.cleanCheckCode(checkCodeKey);
        }

    }


    @RequestMapping("/login")
    public ResponseVO login(HttpServletResponse response,
                            HttpServletRequest request,
                            @NotEmpty String email,
                            @NotEmpty String password,
                            @NotEmpty String checkCodeKey,
                            @NotEmpty String checkCode) throws BusinessException {
        try {
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码不正确");
            }
            String ip = getIpAddr();
            TokenUserInfoDto tokenUserInfoDto = infoService.login(email, password, ip);
            saveToken2Cookie(response, tokenUserInfoDto.getToken());
            //TODO 粉丝数,关注数,硬币数
            return getSuccessResponseVO(tokenUserInfoDto);
        } finally {
            redisComponent.cleanCheckCode(checkCodeKey);
        }

    }


    @RequestMapping("/autoLogin")
    public ResponseVO autoLogin(HttpServletResponse response){
        TokenUserInfoDto tokenUserInfoDto=getTokenUserInfoDto();
        if(tokenUserInfoDto==null){
            return getSuccessResponseVO(null);
        }
        if(tokenUserInfoDto.getExpireAt()-System.currentTimeMillis()<Constans.REDIS_KEY_EXPIRES_ONE_DAY){
            redisComponent.saveTokenInfo(tokenUserInfoDto);
            saveToken2Cookie(response,tokenUserInfoDto.getToken());

        }
        saveToken2Cookie(response,tokenUserInfoDto.getToken());
        return getSuccessResponseVO(tokenUserInfoDto);

    }

    @RequestMapping("/logout")
    public ResponseVO logout(HttpServletResponse response){
      cleanCookie(response);
      return getSuccessResponseVO(null);
    }

}