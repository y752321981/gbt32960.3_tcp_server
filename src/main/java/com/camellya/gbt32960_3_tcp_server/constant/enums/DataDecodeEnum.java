package com.camellya.gbt32960_3_tcp_server.constant.enums;

import lombok.Getter;

@Getter
public enum DataDecodeEnum {
    NONE(1, "数据不加密"),
    RSA(2, "RSA加密"),
    AES(3, "AES128加密"),
    ERROR(0xfe, "异常"),
    INVALID(0xff, "无效")
    ;

    private final byte code;

    private final String desc;

    DataDecodeEnum(int code, String desc) {
        this.code = (byte) code;
        this.desc = desc;
    }

    public static DataDecodeEnum getByCode(byte code) {
        DataDecodeEnum[] values = DataDecodeEnum.values();
        for (DataDecodeEnum value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

}
