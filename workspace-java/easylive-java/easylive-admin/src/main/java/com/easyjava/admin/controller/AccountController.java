package com.easyjava.admin.controller;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.exception.BusinessException;

import com.easyjava.utlis.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;


/**
 * @Description:用户信息Controller
 * @auther:哈哈哈
 * @date:2025/07/31
 */
@Validated
@RestController
@RequestMapping
public class AccountController extends ABaseController {


    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

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

    @RequestMapping("/login")
    public ResponseVO login(HttpServletResponse response,
                            HttpServletRequest request,
                            String account,
                            String password,
                            String checkCodeKey,
                            String checkCode) throws BusinessException {

        // 参数验证
        if (StringTools.isEmpty(checkCode)) {
            throw new BusinessException("验证码不能为空");
        }
        if (StringTools.isEmpty(account)) {
            throw new BusinessException("账号不能为空");
        }
        if (StringTools.isEmpty(password)) {
            throw new BusinessException("密码不能为空");
        }
        if (StringTools.isEmpty(checkCodeKey)) {
            throw new BusinessException("验证码key不能为空");
        }

        try {
            // 验证码检查
            String serverCheckCode = redisComponent.getCheckCode(checkCodeKey);
            if (StringTools.isEmpty(serverCheckCode)) {
                throw new BusinessException("验证码已失效，请重新获取");
            }
            if (!checkCode.equalsIgnoreCase(serverCheckCode)) {
                throw new BusinessException("图片验证码不正确");
            }

            // 账号密码验证
            String adminAccount = appConfig.getAdminAccount();
            String adminPassword = appConfig.getAdminPassword();

            if (StringTools.isEmpty(adminAccount) || StringTools.isEmpty(adminPassword)) {
                throw new BusinessException("系统错误：管理员配置异常");
            }

            if (!account.equals(adminAccount)) {
                throw new BusinessException("账号或密码错误");
            }

            // ✅ 关键修复：password已经是MD5了，直接比较，不要再次加密
            if (!password.equals(adminPassword)) {
                throw new BusinessException("账号或密码错误");
            }

            // 生成token
            String token = redisComponent.saveTokenInfo4Admin(account);
            if (StringTools.isEmpty(token)) {
                throw new BusinessException("系统错误：token生成失败");
            }

            saveToken2Cookie(response, token);
            return getSuccessResponseVO(account);

        } finally {
            // 清理验证码
            if (!StringTools.isEmpty(checkCodeKey)) {
                redisComponent.cleanCheckCode(checkCodeKey);
            }

            // 清理旧token
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (Constans.TOKEN_ADMIN.equals(cookie.getName())) {
                        String oldToken = cookie.getValue();
                        if (!StringTools.isEmpty(oldToken)) {
                            redisComponent.cleanToken4Admin(oldToken);
                        }
                        break;
                    }
                }
            }
        }
    }

    @RequestMapping("/logout")
    public ResponseVO logout(HttpServletResponse response){
      cleanCookie(response);
      return getSuccessResponseVO(null);
    }

}