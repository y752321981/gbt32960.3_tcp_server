package com.camellya.gbt32960_3_tcp_server.constant.enums;

import com.camellya.gbt32960_3_tcp_server.protocol.infomodel.*;
import lombok.Getter;

@Getter
public enum ReportInfoEnum {
    VEHICLE(1, "整车数据", VehicleModel.class),
    DRIVER_MOTOR(2, "驱动电机数据", DriverEngineModel.class),
    FUEL_CELL(3, "燃料电池数据", FuelCellModel.class),
    ENGINE(4, "发动机数据", EngineModel.class),
    LOCATION(5, "车辆位置数据", LocateModel.class),
    EXTREMUM(6, "极值数据", ExtremumModel.class),
    ALARM(7, "报警数据", AlarmModel.class),
    UNKNOWN(99, "未知数据", BaseInfoModel.class),
    ;
    private final Byte code;

    private final String desc;

    private final Class<?> clazz;

    ReportInfoEnum(int code, String desc, Class<?> clazz) {
        this.code = (byte) code;
        this.desc = desc;
        this.clazz = clazz;
    }

    public static ReportInfoEnum getInfoReportDataEnum(Byte code) {
        for (ReportInfoEnum reportInfoEnum : ReportInfoEnum.values()) {
            if (reportInfoEnum.getCode().equals(code)) {
                return reportInfoEnum;
            }
        }
        return UNKNOWN;
    }

}
