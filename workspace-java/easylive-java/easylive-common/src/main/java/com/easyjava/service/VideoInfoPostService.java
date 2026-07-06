package com.easyjava.service;

import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.VideoInfoPost;
import com.easyjava.entity.query.VideoInfoPostquery;
import com.easyjava.exception.BusinessException;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:视频信息Service
 * @auther:哈哈哈
 * @date:2025/08/16
 */
public interface VideoInfoPostService {

    /**
     * 根据条件查询列表
     */
    List<VideoInfoPost> findListByParam(VideoInfoPostquery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(VideoInfoPostquery query);

    /**
     * 分页查询
     */
    PaginationResultVO<VideoInfoPost> findListByPage(VideoInfoPostquery query);

    /**
     * 新增
     */
    Integer add(VideoInfoPost Bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<VideoInfoPost> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<VideoInfoPost> listBean);

    /**
     * 根据VideoId查询
     */
    VideoInfoPost getVideoInfoPostByVideoId(String videoId);

    /**
     * 根据VideoId更新
     */
    Integer updateVideoInfoPostByVideoId(VideoInfoPost bean, String videoId);

    /**
     * 根据VideoId删除
     */
    Integer deleteVideoInfoPostByVideoId(String videoId);

    void deleteVideo(String videoId, String userId) throws BusinessException;

    void saveVideoInfo(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> filePostList) throws BusinessException;


    void transferVideoFile(VideoInfoFilePost videoInfoFilePost);

    void auditVideo(String videoId, Integer status, String reason) throws BusinessException;

    void transferToVideoInfo(String videoId) throws BusinessException;
}
