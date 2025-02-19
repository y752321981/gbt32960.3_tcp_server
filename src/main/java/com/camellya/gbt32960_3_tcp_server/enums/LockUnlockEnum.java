package com.camellya.gbt32960_3_tcp_server.enums;

import lombok.Getter;

@Getter
public enum LockUnlockEnum {

    LOCK_UNLOCK_COMMAND(0x40, "远程限速/解除命令", 1),
    LOCK_UNLOCK_ACK(0x41, "远程限速/解除命令接收应答", 1),
    LOCK_UNLOCK_REPORT(0x42, "远程限速/解除命令执行结果上报", 3)
;

    private final Byte code;

    private final String desc;

    private final Integer contentLength;


    LockUnlockEnum(int code, String desc, Integer contentLength) {
        this.code = (byte) code;
        this.desc = desc;
        this.contentLength = contentLength;
    }


    public static LockUnlockEnum getByCode(byte code) {
        LockUnlockEnum[] values = LockUnlockEnum.values();
        for (LockUnlockEnum value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
