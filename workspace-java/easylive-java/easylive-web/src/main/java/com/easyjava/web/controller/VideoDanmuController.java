package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.VideoDanmu;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.service.VideoDanmuService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:视频弹幕Controller
 */
@RestController
@RequestMapping("/danmu")
public class VideoDanmuController extends ABaseController {

    @Resource
    private VideoDanmuService videoDanmuService;

    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(String videoId, String fileId) {
        List<VideoDanmu> danmuList;
        if (fileId != null && !fileId.isEmpty()) {
            danmuList = videoDanmuService.getDanmuListByFileId(fileId);
        } else {
            danmuList = videoDanmuService.getDanmuList(videoId);
        }
        return getSuccessResponseVO(danmuList);
    }

    @RequestMapping("/postDanmu")
    public ResponseVO postDanmu(String videoId, String fileId, String content, String color, Integer time) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        videoDanmuService.saveDanmu(videoId, fileId, content, color, time, tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(Integer danmuId) {
        videoDanmuService.deleteDanmu(danmuId);
        return getSuccessResponseVO(null);
    }
}