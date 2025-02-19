package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;

import com.yin.tcpserver.util.ByteConvertUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 驱动电机数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DriverEngineModel extends BaseInfoModel{

    @Override
    public int getLength() {
        return count * EngineData.LENGTH + 1;
    }

    // 驱动电机个数
    private Byte count;

    // 驱动电机数据列表
    private List<EngineData> dataList;

    @Data
    static class EngineData {

        public static int LENGTH = 12;

        // 序号
        private Byte serial;

        // 状态,1耗电，2发电，3关闭，4准备，0xfe异常，0xff无效
        private Byte status;

        // 控制器温度，（单位摄氏度），偏移40，量程-40-210，0xfe异常，0xff无效
        private Byte controlTemperature;

        // 转速，（单位r/min），偏移量20000，量程 -20000-45531，0xfffe异常，0xffff无效
        private Character rotateSpeed;

        // 转矩，（单位0.1N*m)，偏移量2000，量程-2000-4553.1N*m，0xfffe异常，0xffff无效
        private Character torque;

        // 电机温度，（单位摄氏度），偏移40，量程-40-210，0xfe异常，0xff无效
        private Byte engineTemperature;

        // 电压，（单位0.1V），0xfffe异常，0xffff无效
        private Character voltage;

        // 电流，（单位0.1A），偏移1000，量程-1000-1000A
        private Character current;

    }

    public DriverEngineModel(List<Byte> dataList) {
        this.count = dataList.get(0);
        this.dataList = new ArrayList<>(this.count);
        for (int i = 0; i < this.count; i++) {
            EngineData engineData = new EngineData();
            engineData.serial = dataList.get(i * EngineData.LENGTH);
            engineData.status = dataList.get(i * EngineData.LENGTH + 1);
            engineData.controlTemperature = dataList.get(i * EngineData.LENGTH + 2);
            engineData.rotateSpeed = ByteConvertUtil.byteArrayToChar(dataList.subList(i * EngineData.LENGTH + 3, i * EngineData.LENGTH + 5));
            engineData.torque = ByteConvertUtil.byteArrayToChar(dataList.subList(i * EngineData.LENGTH + 5, i * EngineData.LENGTH + 7));
            engineData.engineTemperature = dataList.get(i * EngineData.LENGTH + 7);
            engineData.voltage = ByteConvertUtil.byteArrayToChar(dataList.subList(i * EngineData.LENGTH + 8, i * EngineData.LENGTH + 10));
            engineData.current = ByteConvertUtil.byteArrayToChar(dataList.subList(i * EngineData.LENGTH + 10, i * EngineData.LENGTH + 12));
            this.dataList.add(engineData);
        }
    }

}
