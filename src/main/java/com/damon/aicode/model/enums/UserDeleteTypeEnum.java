package com.damon.aicode.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserDeleteTypeEnum {

    SELF("self", "self"),
    ADMIN("admin", "admin");

    private final String text;

    private final String value;

    UserDeleteTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static UserDeleteTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserDeleteTypeEnum anEnum : UserDeleteTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
