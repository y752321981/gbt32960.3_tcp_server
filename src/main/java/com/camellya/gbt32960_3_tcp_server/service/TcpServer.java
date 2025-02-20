package com.camellya.gbt32960_3_tcp_server.service;

import com.camellya.gbt32960_3_tcp_server.config.TcpServerProperties;
import com.camellya.gbt32960_3_tcp_server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TcpServer extends ChannelInitializer<SocketChannel>  {

    @Resource
    private TcpServerProperties serverProperties;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @PostConstruct
    public void start() {
        log.info("正在初始化tcp服务器,{}" , serverProperties.toString());
        bossGroup = serverProperties.getUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = serverProperties.getUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(serverProperties.getUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    // 配置 编码器、解码器、业务处理
                    .childHandler(this)
                    // tcp缓冲区
                    .option(ChannelOption.SO_BACKLOG, 2048)
                    // 将网络数据积累到一定的数量后,服务器端才发送出去,会造成一定的延迟。希望服务是低延迟的,建议将TCP_NODELAY设置为true
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.localAddress(new InetSocketAddress(serverProperties.getHost(), serverProperties.getVehiclePort()))
                    // 设置 TCP 接收缓冲区大小（单位：字节）
                    .childOption(ChannelOption.SO_RCVBUF, serverProperties.getVehicleCacheSize())
                    // 设置 TCP 发送缓冲区大小（单位：字节）
                    .childOption(ChannelOption.SO_SNDBUF, serverProperties.getVehicleCacheSize())
                    .bind().sync();
            log.info("tcpServer车辆服务启动成功！开始监听端口：{}, 缓存区大小: {}", serverProperties.getVehiclePort(), serverProperties.getVehicleCacheSize());
            serverBootstrap.localAddress(new InetSocketAddress(serverProperties.getHost(), serverProperties.getPlatformPort()))
                    // 设置 TCP 接收缓冲区大小（单位：字节）
                    .childOption(ChannelOption.SO_RCVBUF, serverProperties.getPlatformCacheSize()) // 设置为 50 MB
                    // 设置 TCP 发送缓冲区大小（单位：字节）
                    .childOption(ChannelOption.SO_SNDBUF, serverProperties.getPlatformCacheSize()) // 设置为 50 MB;
                    .bind().sync();
            log.info("tcpServer车辆服务启动成功！开始监听端口：{}, 缓存区大小: {}", serverProperties.getPlatformPort(), serverProperties.getPlatformCacheSize());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


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
                .addLast(new GBT32960DecoderHandler())
                .addLast(new GBT32960EncoderHandler())
                .addLast(authHandler)
                .addLast(messageHandler)
                .addLast(exceptionHandler);
    }
}
