package com.camellya.gbt32960_3_tcp_server.config;

import com.yin.tcpserver.handler.AuthHandler;
import com.yin.tcpserver.handler.ExceptionHandler;
import com.yin.tcpserver.handler.MessageHandler;
import com.yin.tcpserver.util.GBT32960Decoder;
import com.yin.tcpserver.util.GBT32960Encoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class NettyChannelInit extends ChannelInitializer<SocketChannel> {

    @Resource
    private AuthHandler authHandler;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private ExceptionHandler exceptionHandler;

    @Resource
    private TcpServerProperties properties;

    @Override
    protected void initChannel(SocketChannel socketChannel) {

        socketChannel.pipeline()
                .addLast(new IdleStateHandler(properties.getHeartSeconds(), 0, 0, TimeUnit.SECONDS))
                .addLast(new GBT32960Decoder())
                .addLast(new GBT32960Encoder())
                .addLast(authHandler)
                .addLast(messageHandler)
                .addLast(exceptionHandler);
    }
}
