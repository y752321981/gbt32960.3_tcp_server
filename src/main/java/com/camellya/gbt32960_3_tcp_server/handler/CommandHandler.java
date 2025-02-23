package com.camellya.gbt32960_3_tcp_server.handler;

import com.camellya.gbt32960_3_tcp_server.annotation.Command;
import com.camellya.gbt32960_3_tcp_server.constant.enums.AckEnum;
import com.camellya.gbt32960_3_tcp_server.model.entity.PlatformInfo;
import com.camellya.gbt32960_3_tcp_server.model.entity.VehicleInfo;
import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import com.camellya.gbt32960_3_tcp_server.protocol.InfoModel;
import com.camellya.gbt32960_3_tcp_server.protocol.PlatformLoginModel;
import com.camellya.gbt32960_3_tcp_server.protocol.VehicleLoginModel;
import com.camellya.gbt32960_3_tcp_server.service.IChannelService;
import com.camellya.gbt32960_3_tcp_server.service.INodeService;
import com.camellya.gbt32960_3_tcp_server.service.IPlatformService;
import com.camellya.gbt32960_3_tcp_server.service.IVehicleService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Date;

import static com.camellya.gbt32960_3_tcp_server.constant.enums.CommandEnum.*;

@Slf4j
@Component
public class CommandHandler {

    @Resource
    private IChannelService channelService;

    @Resource
    private IVehicleService vehicleService;

    @Resource
    private IPlatformService platformService;

    @Resource
    private INodeService nodeService;

    @Command(VEHICLE_LOGIN)
    public void vehicleLogin(ChannelHandlerContext context, GBT32960Packet packet) {
        VehicleLoginModel loginModel = new VehicleLoginModel(packet.getData());
        String vin = packet.getVin();
        String iccid = loginModel.getIccid();
        boolean success = vehicleService.login(vin, iccid);
        if (success) {
            VehicleInfo vehicleInfo = new VehicleInfo();
            vehicleInfo.setVin(vin);
            Channel channel = context.channel();
            String channelId = IChannelService.getChannelIdString(context);
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
            vehicleInfo.setIp(inetSocketAddress.toString());
            vehicleInfo.setChannelId(channelId);
            vehicleInfo.setNodeName(nodeService.getNodeName());
            vehicleInfo.setIsVehicle(channelService.isVehicle(context));
            vehicleInfo.setLastHeartbeatTime(new Date());
            vehicleInfo.setLoginTime(new Date());
            vehicleService.saveOrUpdateCache(context, vehicleInfo);
            channelService.sendMessage(context, packet.makeResponse(AckEnum.SUCCESS));
            channelService.authenticate(context, vin);
        } else {
            channelService.sendMessage(context, packet.makeResponse(AckEnum.FAIL));
            channelService.closeAndClean(context);
        }
    }

    @Command(VEHICLE_LOGOUT)
    public void vehicleLogout(ChannelHandlerContext context, GBT32960Packet packet) {
        vehicleService.deleteCache(context);
        channelService.sendMessage(context, packet.makeResponse(AckEnum.SUCCESS));
        channelService.closeAndClean(context);
    }

    @Command(PLATFORM_LOGIN)
    public void platformLogin(ChannelHandlerContext context, GBT32960Packet packet) {
        if (!channelService.isPlatform(context)) {
            log.warn("车辆端口禁止平台登入");
            channelService.sendMessage(context, packet.makeResponse(AckEnum.FAIL));
            channelService.closeAndClean(context);
            return;
        }
        PlatformLoginModel loginModel = new PlatformLoginModel(packet.getData());
        boolean success = platformService.login(loginModel.getUsername(), loginModel.getPassword());
        if (success) {
            PlatformInfo platformInfo = new PlatformInfo();
            platformInfo.setUsername(loginModel.getUsername());
            Channel channel = context.channel();
            String channelId = channel.id().asShortText();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
            platformInfo.setChannelId(channelId);
            platformInfo.setIp(inetSocketAddress.toString());
            platformInfo.setLastHeartbeatTime(new Date());
            platformInfo.setLoginTime(new Date());
            platformInfo.setNodeName(nodeService.getNodeName());
            platformService.saveOrUpdateCache(context, platformInfo);
            channelService.sendMessage(context, packet.makeResponse(AckEnum.SUCCESS));
            channelService.authenticate(context, platformInfo.getUsername());
        } else {
            channelService.sendMessage(context, packet.makeResponse(AckEnum.FAIL));
            channelService.closeAndClean(context);
        }
    }

    @Command(PLATFORM_LOGOUT)
    public void platformLogout(ChannelHandlerContext context, GBT32960Packet packet) {
        platformService.deleteCache(context);
        channelService.sendMessage(context, packet.makeResponse(AckEnum.SUCCESS));
        channelService.closeAndClean(context);
    }

    @Command(REPORT_INFO_REAL)
    public void reportRealtime(ChannelHandlerContext context, GBT32960Packet packet) {
        InfoModel infoModel = new InfoModel(packet.getData());
        String vin = packet.getVin();
        log.warn("上报消息, vin: {}, 是否车辆端口: {}, 数据: {}", vin, channelService.isVehicle(context), infoModel);
        channelService.sendMessage(context, packet.makeResponse(AckEnum.SUCCESS));
    }

    @Command(REPORT_INFO_RESEND)
    public void reportResend(ChannelHandlerContext context, GBT32960Packet packet) {
        InfoModel infoModel = new InfoModel(packet.getData());
        String vin = packet.getVin();
        log.warn("重发消息, vin: {}, channelId: {}, 是否车辆端口: {}, 数据: {}", vin, context.channel().id(), channelService.isVehicle(context), infoModel);
    }

    @Command(HEARTBEAT)
    public void heartbeat(ChannelHandlerContext context, GBT32960Packet packet) {
        if (channelService.isVehicle(context)) {
            vehicleService.refreshCacheExpire(context);
        } else {
            platformService.refreshCacheExpire(context);
        }
        channelService.sendMessage(context, packet.makeResponse(AckEnum.SUCCESS));
    }

    @Command(TERMINAL_TIMING)
    public void terminalTiming(ChannelHandlerContext context, GBT32960Packet packet) {
        channelService.sendMessage(context, packet.makeResponse(AckEnum.SUCCESS));
    }

    @Command(UNKNOWN)
    public void unknownCommand(ChannelHandlerContext context, GBT32960Packet packet) {
        log.warn("未知指令, vin: {}", packet.getVin());
    }

}
