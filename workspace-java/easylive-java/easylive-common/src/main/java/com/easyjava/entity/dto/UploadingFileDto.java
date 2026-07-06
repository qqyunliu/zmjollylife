package com.easyjava.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
/*文件上传数据传输对象 UploadingFileDto，它用于管理大文件分片上传的整个过程*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadingFileDto implements Serializable {

    private static final long serialVersionUID = -9021479612471973571L;
    private String uploadId;
    private String fileName;
    private Integer chunkIndex;
    private Integer chunks;
    private long fileSize = 0L;
    private String filePath;


    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
