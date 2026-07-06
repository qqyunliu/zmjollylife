package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.PlayHistory;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.service.PlayHistoryService;
import com.easyjava.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:播放历史Controller
 */
@RestController
@RequestMapping("/history")
public class PlayHistoryController extends ABaseController {

    @Resource
    private PlayHistoryService playHistoryService;
    
    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/loadHistory")
    public ResponseVO loadHistory() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        List<PlayHistory> historyList = playHistoryService.getPlayHistoryList(tokenUserInfoDto.getUserId());
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (historyList != null) {
            for (PlayHistory history : historyList) {
                VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(history.getVideoId());
                if (videoInfo != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("videoInfo", videoInfo);
                    item.put("progress", history.getProgress());
                    item.put("playTime", history.getPlayTime());
                    item.put("id", history.getId());
                    resultList.add(item);
                }
            }
        }
        
        return getSuccessResponseVO(resultList);
    }

    @RequestMapping("/delHistory")
    public ResponseVO delHistory(@NotEmpty Integer id) {
        playHistoryService.deletePlayHistory(id);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cleanHistory")
    public ResponseVO cleanHistory() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        playHistoryService.cleanPlayHistory(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
