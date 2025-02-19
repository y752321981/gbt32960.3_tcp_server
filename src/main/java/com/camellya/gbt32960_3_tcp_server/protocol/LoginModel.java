package com.camellya.gbt32960_3_tcp_server.protocol;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class LoginModel {

    private final TimeModel time;

    /**
     * 流水号
     */
    private final Character serialNumber;

    /**
     * ICCID
     */
    private final String iccid;

    /**
     * 可充电储能子系统数
     */
    private final Byte rechargeNumber;

    /**
     * 可充电储能子系统编码长度
     */
    private final Byte rechargeCodeLength;

    /**
     * 可充电储能子系统编码
     */
    private List<String> rechargeCodes;

    public LoginModel(List<Byte> bytes) {
        time = new TimeModel(bytes);
        serialNumber = (char) (((bytes.get(6) & 0xFF) << 8 ) | (bytes.get(7) & 0xFF));
        Byte[] array1 = bytes.subList(8, 28).toArray(Byte[]::new);
        byte[] bytes1 = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            bytes1[i] = array1[i];
        }
        iccid = new String(bytes1);
        rechargeNumber = bytes.get(28);
        rechargeCodeLength = bytes.get(29);
        if (rechargeCodeLength > 0) {
            int length = rechargeCodeLength * rechargeNumber;
            if (bytes.size() < length + 10) {
                return;
            }
            Byte[] array2 = bytes.subList(30, 30 + length).toArray(Byte[]::new);
            byte[] bytes2 = new byte[length];
            for (int i = 0; i < length; i++) {
                bytes2[i] = array2[i];
            }
            String codeString = new String(bytes2);
            rechargeCodes = new LinkedList<>();
            for (int i = 0; i < rechargeNumber; i++) {
                rechargeCodes.add(codeString.substring(i * rechargeCodeLength, (i + 1) * rechargeCodeLength - 1));
            }
        }
    }
}
