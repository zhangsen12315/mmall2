package com.mmall.common;

import lombok.Getter;

@Getter
public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    PARAMETER_ERROR(2,"PARAMETER_ERROR"),
    ILLEGAL_ARGUMENT(3,"illegal argument"),
    ;

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ;

}
