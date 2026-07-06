package com.easyjava.entity.query;

import com.easyjava.entity.po.VideoComment;

/**
 * @Description:视频评论查询
 */
public class VideoCommentquery extends VideoComment {
    
    private Integer pageNo;
    private Integer pageSize;
    private Integer sortType;
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
    
    public Integer getSortType() {
        return sortType;
    }
    
    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }
    
    public Integer getOffset() {
        return offset;
    }
    
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}