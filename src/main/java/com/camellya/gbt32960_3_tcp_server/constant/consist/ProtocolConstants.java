package com.camellya.gbt32960_3_tcp_server.constant.consist;

public class ProtocolConstants {

    public static final byte[] START = { 0x23, 0x23 };

    /**
     * 不加密
     */
    public static final byte ENCRYPT_MODE_NULL = 1;

    /**
     * RSA加密
     */
    public static final byte ENCRYPT_MODE_RSA = 2;

    /**
     * AES128加密
     */
    public static final byte ENCRYPT_MODE_AES = 3;

    /**
     * 异常
     */
    public static final byte EXCEPTION = (byte) 0xFE;

    /**
     * 无效
     */
    public static final byte INVALID = (byte) 0xFF;

    /**
     * 成功
     */
    public static final byte ACK_SUCCESS = 1;

    /**
     * 错误
     */
    public static final byte ACK_FAILURE = 2;

    /**
     * VIN 重复
     */
    public static final byte ACK_VIN_DUPLICATION = 3;

    /**
     * 命令包，而非应答包
     */
    public static final byte ACK_NOT = (byte) 0xfe;

}
