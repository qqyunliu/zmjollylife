package com.easyjava.entity.query;

import com.easyjava.entity.po.VideoDanmu;

/**
 * @Description:视频弹幕查询
 */
public class VideoDanmUquery extends VideoDanmu {

    private Integer pageNo;
    private Integer pageSize;
    private Integer offset;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}