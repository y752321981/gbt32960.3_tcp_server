package com.camellya.gbt32960_3_tcp_server.service.Impl;

import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import com.camellya.gbt32960_3_tcp_server.service.IChannelService;
import com.camellya.gbt32960_3_tcp_server.service.INodeService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChannelServiceImpl implements IChannelService {

    /**
     * 频道绑定Key, 车辆为vin, 平台为对应的用户名
     */
    private final static AttributeKey<String> CLIENT_ID = AttributeKey.valueOf("clientId");

    /**
     * 是否是车辆通道标记
     */
    private final static AttributeKey<Boolean> IS_VEHICLE = AttributeKey.valueOf("isVehicle");

    /**
     * 是否登入标记
     */
    private final static AttributeKey<Boolean> IS_AUTHORIZED = AttributeKey.valueOf("nodeInfo");

    /**
     * 车辆频道表
     */
    private final ConcurrentHashMap<String, ChannelId> vehicleChannelMap = new ConcurrentHashMap<>();

    /**
     * 平台频道表
     */
    private final ConcurrentHashMap<String, ChannelId> platformChannelMap = new ConcurrentHashMap<>();

    /**
     * 存储频道
     */
    public final static ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Resource
    private INodeService nodeService;

    @Override
    public void recordChannel(ChannelHandlerContext context, boolean isVehicle) {
        Channel channel = CHANNEL_GROUP.find(context.channel().id());
        if (channel != null) {
            CHANNEL_GROUP.remove(channel);
        }
        channel = context.channel();
        context.channel().attr(CLIENT_ID).set(context.channel().id().toString());
        context.channel().attr(IS_VEHICLE).set(isVehicle);
        context.channel().attr(IS_AUTHORIZED).set(false);
        CHANNEL_GROUP.add(channel);
    }

    @Override
    public void authenticate(ChannelHandlerContext context, String clientId) {
        context.channel().attr(CLIENT_ID).set(clientId);
        if (context.channel().attr(IS_VEHICLE).get()) {
            vehicleChannelMap.put(clientId, context.channel().id());
        } else {
            platformChannelMap.put(clientId, context.channel().id());
        }
        context.channel().attr(IS_AUTHORIZED).set(true);
        nodeService.updateAliveCount(vehicleChannelMap.size(), platformChannelMap.size());
    }

    @Override
    public boolean isAuthorized(ChannelHandlerContext context) {
        return context.channel().attr(IS_AUTHORIZED).get();
    }

    @Override
    public boolean isVehicle(ChannelHandlerContext context) {
        return context.channel().attr(IS_VEHICLE).get();
    }

    @Override
    public boolean isPlatform(ChannelHandlerContext context) {
        return !isVehicle(context);
    }

    @Override
    public void sendMessage(ChannelHandlerContext context, GBT32960Packet packet) {
        Channel channel = context.channel();
        channel.writeAndFlush(packet);
        log.info("给{}发送消息:{}, clientId:{}, channelId:{}", channel.attr(IS_VEHICLE).get() ? "车辆" : "平台", packet, channel.attr(CLIENT_ID).get(), channel.id());
    }

    @Override
    public String getClientId(ChannelHandlerContext context) {
        return "";
    }

    @Override
    public void closeAndClean(ChannelHandlerContext context) {
        Channel channel = CHANNEL_GROUP.find(context.channel().id());
        if (channel != null) {
            CHANNEL_GROUP.remove(channel);
        } else {
            return;
        }
        if (isAuthorized(context)) {
            if (isVehicle(context)) {
                vehicleChannelMap.remove(channel.attr(CLIENT_ID).get());
            } else {
                platformChannelMap.remove(channel.attr(CLIENT_ID).get());
            }
            nodeService.updateAliveCount(vehicleChannelMap.size(), platformChannelMap.size());
        }
        channel.attr(CLIENT_ID).set("");
        channel.attr(IS_AUTHORIZED).set(false);
        CHANNEL_GROUP.remove(channel);
        channel.close();
    }
}
