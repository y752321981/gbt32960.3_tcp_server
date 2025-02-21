package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;

import com.camellya.gbt32960_3_tcp_server.utils.ByteUtil;
import lombok.Data;

import java.util.List;

/**
 * 极值数据
 */
@Data
public class ExtremumModel {

    private static final int Fixed_Length = 14;

    // 最高电压电池子系统号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte highestVoltageBatterySystemIndex;

    // 最高电压电池单体号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte highestVoltageBatteryCellIndex;

    // 电池单体电压最高值 0-15000 单位(0.001V), 0xfffe表示异常, 0xffff表示无效
    private Character batteryCellVoltageMax;

    // 最低电压电池子系统号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte lowestVoltageBatterySystemIndex;

    // 最低电压电池单体号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte lowestVoltageBatteryCellIndex;

    // 电池单体电压最低值 0-15000 单位(0.001V), 0xfffe表示异常, 0xffff表示无效
    private Character batteryCellVoltageMin;

    // 最高温度子系统号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte highestTemperatureSystemIndex;

    // 最高温度探针序号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte highestTemperatureIndex;

    // 最高温度值 0-250 单位(1摄氏度), 偏移40, 值域(-40 - 210摄氏度), 0xfe表示异常, 0xff表示无效
    private Byte temperatureMax;

    // 最低温度子系统号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte lowestTemperatureSystemIndex;

    // 最低温度探针序号 1-250, 0xfe表示异常, 0xff表示无效
    private Byte lowestTemperatureIndex;

    // 最低温度值 0-250 单位(1摄氏度), 偏移40, 值域(-40 - 210摄氏度), 0xfe表示异常, 0xff表示无效
    private Byte temperatureMin;

    public int getLength() {
        return Fixed_Length;
    }

    public ExtremumModel(List<Byte> dataList) {
        if (dataList == null || dataList.size() < this.getLength()) {
            throw new RuntimeException("极值数据解析失败，传入数据长度不正确:" + dataList);
        }
        this.highestVoltageBatterySystemIndex = dataList.get(0);
        this.highestVoltageBatteryCellIndex = dataList.get(1);
        this.batteryCellVoltageMax = ByteUtil.byteArrayToChar(dataList.subList(2, 4));
        this.lowestVoltageBatterySystemIndex = dataList.get(4);
        this.lowestVoltageBatteryCellIndex = dataList.get(5);
        this.batteryCellVoltageMin = ByteUtil.byteArrayToChar(dataList.subList(6, 8));
        this.highestTemperatureSystemIndex = dataList.get(8);
        this.highestTemperatureIndex = dataList.get(9);
        this.temperatureMax = dataList.get(10);
        this.lowestTemperatureSystemIndex = dataList.get(11);
        this.lowestTemperatureIndex = dataList.get(12);
        this.temperatureMin = dataList.get(13);
    }

}
