package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.UserMessage;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.service.UserMessageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:用户消息Controller
 */
@RestController
@RequestMapping("/message")
public class UserMessageController extends ABaseController {

    @Resource
    private UserMessageService userMessageService;

    @RequestMapping("/loadMessage")
    public ResponseVO loadMessage() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        List<UserMessage> messageList = userMessageService.getMessageList(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(messageList);
    }

    @RequestMapping("/getNoReadCount")
    public ResponseVO getNoReadCount() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        Integer count = userMessageService.getNoReadCount(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(count);
    }

    @RequestMapping("/getNoReadCountGroup")
    public ResponseVO getNoReadCountGroup() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        Map<Integer, Integer> counts = userMessageService.getNoReadCountGroup();
        return getSuccessResponseVO(counts);
    }

    @RequestMapping("/readAll")
    public ResponseVO readAll() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        userMessageService.readAllMessage(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delMessage")
    public ResponseVO delMessage(Integer messageId) {
        userMessageService.deleteMessage(messageId);
        return getSuccessResponseVO(null);
    }
}
