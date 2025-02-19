package com.camellya.gbt32960_3_tcp_server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = TcpServerProperties.PREFIX)
@Data
public class TcpServerProperties {

    public static final String PREFIX = "netty.server";

    private String host = "0.0.0.0";

    private Integer vehiclePort = 20000;

    private Integer platformPort = 20001;

    private Integer vehicleCacheSize = 1024 * 5;

    private Integer platformCacheSize = 1024 * 1024 * 50;

    /**
     * 使用epoll传输模式linux上开启会有更高的性能
     */
    private Boolean useEpoll = false;

    private Integer heartSeconds = 120;
}
