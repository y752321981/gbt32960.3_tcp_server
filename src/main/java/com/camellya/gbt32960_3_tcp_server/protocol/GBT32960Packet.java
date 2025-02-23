package com.camellya.gbt32960_3_tcp_server.protocol;


import com.camellya.gbt32960_3_tcp_server.constant.enums.AckEnum;
import com.camellya.gbt32960_3_tcp_server.constant.enums.DataDecodeEnum;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class GBT32960Packet {

    /**
     * 最小长度，即数据单元长度为0时
     */
    public final static int MIN_LENGTH = 25;

    /**
     * 命令标识
     */
    private Byte commandFlag;

    /**
     * 应答标识
     */
    private Byte ackFlag;

    /**
     * vin号
     */
    private String vin;

    /**
     * 加密方式
     */
    private Byte encryptMode;

    /**
     * 数据单元长度
     */
    private Character dataLength;

    /**
     * 数据单元
     */
    private List<Byte> data;

    /**
     * 校验码
     */
    private Byte verify;

    @Override
    public String toString() {
        return  "commandFlag=" + Integer.toHexString(commandFlag) + ",ackFlag=" + Integer.toHexString(ackFlag) +
                ",VIN=" + vin + ",encryMode=" + Integer.toHexString(encryptMode)+ ",dataLength=" + Integer.valueOf(dataLength) +
                ",data=" + data.stream().map(Integer::toHexString).toList() + ",verify=" + Integer.toHexString(verify);
    }

    public GBT32960Packet makeResponse(AckEnum ackFlag) {
        this.ackFlag = ackFlag.getCode();
        List<Byte> now = TimeModel.getNow();
        this.data = now;
        this.dataLength = (char) now.size();
        this.encryptMode = DataDecodeEnum.NONE.getCode();
        this.verify = this.calcVerifyCode();
        return this;
    }

    public Byte calcVerifyCode() {
        Byte verifyCode = this.commandFlag;
        verifyCode = (byte) (verifyCode ^ this.ackFlag);
        byte[] bytes = vin.getBytes(StandardCharsets.UTF_8);
        for (byte aByte : bytes) {
            verifyCode = (byte) (verifyCode ^ aByte);
        }
        verifyCode = (byte) (verifyCode ^ this.encryptMode);
        verifyCode = (byte) (verifyCode ^ ((this.dataLength >> 8) & 0xFF));
        verifyCode = (byte) (verifyCode ^ (this.dataLength & 0xFF));
        for (byte aByte : this.data) {
            verifyCode = (byte) (verifyCode ^ aByte);
        }
        return verifyCode;
    }

    public Byte[] toProtocolBytes() {
        Byte[] bytes = new Byte[this.data.size() + MIN_LENGTH];
        bytes[0] = '#';
        bytes[1] = '#';
        bytes[2] = commandFlag;
        bytes[3] = ackFlag;
        for (int i = 4; i < 21; i++) {
            bytes[i] = (byte) vin.charAt(i - 4);
        }
        bytes[21] = encryptMode;
        bytes[22] = (byte) (dataLength >> 8);
        bytes[23] = (byte) (dataLength & 0xFF);
        for (int i = 0; i < dataLength; i++) {
            bytes[24 + i] = (byte) (0xFF & data.get(i));
        }
        bytes[24 + dataLength] = verify;
        return bytes;
    }

}
