package com.camellya.gbt32960_3_tcp_server.service;

public interface IVehicleService {

    /**
     * 登录
     * @param vin Vin
     * @param iccid Iccid
     * @return 结果
     */
    boolean login(String vin, String iccid);

}
