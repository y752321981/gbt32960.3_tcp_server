package com.camellya.gbt32960_3_tcp_server.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PlatformInfo {

    private String username;

    private String nodeName;

    private String channelId;

    private String ip;

    private Date lastHeartbeatTime;

    private Date loginTime;

}
