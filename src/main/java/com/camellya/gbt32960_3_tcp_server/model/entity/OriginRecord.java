package com.camellya.gbt32960_3_tcp_server.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
@TableName("origin_record")
public class OriginRecord {

    @TableId
    private Long id;

    @TableField
    private Byte[] origin;

    @TableField
    private String vin;

    @TableField
    private Date time;
}
