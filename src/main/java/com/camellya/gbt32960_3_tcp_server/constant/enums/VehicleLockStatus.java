package com.camellya.gbt32960_3_tcp_server.constant.enums;

import lombok.Getter;

@Getter
public enum VehicleLockStatus {
    NOT_LOCK("0", "未锁车", 0xff),
    SENDING_LOCK("1", "下发锁车", 0xfe),
    WAIT_LOCKED("2", "等待锁车生效", 0xfe),
    LIMIT_10KM("3", "限速10KM/h", 0x0A),
    LIMIT_5KM("4", "限速5KM/h", 0x05),
    LIMIT_0KM("5", "限速0KM/h", 0),
    SENDING_UNLOCK("6", "下发解锁", 0xff),
    WAIT_UNLOCKED("7", "等待解锁生效", 0xfe),
    UNKNOWN("0", "无效值(默认为未锁车)", 0xfe);
    ;
    // 服务器下发的值
    private final String code;
    private final String desc;
    // 下发给tbox的值
    private final Byte commandCode;

    VehicleLockStatus(String code, String desc, int commandCode) {
        this.code = code;
        this.desc = desc;
        this.commandCode = (byte) commandCode;
    }

    public static VehicleLockStatus getByCode(String code) {
        for (VehicleLockStatus status : VehicleLockStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return UNKNOWN;
    }

    public static VehicleLockStatus getByCommandCode(byte commandCode) {
        return switch (commandCode) {
            case (byte) 0x0A -> LIMIT_10KM;
            case (byte) 0x00 -> LIMIT_0KM;
            default -> NOT_LOCK;
        };
    }
}
