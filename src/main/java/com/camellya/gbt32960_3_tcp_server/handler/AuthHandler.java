package com.camellya.gbt32960_3_tcp_server.handler;


import com.camellya.gbt32960_3_tcp_server.enums.AckEnum;
import com.camellya.gbt32960_3_tcp_server.enums.CommandEnum;
import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import com.camellya.gbt32960_3_tcp_server.service.IChannelService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<GBT32960Packet> {

    @Resource
    private IChannelService channelService;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("成功建立连接， channelId:{}", ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GBT32960Packet gbt32960) {
        boolean auth = channelService.isAuthorized(channelHandlerContext);
        CommandEnum command = CommandEnum.getCommandEnum(gbt32960.getCommandFlag());
        if (auth) {
            // 已登入判断是平台还是车辆
            if (channelService.isVehicle(channelHandlerContext)) {
                // 车辆放行 除登录外的所有指令
                if (command != CommandEnum.VEHICLE_LOGIN) {
                    channelHandlerContext.fireChannelRead(gbt32960);
                } else {
                    log.info("channelId:{}, vin:{},车辆重复登录", channelHandlerContext.channel().id(), gbt32960.getVIN());
                    channelService.sendMessage(channelHandlerContext, gbt32960.makeResponse(AckEnum.SUCCESS));
                }
            } else if (channelService.isPlatform(channelHandlerContext)) {
                // 平台不放行 车辆登入登出指令
                if (command == CommandEnum.VEHICLE_LOGIN || command == CommandEnum.VEHICLE_LOGOUT) {
                    log.info("忽略平台转发:{}, channelId:{}", command.getDesc(), channelHandlerContext.channel().id());
                } else if (command == CommandEnum.PLATFORM_LOGIN) {
                    log.info("channelId:{}, account:{},平台重复登录", channelHandlerContext.channel().id(), channelService.getClientId(channelHandlerContext));
                    channelService.sendMessage(channelHandlerContext, gbt32960.makeResponse(AckEnum.SUCCESS));
                } else {
                    channelHandlerContext.fireChannelRead(gbt32960);
                }
            } else {
                log.error("当前频道:{}, 状态错误:{}", channelHandlerContext.channel().id(), channelService.getLoginStatus(channelHandlerContext));
                channelService.closeAndClean(channelHandlerContext);
            }
        } else {
            // 未登入只放行登入消息
            if (!Objects.equals(gbt32960.getCommandFlag(), CommandEnum.VEHICLE_LOGIN.getCode()) && !Objects.equals(gbt32960.getCommandFlag(), CommandEnum.PLATFORM_LOGIN.getCode())) {
                log.info("当前频道{}未登录, 发送非登陆指令:{},断开此连接", channelHandlerContext.channel().id(), CommandEnum.getCommandEnum(gbt32960.getCommandFlag()).getDesc());
                channelHandlerContext.close();
            } else {
                channelHandlerContext.fireChannelRead(gbt32960);
            }
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接断开， channelId:{}", ctx.channel().id());
        if (channelService.getLoginStatus(ctx) == ChannelLoginStatus.VEHICLE_LOGIN) {
            log.info("设置{}离线", channelService.getClientId(ctx));
            vehicleInfoService.logout(channelService.getClientId(ctx));
        }
        if (channelService.isAuth(ctx)) {
            channelService.closeAndClean(ctx);
        }
        super.channelInactive(ctx);
    }
}
