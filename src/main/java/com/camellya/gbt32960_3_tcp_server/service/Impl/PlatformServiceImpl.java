package com.camellya.gbt32960_3_tcp_server.service.Impl;

import com.camellya.gbt32960_3_tcp_server.service.IPlatformService;
import org.springframework.stereotype.Service;

@Service
public class PlatformServiceImpl implements IPlatformService {
    @Override
    public boolean login(String username, String password) {
        return true;
    }
}
