package com.camellya.gbt32960_3_tcp_server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = NodeConfig.PREFIX)
@Data
public class NodeConfig {

    public static final String PREFIX = "node";

    private String name = "default_node";

    private String host = "127.0.0.1:16321";

}
