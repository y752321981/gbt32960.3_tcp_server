package com.camellya.gbt32960_3_tcp_server.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.yin.tcpserver.annotation.Command;
import com.yin.tcpserver.pojo.constant.RedisKeyConstants;
import com.yin.tcpserver.pojo.entity.LoginInfo;
import com.yin.tcpserver.pojo.entity.VehicleRuntimeInfo;
import com.yin.tcpserver.pojo.enums.AckEnum;
import com.yin.tcpserver.pojo.enums.InfoReportDataEnum;
import com.yin.tcpserver.pojo.protocol.GBT32960Packet;
import com.yin.tcpserver.pojo.protocol.InfoModel;
import com.yin.tcpserver.pojo.protocol.LoginModel;
import com.yin.tcpserver.pojo.protocol.infomodel.BaseInfoModel;
import com.yin.tcpserver.pojo.protocol.infomodel.LocateModel;
import com.yin.tcpserver.pojo.protocol.infomodel.VehicleModel;
import com.yin.tcpserver.service.IAuthService;
import com.yin.tcpserver.service.IChannelService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yin.tcpserver.pojo.enums.CommandEnum.*;

@Slf4j
@Component
public class CommandHandler {

    @Resource
    private IAuthService authService;

    @Resource
    private IChannelService channelService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Command(VEHICLE_LOGIN)
    public void vehicleLogin(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {
        LoginModel loginModel = new LoginModel(packet.getData());
        String vin = packet.getVIN();
        boolean success = authService.vehicleLogin(vin, loginModel.getIccid());
        if (success) {
            if (redisTemplate.opsForHash().hasKey(RedisKeyConstants.VEHICLE_LOGIN, vin)) {
                Channel channel = channelService.getChannel(vin, true);
                if (channel != null) {
                    log.warn("此车辆已登录，关闭此前的连接,vin:{},channelId:{}", vin, channel.id());
                    channelService.closeAndClean(vin, true);
                } else {
                    redisTemplate.opsForHash().delete(RedisKeyConstants.VEHICLE_LOGIN, vin);
                }
            }
            channelService.vehicleLogin(channelHandlerContext, vin);
            channelService.sendMessage(channelHandlerContext, packet.makeResponse(AckEnum.SUCCESS));
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setLoginTime(new Date());
            loginInfo.setIp(channelHandlerContext.channel().remoteAddress().toString());
            loginInfo.setVin(packet.getVIN());
            redisTemplate.opsForHash().put(RedisKeyConstants.VEHICLE_LOGIN, vin, JSONObject.toJSONString(loginInfo));
        } else {
            channelService.sendMessage(channelHandlerContext, packet.makeResponse(AckEnum.FAIL));
            channelService.closeAndClean(channelHandlerContext);
        }
    }
    private Map<InfoReportDataEnum, BaseInfoModel> decodeReportData(List<Byte> byteList) {
        Map<InfoReportDataEnum, BaseInfoModel> map = new HashMap<>();
        if (CollectionUtil.isEmpty(byteList)) {
            return map;
        }
        int length = byteList.size();
        InfoReportDataEnum infoReportDataEnum = InfoReportDataEnum.getInfoReportDataEnum(byteList.get(0));
        BaseInfoModel baseInfoModel = BaseInfoModel.makeInfoModel(infoReportDataEnum, byteList.subList(1, byteList.size()));
        map.put(infoReportDataEnum, baseInfoModel);
        if (baseInfoModel.getLength() == 0) {
            return map;
        }
        if (length - baseInfoModel.getLength() <= 1) {
            return map;
        }
        if (1 + baseInfoModel.getLength() >= length - baseInfoModel.getLength() - 1) {
            return map;
        }
        Map<InfoReportDataEnum, BaseInfoModel> infoReportDataEnumBaseInfoModelMap = decodeReportData(byteList.subList(1 + baseInfoModel.getLength(), length - baseInfoModel.getLength() - 1));
        map.putAll(infoReportDataEnumBaseInfoModelMap);
        return map;
    }

    private void reportInfo(String vin, InfoModel infoModel) {
        Map<InfoReportDataEnum, BaseInfoModel> maps = decodeReportData(infoModel.getInfoBytes());
        VehicleRuntimeInfo info = null;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(RedisKeyConstants.VEHICLE_RUNTIME_KEY)) && redisTemplate.opsForHash().hasKey(RedisKeyConstants.VEHICLE_RUNTIME_KEY, vin)) {
            info = JSONObject.parseObject((String)redisTemplate.opsForHash().get(RedisKeyConstants.VEHICLE_RUNTIME_KEY, vin), VehicleRuntimeInfo.class);
        }
        if (info == null) {
            info = new VehicleRuntimeInfo();
        }
        for (Map.Entry<InfoReportDataEnum, BaseInfoModel> entry : maps.entrySet()) {
            switch (entry.getKey()) {
                case VEHICLE -> {
                    VehicleModel value = (VehicleModel) entry.getValue();
                    info.setVehicleStatus(value.getVehicleStatus().toString());
                    info.setChargeStatus(value.getChargeStatus().toString());
                    info.setRunMode(value.getRunModel().toString());
                    info.setSpeed(value.getSpeed() / 10.0F);
                    info.setTotalMileage(value.getTotalMileage());
                    info.setVoltage(value.getVoltage() / 10.0F);
                    info.setCurrent(value.getCurrent() / 10.0F);
                    info.setSoc(value.getSoc().intValue());
                    info.setDcDcStatus(value.getDcDc().toString());
                    info.setGear(value.getGear().toString());
                    info.setInsulationResistance(Integer.valueOf(value.getInsulationResistance()));
                }
                case DRIVER_MOTOR -> {
                }
                case FUEL_CELL -> {
                }
                case ENGINE -> {
                }
                case LOCATION -> {
                    LocateModel value = (LocateModel) entry.getValue();
                    if (!value.getValid()) {
                        break;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", value.getLatitude());
                    map.put("lon", value.getLongitude());
                    info.setLat(value.getLatitude());
                    info.setLon(value.getLongitude());
                    info.setLocationTime(infoModel.getTime());
                }
                case EXTREMUM -> {
                }
                case ALARM -> {
                }
                case UNKNOWN -> {
                }
            }
        }
    }

    @Command(REPORT_INFO_REAL)
    public void reportRealtime(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {
        InfoModel infoModel = new InfoModel(packet.getData());
        String vin = packet.getVIN();

    }

    @Command(REPORT_INFO_RESEND)
    public void reportResend(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {

    }

    @Command(VEHICLE_LOGOUT)
    public void vehicleLogout(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {

    }

    @Command(PLATFORM_LOGIN)
    public void platformLogin(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {

    }

    @Command(PLATFORM_LOGOUT)
    public void platformLogout(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {

    }

    @Command(UNKNOWN)
    public void unknownCommand(ChannelHandlerContext channelHandlerContext, GBT32960Packet packet) {

    }

}
