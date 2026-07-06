package com.easyjava.entity.vo;
/*这是一个统一的API响应封装类 ResponseVO<T>，它为标准化的API响应提供了数据结构*/
public class ResponseVO<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
