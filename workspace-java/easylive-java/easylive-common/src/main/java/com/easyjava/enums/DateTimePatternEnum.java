package com.easyjava.enums;
/*这是一个日期时间格式模式枚举类 DateTimePatternEnum，它定义了系统中常用的日期时间格式化模式*/
public enum DateTimePatternEnum {
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), YYYY_MM_DD("yyyy-MM-dd"), YYYYMM("yyyyMM"), YYYYMMDD("yyyyMMdd");
    private String pattern;

    DateTimePatternEnum(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
/*1. 统一格式管理
将系统中常用的日期时间格式集中管理，避免在代码中硬编码字符串格式，提高一致性和可维护性。

2. 语义化枚举命名
使用清晰的枚举名称来描述格式的用途，而不是直接使用格式字符串。*/