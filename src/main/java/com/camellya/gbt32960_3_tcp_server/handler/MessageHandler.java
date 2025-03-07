package com.camellya.gbt32960_3_tcp_server.handler;


import cn.hutool.core.util.ReflectUtil;
import com.camellya.gbt32960_3_tcp_server.annotation.Command;
import com.camellya.gbt32960_3_tcp_server.constant.consist.ProtocolConstants;
import com.camellya.gbt32960_3_tcp_server.constant.consist.RedisConstants;
import com.camellya.gbt32960_3_tcp_server.constant.enums.CommandEnum;
import com.camellya.gbt32960_3_tcp_server.constant.enums.DataDecodeEnum;
import com.camellya.gbt32960_3_tcp_server.model.entity.OriginRecord;
import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import com.camellya.gbt32960_3_tcp_server.service.IChannelService;
import com.camellya.gbt32960_3_tcp_server.service.IOriginRecordService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Executor;

@Slf4j
@Component
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<GBT32960Packet> {

    @Resource
    private IChannelService channelService;

    private HashMap<CommandEnum, Method> commands;

    private HashMap<CommandEnum, Method> answers;

    @Resource
    private CommandHandler commandHandler;

    @Resource
    private AuthHandler authHandler;

    @Resource(name = "taskExecutor")
    private Executor executor;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private IOriginRecordService originRecordService;

    @PostConstruct
    public void initFunction() {
        commands = new LinkedHashMap<>();
        Method[] methods = ReflectUtil.getMethods(commandHandler.getClass());
        for (Method method : methods) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation != null) {
                commands.put(annotation.value(), method);
            }
        }

        answers = new LinkedHashMap<>();
        methods = ReflectUtil.getMethods(authHandler.getClass());
        for (Method method : methods) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation != null) {
                answers.put(annotation.value(), method);
            }
        }
    }

    private void processCommand(CommandEnum commandEnum, ChannelHandlerContext ctx, GBT32960Packet packet) {
        Method method = commands.get(commandEnum);
        if (method == null) {
            log.error("未实现处理方法: {}", commandEnum.getDesc());
            return;
        }
        try {
            method.invoke(commandHandler, ctx, packet);
        } catch (Exception e) {
            log.error("调用指令处理方法失败: {}, {}", commandEnum.getDesc(), e.getMessage());
        }
    }

    private void processAnswer(CommandEnum commandEnum, ChannelHandlerContext ctx, GBT32960Packet packet) {
        Method method = answers.get(commandEnum);
        if (method == null) {
            log.error("未实现处理方法: {}", commandEnum.getDesc());
            return;
        }
        try {
            method.invoke(answers, ctx, packet);
        } catch (Exception e) {
            log.error("调用指令处理方法失败: {}, {}", commandEnum.getDesc(), e.getMessage());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {
        log.debug("channelId:{}, 收到消息:{}", channelHandlerContext.channel().id(), packet);
        Byte[] protocolBytes = packet.toProtocolBytes();
        originRecordService.save(OriginRecord.builder()
                .vin(packet.getVin())
                .origin(protocolBytes)
                .time(new Date()).build());

        // 对数据单元进行解码
        DataDecodeEnum decodeType = DataDecodeEnum.getByCode(packet.getEncryptMode());
        if (decodeType == null) {
            log.error("无效的加密方式:{},数据:{}", packet.getEncryptMode(), packet.getData());
            return;
        }
        // TODO: 加解密实现
//        switch (decodeType) {
//            case NONE -> {
//            }
//            case RSA -> {
//
//            }
//            case AES -> {
//
//            }
//            case ERROR -> {
//            }
//            case INVALID -> {
//            }
//        }
        CommandEnum commandEnum = CommandEnum.getCommandEnum(packet.getCommandFlag());
        if (packet.getAckFlag() == ProtocolConstants.ACK_NOT) {
            if (channelService.isVehicle(channelHandlerContext)) {
                log.info("设备上行-vin:{}, 指令:{}, channelId:{}", packet.getVin(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            } else if (channelService.isPlatform(channelHandlerContext)) {
                log.info("平台转发-account:{}, vin:{}, 指令:{}, channelId:{}", channelService.getClientId(channelHandlerContext), packet.getVin(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            } else {
                log.info("未登入-clientId:{}, 指令:{}, channelId:{}", packet.getVin(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            }
            executor.execute(() -> processCommand(commandEnum, channelHandlerContext, packet));
        } else {
            log.info("设备响应-vin:{}, 指令:{}, channelId:{}", packet.getVin(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            executor.execute(() -> processAnswer(commandEnum, channelHandlerContext, packet));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            if (event.state() == IdleState.READER_IDLE) {
                log.info("心跳检测超时，断开连接: channelId: {}, clientId: {}", ctx.channel().id(), channelService.getClientId(ctx));
                redisTemplate.delete(RedisConstants.VEHICLE_INFO + ":" + ctx.channel().id());
                channelService.closeAndClean(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
