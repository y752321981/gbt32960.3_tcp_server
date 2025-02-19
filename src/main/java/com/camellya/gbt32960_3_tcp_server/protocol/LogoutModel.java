package com.camellya.gbt32960_3_tcp_server.protocol;

import lombok.Data;

import java.util.List;

@Data
public class LogoutModel {

    private TimeModel time;

    private Character serialNumber;

    public LogoutModel(List<Byte> byteList) {
        time = new TimeModel(byteList);
        serialNumber = (char) (((byteList.get(6) & 0xFF) << 8 ) | (byteList.get(7) & 0xFF));
    }

}
