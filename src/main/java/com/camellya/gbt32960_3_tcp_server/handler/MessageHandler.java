package com.camellya.gbt32960_3_tcp_server.handler;

import com.yin.tcpserver.pojo.constant.ProtocolConstants;
import com.yin.tcpserver.pojo.enums.CommandEnum;
import com.yin.tcpserver.pojo.enums.DataDecodeEnum;
import com.yin.tcpserver.pojo.protocol.*;
import com.yin.tcpserver.service.IChannelService;
import com.yin.tcpserver.util.AesUtil;
import com.yin.tcpserver.util.AnswerHandlerUtil;
import com.yin.tcpserver.util.CommandHandlerUtil;
import com.yin.tcpserver.util.RsaUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<GBT32960Packet> {

    @Resource
    private IChannelService channelService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {
        log.debug("channelId:{}, 收到消息:{}", channelHandlerContext.channel().id(), packet);
        // 对数据单元进行解码
        DataDecodeEnum decodeType = DataDecodeEnum.getByCode(packet.getEncryMode());
        if (decodeType == null) {
            log.error("无效的加密方式:{},数据:{}", packet.getEncryMode(), packet.getData());
            return;
        }
        switch (decodeType) {
            case NONE -> {
            }
            case RSA -> packet.setData(RsaUtil.decrypt(packet.getData()));
            case AES -> packet.setData(AesUtil.decrypt(packet.getData()));
            case ERROR -> {
            }
            case INVALID -> {
            }
        }
        CommandEnum commandEnum = CommandEnum.getCommandEnum(packet.getCommandFlag());
        if (packet.getAckFlag() == ProtocolConstants.ACK_NOT) {
            if (channelService.isVehicle(channelHandlerContext)) {
                log.info("设备上行-vin:{}, 指令:{}, channelId:{}", packet.getVIN(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            } else if (channelService.isPlatform(channelHandlerContext)) {
                log.info("平台转发-account:{}, vin:{}, 指令:{}, channelId:{}", channelService.getClientId(channelHandlerContext), packet.getVIN(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            } else {
                log.info("未登入-clientId:{}, 指令:{}, channelId:{}", packet.getVIN(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            }
            CommandHandlerUtil.process(commandEnum, channelHandlerContext, packet);
        } else {
            log.info("设备响应-vin:{}, 指令:{}, channelId:{}", packet.getVIN(), commandEnum.getDesc(), channelHandlerContext.channel().id());
            AnswerHandlerUtil.process(commandEnum, channelHandlerContext, packet);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            if (event.state() == IdleState.READER_IDLE) {
                log.info("心跳检测超时，断开连接: channelId: {}, clientId: {}", ctx.channel().id(), channelService.getClientId(ctx));
                channelService.closeAndClean(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }




}
