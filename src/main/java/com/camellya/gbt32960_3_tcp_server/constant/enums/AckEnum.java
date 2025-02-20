package com.camellya.gbt32960_3_tcp_server.constant.enums;

import lombok.Getter;

@Getter
public enum AckEnum {
    SUCCESS(1, "成功"),
    FAIL(2, "错误"),
    VIN_REPETITION(3, "VIN重复"),
    VIN_NOT_EXIST(4, "不存在此vin号的设备"),
    HAS_LOGIN(5, "已登录"),
    VERIFY_ERROR(6, "VIN和ICCID不匹配"),
    NOT_ACK(0xfe, "并非应答")
    ;
    private final Byte code;

    private final String desc;

    AckEnum(int code, String desc) {
        this.code = (byte) code;
        this.desc = desc;
    }

    public static AckEnum getAckEnum(int code) {
        AckEnum[] values = AckEnum.values();
        for (AckEnum value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return FAIL;
    }

}
