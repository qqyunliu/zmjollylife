package com.easyjava.web.controller;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.dto.AiAuditResult;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.UserAction;
import com.easyjava.entity.po.UserFocus;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.po.VideoSeries;
import com.easyjava.entity.query.UserInfoquery;
import com.easyjava.entity.query.VideoInfoPostquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.AiAuditService;
import com.easyjava.service.UserActionService;
import com.easyjava.service.UserFocusService;
import com.easyjava.service.UserInfoService;
import com.easyjava.service.VideoInfoPostService;
import com.easyjava.service.VideoSeriesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:用户中心Controller
 */
@RestController
@RequestMapping("/uhome")
public class UcenterController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private VideoInfoPostService videoInfoPostService;
    
    @Resource
    private UserFocusService userFocusService;
    
    @Resource
    private UserActionService userActionService;
    
    @Resource
    private VideoSeriesService videoSeriesService;
    
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AiAuditService aiAuditService;

    @RequestMapping("/getUserInfo")
    public ResponseVO getUserInfo(String userId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        String currentUserId = tokenUserInfoDto != null ? tokenUserInfoDto.getUserId() : null;
        
        UserInfoquery userInfoQuery = new UserInfoquery();
        userInfoQuery.setUserId(userId);
        List<UserInfo> userInfoList = userInfoService.findListByParam(userInfoQuery);
        
        if (userInfoList.isEmpty()) {
            return getSuccessResponseVO(null);
        }
        
        UserInfo userInfo = userInfoList.get(0);
        Integer focusCount = userFocusService.getFocusCount(userId);
        Integer fansCount = userFocusService.getFansCount(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userInfo", userInfo);
        result.put("focusCount", focusCount);
        result.put("fansCount", fansCount);
        if (currentUserId != null) {
            result.put("haveFocus", userFocusService.isFocused(userId));
        }
        
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/updateUserInfo")
    public ResponseVO updateUserInfo(String nickId, String avatar, String sex, String birthday, String school, String personIntroduction, String noticeInfo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }

        boolean needAudit = nickId != null || personIntroduction != null;
        if (needAudit) {
            StringBuilder auditText = new StringBuilder();
            if (nickId != null) {
                auditText.append("昵称: ").append(nickId).append("\n");
            }
            if (personIntroduction != null) {
                auditText.append("签名: ").append(personIntroduction);
            }
            AiAuditResult auditResult = aiAuditService.auditText(tokenUserInfoDto.getUserId(), auditText.toString());
            if (auditResult == null || !Boolean.TRUE.equals(auditResult.getPassed())) {
                String reason = auditResult == null ? "审核失败" : auditResult.getReason();
                return getErrorResponseVO("昵称/签名审核未通过" + (reason == null || reason.trim().isEmpty() ? "" : (": " + reason)));
            }
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setNickId(nickId);
        userInfo.setAvatar(avatar);
        userInfo.setSex(sex);
        userInfo.setSchool(school);
        userInfo.setPersonIntroduction(personIntroduction);
        userInfo.setNoticeInfo(noticeInfo);
        userInfoService.updateUserInfoByUserId(userInfo, tokenUserInfoDto.getUserId());
        if (nickId != null) {
            tokenUserInfoDto.setNickId(nickId);
        }
        if (avatar != null) {
            tokenUserInfoDto.setAvatar(avatar);
        }
        redisComponent.updateTokenInfo(tokenUserInfoDto);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(Integer pageNo, Integer status) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(null);
        }
        VideoInfoPostquery query = new VideoInfoPostquery();
        query.setUserId(tokenUserInfoDto.getUserId());
        if (status != null) {
            query.setStatus(status);
        }
        query.setOrderBy("create_time desc");
        query.setPageNo(pageNo);
        query.setPageSize(20);
        PaginationResultVO resultVO = videoInfoPostService.findListByPage(query);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/loadUserCollection")
    public ResponseVO loadUserCollection(Integer pageNo) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        List<UserAction> collectList = userActionService.getUserActionList(tokenUserInfoDto.getUserId(), 3);
        return getSuccessResponseVO(collectList);
    }

    @RequestMapping("/saveTheme")
    public ResponseVO saveTheme(Integer theme) {
        return getSuccessResponseVO(null);
    }
}
