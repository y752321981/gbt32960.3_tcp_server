package com.camellya.gbt32960_3_tcp_server.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.camellya.gbt32960_3_tcp_server.mapper.OriginRecordMapper;
import com.camellya.gbt32960_3_tcp_server.model.entity.OriginRecord;
import com.camellya.gbt32960_3_tcp_server.service.IOriginRecordService;
import org.springframework.stereotype.Service;

@Service
public class OriginRecordServiceImpl extends ServiceImpl<OriginRecordMapper, OriginRecord> implements IOriginRecordService {
}
