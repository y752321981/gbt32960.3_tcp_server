package com.camellya.gbt32960_3_tcp_server.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VehicleInfo {

    private String vin;

    private Date loginTime;

    private String nodeName;

    private String channelId;

    private Boolean isVehicle;

    private String ip;

    private Date lastHeartbeatTime;
}
