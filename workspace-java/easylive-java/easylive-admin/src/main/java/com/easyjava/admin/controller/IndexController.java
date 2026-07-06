package com.easyjava.admin.controller;

import com.easyjava.entity.po.Info;
import com.easyjava.entity.po.VideoComment;
import com.easyjava.entity.po.VideoInfo;
import com.easyjava.entity.query.Infoquery;
import com.easyjava.mappers.InfoMapper;
import com.easyjava.mappers.VideoCommentMapper;
import com.easyjava.mappers.VideoInfoMapper;
import com.easyjava.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController extends ABaseController {

    @Resource
    private InfoMapper<Info, Infoquery> infoMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, com.easyjava.entity.query.VideoInfoquery> videoInfoMapper;

    @Resource
    private VideoCommentMapper<VideoComment, com.easyjava.entity.query.VideoCommentquery> videoCommentMapper;

    @RequestMapping("/getActualTimaStatisticsInfo")
    public ResponseVO getActualTimaStatisticsInfo() {
        Integer userCount = infoMapper.selectCount(new Infoquery());
        Integer playCount = videoInfoMapper.sumPlayCountAll();
        Integer likeCount = videoInfoMapper.sumLikeCountAll();
        Integer commentCount = videoCommentMapper.selectAllCount();

        Map<String, Object> result = new HashMap<>();
        result.put("userCount", userCount == null ? 0 : userCount);
        result.put("playCount", playCount == null ? 0 : playCount);
        result.put("likeCount", likeCount == null ? 0 : likeCount);
        result.put("commentCount", commentCount == null ? 0 : commentCount);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/getWeekStatisticsInfo")
    public ResponseVO getWeekStatisticsInfo() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<Map<String, Object>> rows = infoMapper.selectJoinCountByDateRange(startDate.toString(), endDate.toString());
        Map<String, Integer> dayCountMap = new HashMap<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Object dayObj = row.get("day");
                Object cntObj = row.get("cnt");
                if (dayObj == null) {
                    continue;
                }
                String day = String.valueOf(dayObj);
                Integer cnt;
                if (cntObj instanceof Number) {
                    cnt = ((Number) cntObj).intValue();
                } else {
                    try {
                        cnt = Integer.parseInt(String.valueOf(cntObj));
                    } catch (Exception e) {
                        cnt = 0;
                    }
                }
                dayCountMap.put(day, cnt);
            }
        }

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = startDate.plusDays(i);
            String dayStr = day.toString();
            labels.add(dayStr);
            data.add(dayCountMap.getOrDefault(dayStr, 0));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);
        return getSuccessResponseVO(result);
    }
}
