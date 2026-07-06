package com.easyjava.admin.controller;

import com.easyjava.entity.query.VideoInfoPostquery;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.VideoInfoFilePostService;
import com.easyjava.service.VideoInfoFileService;
import com.easyjava.service.VideoInfoPostService;
import com.easyjava.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.aggregations.bucket.composite.GeoTileGridValuesSourceBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RequestMapping("/videoInfo")
@RestController
@Validated
public class VideoInfoController extends ABaseController{

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(VideoInfoPostquery videoInfoPostquery){
        videoInfoPostquery.setOrderBy("v.last_update_time desc");
        videoInfoPostquery.setQueryCountInfo(true);
        videoInfoPostquery.setQueryUserInfo(true);
        PaginationResultVO resultVO=videoInfoPostService.findListByPage(videoInfoPostquery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/auditVideo")
    public ResponseVO auditVideo(@NotEmpty String videoId, @NotNull Integer status,String reason) throws BusinessException {
        videoInfoPostService.auditVideo(videoId,status,reason);
        return getSuccessResponseVO(null);
    }

}
