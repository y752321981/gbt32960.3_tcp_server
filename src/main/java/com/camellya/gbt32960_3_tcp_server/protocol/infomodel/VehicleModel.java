package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;

import com.camellya.gbt32960_3_tcp_server.utils.ByteUtil;
import lombok.Data;

import java.util.List;

/**
 * 整车数据
 */
@Data
public class VehicleModel {

    private static final int FIXED_LENGTH = 20;

    public int getLength() {
        return FIXED_LENGTH;
    }

    // 车辆状态，1启动，2熄火，3其他，0xfe异常，0xff无效
    private Byte vehicleStatus;

    // 充电状态，1停车充电，2行驶充电，3未充电，4充电完成，0xfe异常，0xff无效
    private Byte chargeStatus;

    // 运行模式，1纯电，2混动，3燃油，0xfe异常，0xff无效
    private Byte runModel;

    // 车速，0-2200(单位百米/小时)，0xfffe表示异常，0xffff表示无效
    private Character speed;

    // 累计里程，（单位百米），0xfffffffe表示异常，0xffffffff表示无效
    private Long totalMileage;

    // 总电压，（单位0.1V），0xfffe表示异常，0xffff表示无效
    private Character voltage;

    // 总电流，（单位0.1A，偏移量1000A，量程-1000~1000A），0xfffe表示异常，0xffff表示无效
    private Character current;

    // 电量，（%），0xfe异常，0xff无效
    private Byte soc;

    // DC-DC状态，1工作，2断开，0xfe异常，0xff无效
    private Byte dcDc;

    /**
     * {@code @bit7} 预留
     * {@code @bit6} 预留
     * {@code @bit5} 1-有驱动力 0-无驱动力
     * {@code @bit4} 1-有制动力 0-无制动力
     * {@code @bit3-bit0} 0000-空挡, 0001-1挡, 0010-2挡, 0011-3挡, 0100-4挡, 0101-5挡, 0110-6挡, ..., 1101-倒挡, 1110-自动D挡, 1111-停车P挡
     */
    private Byte gear;

    // 绝缘电阻，0-60000，（单位千欧）
    private Character insulationResistance;

    // 预留
    private Character reserve;

    public VehicleModel(List<Byte> dataList) {
        this.vehicleStatus = dataList.get(0);
        this.chargeStatus = dataList.get(1);
        this.runModel = dataList.get(2);
        this.speed = ByteUtil.byteArrayToChar(dataList.subList(3, 5));
        this.totalMileage = ByteUtil.byteArrayToUnsignedInt(dataList.subList(5, 9));
        this.voltage = ByteUtil.byteArrayToChar(dataList.subList(9, 11));
        this.current = ByteUtil.byteArrayToChar(dataList.subList(11, 13));
        this.soc = dataList.get(13);
        this.dcDc = dataList.get(14);
        this.gear = dataList.get(15);
        this.insulationResistance = ByteUtil.byteArrayToChar(dataList.subList(16, 18));
        this.reserve = ByteUtil.byteArrayToChar(dataList.subList(18, 20));
    }

}
