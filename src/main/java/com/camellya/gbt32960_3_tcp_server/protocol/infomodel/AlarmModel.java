package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;

import com.camellya.gbt32960_3_tcp_server.utils.ByteUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmModel {

    private static final int FIXED_LENGTH = 9;

    // 最高报警等级 0-3, 0xfe表示异常, 0xff表示无效
    private Byte maxAlarmLevel;

    // 通用报警标志
    private Long alarmFlag;

    // 可充电储能装置故障总数
    private Byte totalFailuresDevices;

    // 可充电储能装置故障代码列表
    private List<Long> failuresDevicesErrorCode;

    // 驱动电机故障总数
    private Byte totalFailuresDriverEngines;

    // 驱动电机故障代码列表
    private List<Long> failuresDriverEnginesErrorCode;

    // 发动机故障总数
    private Byte totalFailuresEngines;

    // 发动机故障列表
    private List<Long> failuresEnginesErrorCode;

    // 其他故障总数
    private Byte totalFailuresOthers;

    // 其他故障代码列表
    private List<Long> failuresOthersErrorCode;

    public int getLength() {
        return FIXED_LENGTH + totalFailuresDevices + totalFailuresDriverEngines + totalFailuresEngines + totalFailuresOthers;
    }

    public AlarmModel(List<Byte> dataList) {
        if (dataList == null) {
            throw new RuntimeException("报警数据解析失败, 传入数据为空");
        }
        int size = dataList.size();
        if (size < FIXED_LENGTH) {
            throw new RuntimeException("报警数据解析失败，传入数据长度不正确:" + dataList);
        }
        this.maxAlarmLevel = dataList.get(0);
        this.alarmFlag = ByteUtil.byteArrayToUnsignedInt(dataList.subList(1, 5));

        this.totalFailuresDevices = dataList.get(5);
        if (size < FIXED_LENGTH + this.totalFailuresDevices) {
            throw new RuntimeException("报警数据解析失败，传入数据长度不正确:" + dataList);
        }
        this.failuresDevicesErrorCode = new ArrayList<>();
        for (int i = 0; i < this.totalFailuresDevices; i++) {
            this.failuresDevicesErrorCode.set(i, ByteUtil.byteArrayToUnsignedInt(dataList.subList(i * 4 + 6, i * 4 + 10)));
        }

        this.totalFailuresDriverEngines = dataList.get(this.totalFailuresDevices * 4 + 6);
        if (size < FIXED_LENGTH + this.totalFailuresDevices + this.totalFailuresDriverEngines) {
            throw new RuntimeException("报警数据解析失败，传入数据长度不正确:" + dataList);
        }
        this.failuresDriverEnginesErrorCode = new ArrayList<>();
        for (int i = 0; i < this.totalFailuresDriverEngines; i++) {
            this.failuresDevicesErrorCode.set(i, ByteUtil.byteArrayToUnsignedInt(dataList.subList(i * 4 + this.totalFailuresDevices * 4 + 7, i * 4 + this.totalFailuresDevices * 4 + 11)));
        }

        this.totalFailuresEngines = dataList.get(this.totalFailuresDevices * 4 + this.totalFailuresDriverEngines * 4 + 7);
        if (size < FIXED_LENGTH + this.totalFailuresDevices + this.totalFailuresDriverEngines + this.totalFailuresEngines) {
            throw new RuntimeException("报警数据解析失败，传入数据长度不正确:" + dataList);
        }
        this.failuresEnginesErrorCode = new ArrayList<>();
        for (int i = 0; i < this.totalFailuresEngines; i++) {
            this.failuresEnginesErrorCode.set(i, ByteUtil.byteArrayToUnsignedInt(dataList.subList(i * 4 + this.totalFailuresDevices * 4 + this.totalFailuresDriverEngines * 4 + 8, i * 4 + this.totalFailuresDevices * 4 + this.totalFailuresDriverEngines * 4 + 12)));
        }

        this.totalFailuresOthers = dataList.get(this.totalFailuresDevices * 4 + this.totalFailuresDriverEngines * 4 + this.totalFailuresEngines * 4 + 8);
        if (size < FIXED_LENGTH + this.totalFailuresDevices + this.totalFailuresDriverEngines + this.totalFailuresEngines + this.totalFailuresOthers) {
            throw new RuntimeException("报警数据解析失败，传入数据长度不正确:" + dataList);
        }
        this.failuresOthersErrorCode = new ArrayList<>();
        for (int i = 0; i < this.totalFailuresOthers; i++) {
            this.failuresOthersErrorCode.set(i, ByteUtil.byteArrayToUnsignedInt(dataList.subList(i * 4 + this.totalFailuresDevices * 4 + this.totalFailuresDriverEngines * 4 + this.totalFailuresEngines * 4 + 9, i * 4 + this.totalFailuresDevices * 4 + this.totalFailuresDriverEngines * 4 + this.totalFailuresEngines * 4 + 13)));
        }

    }
}
