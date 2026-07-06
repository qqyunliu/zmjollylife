package com.easyjava.entity.vo;
/*这是一个视频状态统计信息封装类 VideoStatusCountInfoVO，它用于统计和展示视频在不同审核状态下的数量*/
public class VideoStatusCountInfoVO {
    private Integer auditPassCount;
    private Integer auditFailCount;
    private Integer inProgress;

    public Integer getAuditPassCount() {
        return auditPassCount;
    }

    public void setAuditPassCount(Integer auditPassCount) {
        this.auditPassCount = auditPassCount;
    }

    public Integer getAuditFailCount() {
        return auditFailCount;
    }

    public void setAuditFailCount(Integer auditFailCount) {
        this.auditFailCount = auditFailCount;
    }

    public Integer getInProgress() {
        return inProgress;
    }

    public void setInProgress(Integer inProgress) {
        this.inProgress = inProgress;
    }
}
