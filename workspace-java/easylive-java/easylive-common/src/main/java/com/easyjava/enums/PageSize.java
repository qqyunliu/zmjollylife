package com.easyjava.enums;
/*这是一个分页大小枚举类 PageSize，它定义了系统中可用的分页大小选项*/
public enum PageSize {
    SIZE15(15), SIZE20(20), SIZE30(30), SIZE40(40), SIZE50(50);
    Integer size;


    private PageSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        return this.size;
    }

}
