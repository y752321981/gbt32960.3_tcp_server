package com.camellya.gbt32960_3_tcp_server.constant.enums;

import lombok.Getter;

@Getter
public enum MessageType {
    LOGIN(0, "设备登入"),
    LOGOUT(1, "设备登出"),

    REPORT_LOCATION(2, "设备上报定位数据"),
    REPORT_VEHICLE(3, "设备上报整车数据"),
    REPORT_DRIVER_MOTOR(4, "设备上报驱动电机数据"),
    REPORT_LOCATION_AGAIN(5, "设备补发定位数据"),

    LOCK_UNLOCK_COMMAND(10, "限速/解除命令"),
    UNKNOWN(99, "未知消息")
    ;
    private final int code;

    private final String desc;

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MessageType getMessageType(int code) {
        for (MessageType type : MessageType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
