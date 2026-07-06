package com.easyjava.service;

import com.easyjava.entity.po.VideoDanmu;
import java.util.List;

/**
 * @Description:视频弹幕Service接口
 */
public interface VideoDanmuService {
    
    void saveDanmu(String videoId, String fileId, String content, String color, Integer time, String userId);
    
    List<VideoDanmu> getDanmuList(String videoId);
    
    List<VideoDanmu> getDanmuListByFileId(String fileId);

    List<VideoDanmu> getDanmuListByUserId(String userId, String keyword, Integer pageNo, Integer pageSize);

    Integer getDanmuCountByUserId(String userId, String keyword);

    void deleteDanmu(Integer danmuId);
}