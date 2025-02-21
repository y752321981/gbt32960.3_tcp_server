package com.camellya.gbt32960_3_tcp_server.service.Impl;

import com.camellya.gbt32960_3_tcp_server.service.IVehicleService;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements IVehicleService {
    @Override
    public boolean login(String vin, String iccid) {
        return true;
    }
}
