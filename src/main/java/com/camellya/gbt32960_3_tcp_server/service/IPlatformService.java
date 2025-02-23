package com.camellya.gbt32960_3_tcp_server.service;

import com.camellya.gbt32960_3_tcp_server.model.entity.PlatformInfo;
import io.netty.channel.ChannelHandlerContext;

public interface IPlatformService {

    boolean login(String username, String password);

    void saveOrUpdateCache(ChannelHandlerContext context, PlatformInfo info);

    void refreshCacheExpire(ChannelHandlerContext context);

    void deleteCache(ChannelHandlerContext context);
}
