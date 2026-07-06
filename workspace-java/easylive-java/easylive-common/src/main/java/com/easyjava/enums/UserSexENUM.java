package com.easyjava.enums;

public enum UserSexENUM {
    FEMALE(0, "女"),
    MALE(1, "男"),
    UNKNOWN(2, "保密");

    private Integer type;
    private String desc;

    UserSexENUM(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据代码获取性别枚举
     */
    public static UserSexENUM getByType(Integer type) {
        for (UserSexENUM item : UserSexENUM.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }


    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}

