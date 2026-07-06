package com.easyjava.web.task;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.redis.RedisUtils;
import com.easyjava.service.VideoInfoPostService;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ExecuteQueueTask {
    private ExecutorService executorService= Executors.newFixedThreadPool(Constans.LENGTH_2);
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private VideoInfoPostService videoInfoPostService;

    private volatile boolean running = true;

    @PostConstruct
    public void consumTrasnferFileQueue(){
        executorService.execute(()->{
            while (running){
                try{
                    VideoInfoFilePost videoInfoFilePost=redisComponent.getFileFromTransferQueue();
                    if(videoInfoFilePost==null){
                        Thread.sleep(1500);
                        continue;
                    }
                    videoInfoPostService.transferVideoFile(videoInfoFilePost);
                }catch (Exception e){
                    log.error("获取转码文件队列信息失败",e);
                    if(e.getMessage() != null && e.getMessage().contains("destroyed")) {
                        log.warn("Redis连接已销毁，停止任务");
                        break;
                    }
                }
            }
        });
    }

    @javax.annotation.PreDestroy
    public void shutdown() {
        running = false;
        executorService.shutdown();
    }

    /*@PostConstruct
    public void consumFileQueue(){
        executorService.execute(()->{
            while (true){
                try{
                    VideoInfoFilePost videoInfoFilePost=redisComponent.getFileFromTransferQueue();
                    if(videoInfoFilePost==null){
                        Thread.sleep(1500);
                        continue;
                    }
                    videoInfoPostService.transferVideoFile(videoInfoFilePost);
                }catch (Exception e){
                    log.error("获取转码文件队列信息失败",e);
                }
            }
        });
    }*/
}
