package com.camellya.gbt32960_3_tcp_server.protocol;

import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class InfoModel {

    private final TimeModel time;

    private final List<Byte> infoBytes;

    public InfoModel(List<Byte> byteList) {
        time = new TimeModel(byteList);
        infoBytes = byteList.subList(6, byteList.size());
    }

    public Date getTime() {
        return time.getDate();
    }

}
