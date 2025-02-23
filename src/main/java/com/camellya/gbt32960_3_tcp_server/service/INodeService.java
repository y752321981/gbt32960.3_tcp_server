package com.camellya.gbt32960_3_tcp_server.service;

import com.camellya.gbt32960_3_tcp_server.model.entity.NodeInfo;

import java.util.List;

public interface INodeService {

    String getNodeName();

    void updateAliveCount(Integer vehicle, Integer platform);

    List<NodeInfo> getNodeList();
}
