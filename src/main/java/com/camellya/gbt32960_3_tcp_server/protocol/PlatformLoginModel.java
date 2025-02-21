package com.camellya.gbt32960_3_tcp_server.protocol;

import com.camellya.gbt32960_3_tcp_server.utils.ByteUtil;
import lombok.Data;

import java.util.List;

@Data
public class PlatformLoginModel {

    private final TimeModel time;

    /**
     * 流水号
     */
    private final Character serialNumber;

    private final String username;

    private final String password;

    private final Byte encodeRule;

    public PlatformLoginModel(List<Byte> bytes) {
        time = new TimeModel(bytes);
        serialNumber = (char) (((bytes.get(6) & 0xFF) << 8 ) | (bytes.get(7) & 0xFF));
        username = ByteUtil.byteArrayToString(bytes.subList(8, 20), 12);
        password = ByteUtil.byteArrayToString(bytes.subList(20, 40), 20);
        encodeRule = bytes.get(40);
    }
}
