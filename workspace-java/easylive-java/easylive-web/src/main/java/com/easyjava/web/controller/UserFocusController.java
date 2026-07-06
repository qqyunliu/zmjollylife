package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.UserFocus;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.service.UserFocusService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:用户关注Controller
 */
@RestController
@RequestMapping("/uhome")
public class UserFocusController extends ABaseController {

    @Resource
    private UserFocusService userFocusService;

    @RequestMapping("/focus")
    public ResponseVO focus(String focusUserId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        userFocusService.focusUser(focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cancelFocus")
    public ResponseVO cancelFocus(String focusUserId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        userFocusService.cancelFocus(focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadFocusList")
    public ResponseVO loadFocusList() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        List<UserFocus> focusList = userFocusService.getFocusList();
        return getSuccessResponseVO(focusList);
    }

    @RequestMapping("/loadFansList")
    public ResponseVO loadFansList() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        List<UserFocus> fansList = userFocusService.getFansList();
        return getSuccessResponseVO(fansList);
    }
}
