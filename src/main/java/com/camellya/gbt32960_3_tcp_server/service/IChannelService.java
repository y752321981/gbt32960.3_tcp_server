package com.camellya.gbt32960_3_tcp_server.service;

import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import io.netty.channel.ChannelHandlerContext;

public interface IChannelService {

    /**
     * 此频道是否已登入
     * @param context ChannelHandlerContext
     * @return 结果
     */
    boolean isAuthorized(ChannelHandlerContext context);

    /**
     * 此频道是否车辆登入
     * @param context ChannelHandlerContext
     * @return 结果
     */
    boolean isVehicle(ChannelHandlerContext context);

    /**
     * 此频道是否平台登入
     * @param context ChannelHandlerContext
     * @return 结果
     */
    boolean isPlatform(ChannelHandlerContext context);

    /**
     *
     * @param context ChannelHandlerContext
     * @param packet GBT32960Packet
     */
    void sendMessage(ChannelHandlerContext context, GBT32960Packet packet);

    String getClientId(ChannelHandlerContext context);

    void closeAndClean(ChannelHandlerContext context);
}
