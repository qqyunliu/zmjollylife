package com.easyjava.admin.controller;

import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.query.CategoryInfoquery;
import com.easyjava.entity.po.VideoInfoFilePost;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.VideoInfoFilePostService;
import com.easyjava.utlis.DateUtils;
import com.easyjava.utlis.FFmpegUtils;
import com.easyjava.utlis.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.mapper.StringFieldType;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.concurrent.locks.Condition;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/file")
@RestController
@Slf4j
@Validated
public class FileController extends ABaseController {
    @Resource
    private AppConfig appConfig;

    @Resource
    private FFmpegUtils fFmpegUtils;

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotNull MultipartFile file,@NotNull Boolean createThumbanil) throws IOException, BusinessException {
        String mouth= DateUtils.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
        String folder=appConfig.getProjectFolder()+ Constans.FILE_FOLDER+Constans.FILE_COVER+mouth;
        File folderFile=new File(folder);
        if(!folderFile.exists()){
            folderFile.mkdirs();
        }
        String fileName=file.getOriginalFilename();
        String fileSuffix=StringTools.getFileSuffix(fileName);
        String realFileName= StringTools.getRandomString(Constans.LENGTH_30)+fileSuffix;
        String filePath=folder+"/"+realFileName;
        file.transferTo(new File(filePath));
        if(createThumbanil){
            //生成缩略图
            fFmpegUtils.createImageThumbnail(filePath);

        }
        return getSuccessResponseVO(Constans.FILE_COVER+mouth+"/"+realFileName);
    }

    @RequestMapping("/getResource")
    public void getResource(HttpServletResponse response,@NotNull String sourceName) throws IOException, BusinessException {
        sourceName = URLDecoder.decode(sourceName, StandardCharsets.UTF_8.name());
        if(!StringTools.pathIsOk(sourceName)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String suffix=StringTools.getFileSuffix(sourceName);
        response.setContentType("image/"+suffix.replace(".",""));
        response.setHeader("Cache-control","max-age=2592000");
        readFile(response,sourceName);
    }

    protected void readFile (HttpServletResponse response,String filePath){
        File file =new File(appConfig.getProjectFolder()+Constans.FILE_FOLDER+filePath);
        if(!file.exists()){
            return;
        }
        try(OutputStream out=response.getOutputStream(); FileInputStream in=new FileInputStream(file)){
            byte[] byteDate=new byte[1024];
            int len=0;
            while((len=in.read(byteDate))!=-1){
                out.write(byteDate,0,len);
            }
            out.flush();
        }catch (Exception e){
            log.error("读取文件异常",e);
        }
    }

    private String readTextFile(String filePath) throws IOException {
        File file = new File(appConfig.getProjectFolder() + Constans.FILE_FOLDER + filePath);
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int read = in.read(data);
            if (read <= 0) {
                return "";
            }
            return new String(data, 0, read, StandardCharsets.UTF_8);
        }
    }

    private String normalizeHlsPlaylist(String playlistContent) {
        if (playlistContent == null) {
            return null;
        }
        String[] lines = playlistContent.replace("\r\n", "\n").replace("\r", "\n").split("\n");
        StringBuilder sb = new StringBuilder(playlistContent.length());
        for (String line : lines) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                sb.append(line == null ? "" : line).append("\n");
                continue;
            }
            String seg = trimmed;
            int qIndex = seg.indexOf('?');
            if (qIndex > -1) {
                seg = seg.substring(0, qIndex);
            }
            int slashIndex = Math.max(seg.lastIndexOf('/'), seg.lastIndexOf('\\'));
            if (slashIndex > -1) {
                seg = seg.substring(slashIndex + 1);
            }
            sb.append(seg).append("\n");
        }
        return sb.toString();
    }

    @RequestMapping("/videoResourcePost/{fileId}")
    public void videoResourcePost(HttpServletResponse response, @PathVariable @NotEmpty String fileId) {
        response.setContentType("application/vnd.apple.mpegurl");
        videoResourcePostM3u8(response, fileId);
    }

    @RequestMapping("/videoResourcePost/{fileId}/index.m3u8")
    public void videoResourcePostM3u8(HttpServletResponse response, @PathVariable @NotEmpty String fileId) {
        VideoInfoFilePost videoInfoFilePost = videoInfoFilePostService.getVideoInfoFilePostByFileId(fileId);
        if (videoInfoFilePost == null) {
            return;
        }
        String filePath = videoInfoFilePost.getFilePath();
        try {
            String raw = readTextFile(filePath + Constans.M3U8_NAME);
            String normalized = normalizeHlsPlaylist(raw);
            if (normalized == null) {
                return;
            }
            response.setHeader("Cache-control","no-cache");
            response.getOutputStream().write(normalized.getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("读取m3u8异常", e);
        }
    }

    @RequestMapping("/videoResourcePost/{fileId}/{ts}")
    public void videoResourcePostTs(HttpServletResponse response, @PathVariable @NotEmpty String fileId, @PathVariable @NotEmpty String ts) throws BusinessException {
        if (!StringTools.pathIsOk(ts) || ts.contains("/") || ts.contains("\\")) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        VideoInfoFilePost videoInfoFilePost = videoInfoFilePostService.getVideoInfoFilePostByFileId(fileId);
        if (videoInfoFilePost == null) {
            return;
        }
        if (ts.endsWith(".m3u8")) {
            response.setContentType("application/vnd.apple.mpegurl");
        } else if (ts.endsWith(".ts")) {
            response.setContentType("video/mp2t");
        }
        String filePath = videoInfoFilePost.getFilePath();
        readFile(response, filePath + "/" + ts);
    }

}
