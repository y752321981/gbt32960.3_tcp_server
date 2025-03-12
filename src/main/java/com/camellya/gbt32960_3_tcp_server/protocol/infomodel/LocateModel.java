package com.camellya.gbt32960_3_tcp_server.protocol.infomodel;

import com.camellya.gbt32960_3_tcp_server.utils.ByteUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class LocateModel extends BaseInfoModel {

    private static final int FIXED_LENGTH = 9;

    /**
     * 定位状态
     */
    private Byte status;

    /**
     * 经度（单位百万分之一度）
     */
    private Long longitude;

    /**
     * 纬度 （单位百万分之一度）
     */
    private Long latitude;

    @Override
    public int getLength() {
        return FIXED_LENGTH;
    }

    /**
     * @return {@code true} 有效 {@code false} 有效
     */
    public boolean isValid() {
        return (status & 0x01) != 0x01;
    }

    /**
     * @return {@code true} 北纬 {@code false} 南纬
     */
    public boolean isNorthLatitude() {
        return (status & 0x02) != 0x02;
    }

    /**
     * @return {@code true} 东经 {@code false} 西经
     */
    public boolean isEastLongitude() {
        return (status & 0x04) != 0x04;
    }

    /**
     * 获取经度 （单位度）
     * @return 经度
     */
    public BigDecimal getLongitudeDegree() {
        BigDecimal bigDecimal = new BigDecimal(longitude);
        BigDecimal bigDecimal1 = new BigDecimal(1000000);
        return bigDecimal.divide(bigDecimal1, 6, RoundingMode.HALF_UP);
    }

    /**
     * 获取纬度 （单位度）
     * @return 纬度
     */
    public BigDecimal getLatitudeDegree() {
        BigDecimal bigDecimal = new BigDecimal(latitude);
        BigDecimal bigDecimal1 = new BigDecimal(1000000);
        return bigDecimal.divide(bigDecimal1, 6, RoundingMode.HALF_UP);
    }

    public LocateModel(List<Byte> byteList) {
        this.status = byteList.get(0);
        this.longitude = ByteUtil.byteArrayToUnsignedInt(byteList.subList(1, 5));
        this.latitude = ByteUtil.byteArrayToUnsignedInt(byteList.subList(5, 9));
    }
}
