package com.camellya.gbt32960_3_tcp_server.utils;

import java.util.List;

public class ByteUtil {

    /**
     * 字节数组转字符串
     * @param bytes 字节数组
     * @param length 待转换长度
     * @return 字符串
     */
    public static String byteArrayToString(List<Byte> bytes, int length) {
        if (bytes.size() < length) {
            return "";
        }
        byte[] byteArray = new byte[length];
        for (int i = 0; i < length; i++) {
            byteArray[i] = bytes.get(i);
        }
        return new String(byteArray);
    }

    /**
     * 字节数组转2字节无符号数
     * @param bytes 字节数组
     * @return 2字节无符号数
     */
    public static char byteArrayToChar(List<Byte> bytes) {
        // 检查数组长度，确保有2个字节
        if (bytes.size() < 2) {
            return 0;
        }

        // 按位转换为char
        char value = 0;
        for (int i = 0; i < 2; i++) {
            value = (char) ((value << 8) | (bytes.get(i) & 0xFF));
        }
        return value;
    }

    /**
     * 字节数组转4字节无符号数
     * @param bytes 字节数组
     * @return 4字节无符号数
     */
    public static long byteArrayToUnsignedInt(List<Byte> bytes) {
        // 检查数组长度，确保有4个字节
        if (bytes.size() < 4) {
            return 0;
        }

        // 按位转换为long
        long value = 0;
        for (int i = 0; i < 4; i++) {
            value =  (value << 8) | (bytes.get(i) & 0xFF);
        }
        return value;
    }

    public static boolean byteArrayEquals(byte[] bytes1, byte[] bytes2) {
        if (bytes1.length != bytes2.length) {
            return false;
        }
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }

}
