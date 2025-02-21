package com.camellya.gbt32960_3_tcp_server.constant.enums;

import lombok.Getter;

@Getter
public enum AckEnum {
    SUCCESS(1, "成功"),
    FAIL(2, "错误"),
    NOT_ACK(0xfe, "并非应答")
    ;
    private final Byte code;

    private final String desc;

    AckEnum(int code, String desc) {
        this.code = (byte) code;
        this.desc = desc;
    }
}
