package com.camellya.gbt32960_3_tcp_server.model.entity;

import lombok.Data;

@Data
public class NodeInfo {

    private String name;

    private String host;

    private Integer aliveVehicleCount;

    private Integer alivePlatformCount;

}
