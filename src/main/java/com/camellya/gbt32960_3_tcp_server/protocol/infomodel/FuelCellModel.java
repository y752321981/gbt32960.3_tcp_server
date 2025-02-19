package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;

import com.yin.tcpserver.util.ByteConvertUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 燃料电池
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FuelCellModel extends BaseInfoModel{

    @Override
    public int getLength() {
        return 17 + probeCount;
    }

    // 电压，（单位0.1V），0xfffe异常，0xffff无效
    private Character voltage;

    // 电流，（单位0.1A），0xfffe异常，0xffff无效
    private Character current;

    // 消耗率，（单位0.01kg/100km），0xfffe异常，0xffff无效
    private Character consumption;

    // 温度探针数量
    private Character probeCount;

    // 探针温度
    private List<Byte> probeTemperatures;

    // 氢系统中最高温度, 0-2400 (单位0.1摄氏度)，偏移量40摄氏度，值域(-40 - 200摄氏度)
    private Character hSystemHighestTemperature;

    // 氢系统中最高温度探针代号, 1-252，0xfe异常，0xff无效
    private Byte hSystemHighestTemperatureIndex;

    // 氢气最高浓度, 0-60000 (单位1mg/kg)，值域(0-50000mg/kg)，0xfffe异常，0xffff无效
    private Character hHighestConcentration;

    // 氢气最高浓度传感器代号, 1-252，0xfe异常，0xff无效
    private Byte hHighestConcentrationIndex;

    // 氢气最高压力, 0-1000（单位0.1MPa)
    private Character hHighestPressure;

    // 氢气最高压力传感器代号， 1-252，0xfe异常，0xff无效
    private Byte hHighestPressureIndex;

    // 高压DC/DC状态 1-工作, 2-断开，0xfe异常，0xff无效
    private Byte dcDcStatus;

    public FuelCellModel(List<Byte> dataList) {
        this.voltage = ByteConvertUtil.byteArrayToChar(dataList.subList(0, 2));
        this.current = ByteConvertUtil.byteArrayToChar(dataList.subList(2, 4));
        this.consumption = ByteConvertUtil.byteArrayToChar(dataList.subList(4, 6));
        this.probeCount = ByteConvertUtil.byteArrayToChar(dataList.subList(6, 8));
        this.probeTemperatures = new ArrayList<>(this.probeCount);
        for (int i = 0; i < probeCount; i++) {
            this.probeTemperatures.set(i, dataList.get(i + 8));
        }
        this.hSystemHighestTemperature = ByteConvertUtil.byteArrayToChar(dataList.subList(this.probeCount + 8, this.probeCount + 10));
        this.hSystemHighestTemperatureIndex = dataList.get(this.probeCount + 10);
        this.hHighestConcentration = ByteConvertUtil.byteArrayToChar(dataList.subList(this.probeCount + 11, this.probeCount + 13));
        this.hHighestConcentrationIndex = dataList.get(this.probeCount + 13);
        this.hHighestPressure = ByteConvertUtil.byteArrayToChar(dataList.subList(this.probeCount + 14, this.probeCount + 16));
        this.hHighestPressureIndex = dataList.get(this.probeCount + 16);
        this.dcDcStatus = dataList.get(this.probeCount + 17);
    }

}
