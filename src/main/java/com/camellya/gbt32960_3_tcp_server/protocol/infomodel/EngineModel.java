package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;

import com.yin.tcpserver.util.ByteConvertUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 发动机数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EngineModel extends BaseInfoModel {

    // 状态，1启动，2关闭，0xfe异常，0xff无效
    private Byte status;

    // 曲轴转速，（单位r/min），0xfffe表示异常，0xffff表示无效
    private Character rotateSpeed;

    // 燃料消耗率，（单位0.01L/100km），0xfffe表示异常，0xffff表示无效
    private Character consumption;

    public EngineModel(List<Byte> byteList) {
        this.status = byteList.get(0);
        this.rotateSpeed = ByteConvertUtil.byteArrayToChar(byteList.subList(1, 3));
        this.consumption = ByteConvertUtil.byteArrayToChar(byteList.subList(3, 5));
    }


}
