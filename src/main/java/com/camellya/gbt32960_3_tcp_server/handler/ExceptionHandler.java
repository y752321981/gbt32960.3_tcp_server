package com.camellya.gbt32960_3_tcp_server.handler;

import com.yin.tcpserver.service.IAuthService;
import com.yin.tcpserver.service.IChannelService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private IChannelService channelService;

    @Resource
    private IAuthService vehicleInfoService;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("发生了 IOException: {}", cause.getMessage());
        } else if (cause instanceof DecoderException) {
            log.error("发生了 DecoderException: 数据格式无效。", cause);
        } else if (cause instanceof ReadTimeoutException) {
            log.error("发生了 ReadTimeoutException: 客户端在规定时间内没有发送数据。");
        } else if (cause instanceof WriteTimeoutException) {
            log.error("发生了 WriteTimeoutException: 无法将数据写入客户端。");
        } else if (cause instanceof OutOfMemoryError) {
            log.error("发生了 OutOfMemoryError: 内存不足。");
        } else if (cause.getMessage().contains("Connection reset")) {
            log.error("发生了 ConnectionResetException: 连接被对端重置。");
        } else if (cause instanceof IllegalArgumentException) {
            log.error("发生了 IllegalArgumentException: {}", cause.getMessage());
        } else {
            log.error("发生了未知异常: ", cause);
        }
        if (channelService.isVehicle(ctx)) {
            vehicleInfoService.logout(channelService.getClientId(ctx));
        }
        channelService.closeAndClean(ctx);
    }
}
