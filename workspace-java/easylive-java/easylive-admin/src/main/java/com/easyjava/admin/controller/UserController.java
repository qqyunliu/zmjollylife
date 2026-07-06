package com.easyjava.admin.controller;

import com.easyjava.entity.po.Info;
import com.easyjava.entity.query.Infoquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.UserStatusEnum;
import com.easyjava.service.InfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController extends ABaseController {

    @Resource
    private InfoService infoService;

    /**
     * 加载用户列表
     */
    @RequestMapping("/loadUser")
    public ResponseVO loadUser(Infoquery query) {
        if (query.getPageNo() == null || query.getPageNo() < 1) {
            query.setPageNo(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setOrderBy("join_time desc");
        PaginationResultVO<Info> result = infoService.findListByPage(query);
        return getSuccessResponseVO(result);
    }

    /**
     * 修改用户状态 (0:禁用 1:启用 -1:注销)
     */
    @RequestMapping("/changeStatus")
    public ResponseVO changeStatus(String userId, Integer status) {
        if (userId == null || status == null) {
            return getErrorResponseVO("参数不能为空");
        }
        UserStatusEnum statusEnum = UserStatusEnum.getByStatus(status);
        if (statusEnum == null) {
            return getErrorResponseVO("无效的状态值");
        }
        try {
            Info updateInfo = new Info();
            updateInfo.setStatus(status);
            if (UserStatusEnum.DELETED.getStatus().equals(status)) {
                updateInfo.setNickId("已注销用户");
            }
            infoService.updateInfoByUserId(updateInfo, userId);
            return getSuccessResponseVO(null);
        } catch (Exception e) {
            log.error("修改用户状态失败, userId: {}, status: {}", userId, status, e);
            return getErrorResponseVO("操作失败");
        }
    }

    private ResponseVO getErrorResponseVO(String message) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(STATUC_ERROR);
        responseVO.setCode(com.easyjava.enums.ResponseCodeEnum.CODE_500.getCode());
        responseVO.setInfo(message);
        return responseVO;
    }
}
