package com.easyjava.entity.query;
/*这是一个基础查询参数类 BaseQuery，它为所有数据查询提供了统一的分页和排序参数封装*/
public class BaseQuery {
    private SimplePage simplePage;/*分页对象*/
    private Integer pageNo;
    private Integer pageSize;
    private String orderBy;


    public SimplePage getSimplePage() {
        return simplePage;
    }

    public void setSimplePage(SimplePage simplePage) {
        this.simplePage = simplePage;
    }

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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
