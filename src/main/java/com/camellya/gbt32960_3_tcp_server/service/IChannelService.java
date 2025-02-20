package com.camellya.gbt32960_3_tcp_server.service;

import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import io.netty.channel.ChannelHandlerContext;

public interface IChannelService {

    /**
     * 记录频道
     * @param context context
     * @param isVehicle true-车辆通道, false-平台通道
     */
    void recordChannel(ChannelHandlerContext context, boolean isVehicle);

    /**
     * 为频道登入授权
     * @param context context
     * @param clientId clientId
     */
    void authenticate(ChannelHandlerContext context, String clientId);

    /**
     * 此频道是否已登入
     * @param context ChannelHandlerContext
     * @return 结果
     */
    boolean isAuthorized(ChannelHandlerContext context);

    /**
     * 此频道是否车辆通道
     * @param context ChannelHandlerContext
     * @return 结果
     */
    boolean isVehicle(ChannelHandlerContext context);

    /**
     * 此频道是否平台通道
     * @param context ChannelHandlerContext
     * @return 结果
     */
    boolean isPlatform(ChannelHandlerContext context);

    /**
     * 发送消息
     * @param context ChannelHandlerContext
     * @param packet GBT32960Packet
     */
    void sendMessage(ChannelHandlerContext context, GBT32960Packet packet);

    /**
     * 获取
     * @param context
     * @return
     */
    String getClientId(ChannelHandlerContext context);

    void closeAndClean(ChannelHandlerContext context);

}
