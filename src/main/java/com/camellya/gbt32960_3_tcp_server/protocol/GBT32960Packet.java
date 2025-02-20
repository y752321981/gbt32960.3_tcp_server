package com.camellya.gbt32960_3_tcp_server.protocol;


import com.camellya.gbt32960_3_tcp_server.constant.enums.AckEnum;
import com.camellya.gbt32960_3_tcp_server.constant.enums.DataDecodeEnum;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

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
    private String VIN;

    /**
     * 加密方式
     */
    private Byte encryMode;

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
                ",VIN=" + VIN + ",encryMode=" + Integer.toHexString(encryMode)+ ",dataLength=" + Integer.valueOf(dataLength) +
                ",data=" + data.stream().map(Integer::toHexString).toList() + ",verify=" + Integer.toHexString(verify);
    }

    public GBT32960Packet makeResponse(AckEnum ackFlag) {
        this.ackFlag = ackFlag.getCode();
        List<Byte> now = TimeModel.getNow();
        this.data = now;
        this.dataLength = (char) now.size();
        this.encryMode = DataDecodeEnum.NONE.getCode();
        this.verify = this.calcVerifyCode();
        return this;
    }

    public Byte calcVerifyCode() {
        Byte verifyCode = this.commandFlag;
        verifyCode = (byte) (verifyCode ^ this.ackFlag);
        byte[] bytes = VIN.getBytes(StandardCharsets.UTF_8);
        for (byte aByte : bytes) {
            verifyCode = (byte) (verifyCode ^ aByte);
        }
        verifyCode = (byte) (verifyCode ^ this.encryMode);
        verifyCode = (byte) (verifyCode ^ ((this.dataLength >> 8) & 0xFF));
        verifyCode = (byte) (verifyCode ^ (this.dataLength & 0xFF));
        for (byte aByte : this.data) {
            verifyCode = (byte) (verifyCode ^ aByte);
        }
        return verifyCode;
    }

    public boolean isAckSuccess() {
        return Objects.equals(this.ackFlag, AckEnum.SUCCESS.getCode());
    }

}
