package com.camellya.gbt32960_3_tcp_server.enums;

import lombok.Getter;

@Getter
public enum HttpResult {
    SUCCESS(200, "成功"),
    FAIL(500, "失败"),
    ERROR(600, "异常")
    ;

    private final int code;

    private final String msg;

    HttpResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
