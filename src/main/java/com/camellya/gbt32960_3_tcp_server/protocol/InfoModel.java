package com.camellya.gbt32960_3_tcp_server.protocol;

import com.camellya.gbt32960_3_tcp_server.constant.enums.ReportInfoEnum;
import com.camellya.gbt32960_3_tcp_server.protocol.infomodel.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
@Getter
public class InfoModel {

    private final TimeModel time;

    private VehicleModel vehicle;

    private DriverEngineModel driverEngine;

    private FuelCellModel fuelCell;

    private EngineModel engine;

    private LocateModel locate;

    private ExtremumModel extremum;

    private AlarmModel alarm;

    public InfoModel(List<Byte> byteList) {
        time = new TimeModel(byteList);
        List<Byte> bytes = byteList.subList(6, byteList.size());
        while (!bytes.isEmpty()) {
            ReportInfoEnum reportInfoEnum = ReportInfoEnum.getInfoReportDataEnum(byteList.get(0));
            List<Byte> dataList = byteList.subList(1, byteList.size());
            int length = 0;
            switch (reportInfoEnum) {
                case VEHICLE -> {
                    vehicle = new VehicleModel(dataList);
                    length = vehicle.getLength();
                }
                case DRIVER_MOTOR -> {
                    driverEngine = new DriverEngineModel(dataList);
                    length = driverEngine.getLength();
                }
                case FUEL_CELL -> {
                    fuelCell = new FuelCellModel(dataList);
                    length = fuelCell.getLength();
                }
                case ENGINE -> {
                    engine = new EngineModel(dataList);
                    length = engine.getLength();
                }
                case LOCATION -> {
                    locate = new LocateModel(dataList);
                    length = locate.getLength();
                }
                case EXTREMUM -> {
                    extremum = new ExtremumModel(dataList);
                    length = extremum.getLength();
                }
                case ALARM -> {
                    alarm = new AlarmModel(dataList);
                    length = alarm.getLength();
                }
                case UNKNOWN -> {
                    log.warn("未知的上报数据:{}", byteList);
                    return;
                }
            }
            bytes = byteList.subList(1 + length, byteList.size());
        }
    }

    public Date getTime() {
        return time.getDate();
    }

}
