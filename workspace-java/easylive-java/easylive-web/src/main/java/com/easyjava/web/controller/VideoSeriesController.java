package com.easyjava.web.controller;

import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.po.VideoSeries;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.service.VideoSeriesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:视频系列Controller
 */
@RestController
@RequestMapping("/uhome/series")
public class VideoSeriesController extends ABaseController {

    @Resource
    private VideoSeriesService videoSeriesService;

    @RequestMapping("/loadVideoSeries")
    public ResponseVO loadVideoSeries() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        List<VideoSeries> seriesList = videoSeriesService.getVideoSeriesList(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(seriesList);
    }

    @RequestMapping("/saveVideoSeries")
    public ResponseVO saveVideoSeries(String seriesName, String seriesCover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        videoSeriesService.saveVideoSeries(tokenUserInfoDto.getUserId(), seriesName, seriesCover);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/changeVideoSeriesSort")
    public ResponseVO changeVideoSeriesSort(Integer seriesId, String videoId, Integer sort) {
        videoSeriesService.changeVideoSeriesSort(seriesId, videoId, sort);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getVideoSeriesDetail")
    public ResponseVO getVideoSeriesDetail(Integer seriesId) {
        VideoSeries series = videoSeriesService.getVideoSeriesDetail(seriesId);
        return getSuccessResponseVO(series);
    }

    @RequestMapping("/delVideoSeries")
    public ResponseVO delVideoSeries(Integer seriesId) {
        videoSeriesService.deleteVideoSeries(seriesId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/saveSeriesVideo")
    public ResponseVO saveSeriesVideo(Integer seriesId, String videoId) {
        videoSeriesService.addVideoToSeries(seriesId, videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delSeriesVideo")
    public ResponseVO delSeriesVideo(Integer seriesId, String videoId) {
        videoSeriesService.removeVideoFromSeries(seriesId, videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadAllVideo")
    public ResponseVO loadAllVideo(Integer seriesId) {
        List<VideoInfo> videoList = videoSeriesService.getSeriesVideoList(seriesId);
        return getSuccessResponseVO(videoList);
    }

    @RequestMapping("/loadVideoSeriesWithVideo")
    public ResponseVO loadVideoSeriesWithVideo() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getErrorResponseVO("请先登录");
        }
        List<VideoSeries> seriesList = videoSeriesService.getVideoSeriesList(tokenUserInfoDto.getUserId());
        for (VideoSeries series : seriesList) {
            List<VideoInfo> videoList = videoSeriesService.getSeriesVideoList(series.getSeriesId());
            series.setSeriesCover(videoList.isEmpty() ? null : videoList.get(0).getVideoCover());
        }
        return getSuccessResponseVO(seriesList);
    }
}
