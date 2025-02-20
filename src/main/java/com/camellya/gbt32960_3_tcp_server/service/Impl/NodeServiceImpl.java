package com.camellya.gbt32960_3_tcp_server.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.camellya.gbt32960_3_tcp_server.config.NodeConfig;
import com.camellya.gbt32960_3_tcp_server.constant.consist.RedisConstants;
import com.camellya.gbt32960_3_tcp_server.model.entity.NodeInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NodeServiceImpl {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private NodeConfig nodeConfig;

    private NodeInfo nodeInfo;

    @PostConstruct
    public void registerNode() {
        nodeInfo = new NodeInfo();
        nodeInfo.setHost(nodeConfig.getHost());
        nodeInfo.setName(nodeConfig.getName());
        nodeInfo.setAliveVehicleCount(0);
        nodeInfo.setAlivePlatformCount(0);
        redisTemplate.opsForHash().put(RedisConstants.NODE_INFO, nodeConfig.getName(), JSON.toJSONString(nodeInfo));
    }

    @Scheduled(fixedRate = 10000)
    public void updateNodeInfo() {
        redisTemplate.opsForHash().put(RedisConstants.NODE_INFO, nodeConfig.getName(), JSON.toJSONString(nodeInfo));
    }

}
