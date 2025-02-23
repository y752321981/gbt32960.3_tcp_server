package com.camellya.gbt32960_3_tcp_server.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import com.camellya.gbt32960_3_tcp_server.config.TcpServerProperties;
import com.camellya.gbt32960_3_tcp_server.constant.consist.RedisConstants;
import com.camellya.gbt32960_3_tcp_server.model.entity.VehicleInfo;
import com.camellya.gbt32960_3_tcp_server.service.IChannelService;
import com.camellya.gbt32960_3_tcp_server.service.IVehicleService;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VehicleServiceImpl implements IVehicleService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private TcpServerProperties properties;

    @Override
    public boolean login(String vin, String iccid) {
        return true;
    }

    @Override
    public void saveOrUpdateCache(ChannelHandlerContext context, VehicleInfo info) {
        String key = RedisConstants.VEHICLE_INFO + IChannelService.getChannelIdString(context);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(info));
        redisTemplate.expire(key, properties.getHeartSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void refreshCacheExpire(ChannelHandlerContext context) {
        String key = RedisConstants.VEHICLE_INFO + IChannelService.getChannelIdString(context);
        redisTemplate.expire(key, properties.getHeartSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void deleteCache(ChannelHandlerContext context) {
        String key = RedisConstants.VEHICLE_INFO + IChannelService.getChannelIdString(context);
        redisTemplate.delete(key);
    }
}
