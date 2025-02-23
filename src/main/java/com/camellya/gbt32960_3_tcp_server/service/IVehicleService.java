package com.camellya.gbt32960_3_tcp_server.service;

import com.camellya.gbt32960_3_tcp_server.model.entity.VehicleInfo;
import io.netty.channel.ChannelHandlerContext;

public interface IVehicleService {

    /**
     * 登录
     * @param vin Vin
     * @param iccid Iccid
     * @return 结果
     */
    boolean login(String vin, String iccid);

    void saveOrUpdateCache(ChannelHandlerContext context, VehicleInfo info);

    void refreshCacheExpire(ChannelHandlerContext context);

    void deleteCache(ChannelHandlerContext context);
}
