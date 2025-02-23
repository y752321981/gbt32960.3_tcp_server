package com.camellya.gbt32960_3_tcp_server.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.camellya.gbt32960_3_tcp_server.config.NodeConfig;
import com.camellya.gbt32960_3_tcp_server.constant.consist.RedisConstants;
import com.camellya.gbt32960_3_tcp_server.model.entity.NodeInfo;
import com.camellya.gbt32960_3_tcp_server.service.INodeService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NodeServiceImpl implements INodeService {

    @Resource
    private RedisTemplate<String, NodeInfo> redisTemplate;

    @Resource
    private NodeConfig nodeConfig;

    private NodeInfo nodeInfo;

    private String key;

    @PostConstruct
    public void registerNode() {
        nodeInfo = new NodeInfo();
        nodeInfo.setHost(nodeConfig.getHost());
        nodeInfo.setName(nodeConfig.getName() + "_" + RandomUtil.randomString(6));
        nodeInfo.setAliveVehicleCount(0);
        nodeInfo.setAlivePlatformCount(0);
        key = RedisConstants.NODE_INFO + nodeInfo.getName();
        redisTemplate.opsForValue().set(key, nodeInfo);
        redisTemplate.expire(key, 20, TimeUnit.SECONDS);
        log.info("注册节点: {}", nodeInfo);
    }

    @Scheduled(fixedRate = 10000)
    public void updateNodeInfo() {
        redisTemplate.opsForValue().set(key, nodeInfo);
        redisTemplate.expire(key, 20, TimeUnit.SECONDS);
    }

    @Override
    public String getNodeName() {
        return nodeInfo.getName();
    }

    @Override
    public void updateAliveCount(Integer vehicle, Integer platform) {
        nodeInfo.setAliveVehicleCount(vehicle);
        nodeInfo.setAlivePlatformCount(platform);
    }

    @Override
    public List<NodeInfo> getNodeList() {
        ScanOptions options = ScanOptions.scanOptions().match(RedisConstants.NODE_INFO + "*").build();
        List<NodeInfo> list = new ArrayList<>();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                if (redisTemplate.hasKey(key)) {
                   list.add(redisTemplate.opsForValue().get(key));
                }
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }
}
