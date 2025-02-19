package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;


import com.yin.tcpserver.pojo.enums.InfoReportDataEnum;

import java.util.List;

public class BaseInfoModel {

    public int getLength() {
        return 0;
    }

    public static BaseInfoModel makeInfoModel(InfoReportDataEnum dataEnum, List<Byte> dataList) {
        return switch (dataEnum) {
            case VEHICLE -> new VehicleModel(dataList);
            case DRIVER_MOTOR -> new DriverEngineModel(dataList);
            case FUEL_CELL -> new FuelCellModel(dataList);
            case ENGINE -> new EngineModel(dataList);
            case LOCATION -> new LocateModel(dataList);
            case EXTREMUM -> new ExtremumModel(dataList);
            case ALARM -> new AlarmModel(dataList);
            case UNKNOWN -> new BaseInfoModel();
        };
    }


}
