package com.camellya.gbt32960_3_tcp_server.constant.enums;

import lombok.Getter;

@Getter
public enum CommandEnum {

    VEHICLE_LOGIN(1, "车辆登入"),
    REPORT_INFO_REAL(2, "实时信息上报"),
    REPORT_INFO_RESEND(3, "补发信息上报"),
    VEHICLE_LOGOUT(4, "车辆登出"),
    PLATFORM_LOGIN(5, "平台登入"),
    PLATFORM_LOGOUT(6, "平台登出"),
    HEARTBEAT(7, "心跳"),
    TERMINAL_TIMING(8, "终端校时"),
    GET_PARAM(0x80, "参数查询"),
    SET_PARAM(0x81, "设置命令"),
    TERMINAL_CONTROL(0x82, "车载终端控制"),
    UNKNOWN(0xff, "未知指令");

    private final Byte code;

    private final String desc;

    CommandEnum(int code, String desc) {
        this.code = (byte) code;
        this.desc = desc;
    }

    public static CommandEnum getCommandEnum(int code) {
        CommandEnum[] values = CommandEnum.values();
        for (CommandEnum value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return UNKNOWN;
    }

}
