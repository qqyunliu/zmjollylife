package com.easyjava.entity.vo;

import java.util.ArrayList;
import java.util.List;
/*这是一个分页结果封装类 PaginationResultVO，它用于统一封装分页查询的返回结果*/
public class PaginationResultVO<T> {
    private Integer totalCount;
    private Integer allCount;
    private Integer pageNo;
    private Integer pageSize;
    private Integer pageTotal;
    private List<T> list = new ArrayList<T>();// 当前页数据列表


    public PaginationResultVO(Integer totalCount, Integer pageNo, Integer pageSize, List<T> list) {
        /*自动计算总页数，需要外部传入总记录数、页码、页大小和数据列表*/
        this.totalCount = totalCount;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.list = list;
    }

    public PaginationResultVO(Integer totalCount, Integer pageNo, Integer pageSize, Integer pageTotal, List<T> list) {
        /*允许外部直接传入所有参数，包括计算好的总页数*/
        this.totalCount = totalCount;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.pageTotal = pageTotal;
        this.list = list;
    }

    public PaginationResultVO() {

    }

    public PaginationResultVO(List<T> list) {
        this.list = list;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getAllCount() {
        return allCount;
    }

    public void setAllCount(Integer allCount) {
        this.allCount = allCount;
    }
    
    public Integer getTotal() {
        return totalCount;
    }
    
    public void setTotal(Integer total) {
        this.totalCount = total;
    }
    
    public Integer getPageNo() {
        return pageNo;
    }
    
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
