package com.camellya.gbt32960_3_tcp_server.handler;


import com.camellya.gbt32960_3_tcp_server.config.TcpServerProperties;
import com.camellya.gbt32960_3_tcp_server.constant.enums.CommandEnum;
import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import com.camellya.gbt32960_3_tcp_server.service.IChannelService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<GBT32960Packet> {

    @Resource
    private IChannelService channelService;

    @Resource
    private TcpServerProperties tcpServerProperties;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
        int localAddressPort = localAddress.getPort();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        if (localAddressPort == tcpServerProperties.getVehiclePort()) {
            log.info("车辆通信端口: {}, 与channelId: {}, 建立连接, 远程地址: {}", localAddressPort, ctx.channel().id(), socketAddress.toString());
            channelService.recordChannel(ctx, true);
        } else if (localAddressPort == tcpServerProperties.getPlatformPort()) {
            log.info("平台通信端口: {}, 与channelId: {}, 建立连接, 远程地址: {}", localAddressPort, ctx.channel().id(), socketAddress.toString());
            channelService.recordChannel(ctx, false);
        } else {
            log.warn("未知端口: {}, 与channelId: {}, 建立连接, 远程地址: {}", localAddressPort, ctx.channel().id(), socketAddress.toString());
            ctx.close();
            return;
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GBT32960Packet gbt32960) {
        boolean auth = channelService.isAuthorized(channelHandlerContext);
        if (auth) {
            // 已登入则直接放行
            channelHandlerContext.fireChannelRead(gbt32960);
        } else {
            CommandEnum command = CommandEnum.getCommandEnum(gbt32960.getCommandFlag());
            // 未登入只放行登入消息
            if (channelService.isVehicle(channelHandlerContext)) {
                // 车辆通道放行车辆登入
                if (command == CommandEnum.VEHICLE_LOGIN) {
                    channelHandlerContext.fireChannelRead(gbt32960);
                } else {
                    log.warn("channelId: {}, 车辆端口未登入频道发送非登入指令", channelHandlerContext.channel().id());
                    // 此处可以看实际情况选择是否需要断开连接
                    // channelService.closeAndClean(channelHandlerContext);
                }
            } else if (channelService.isPlatform(channelHandlerContext)) {
                // 平台通道只放行平台登入
                if (command == CommandEnum.PLATFORM_LOGIN) {
                    channelHandlerContext.fireChannelRead(gbt32960);
                } else {
                    log.warn("channelId: {}, 平台端口未登入频道发送非登入指令", channelHandlerContext.channel().id());
                    // 此处可以看实际情况选择是否需要断开连接
                    // channelService.closeAndClean(channelHandlerContext);
                }
            } else {
                channelService.closeAndClean(channelHandlerContext);
            }
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接断开， channelId:{}", ctx.channel().id());
        // TODO: 连接断开时的操作
        channelService.closeAndClean(ctx);
        super.channelInactive(ctx);
    }
}
