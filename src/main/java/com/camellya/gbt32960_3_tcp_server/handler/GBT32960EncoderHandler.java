package com.camellya.gbt32960_3_tcp_server.handler;


import com.camellya.gbt32960_3_tcp_server.constant.consist.ProtocolConstants;
import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class GBT32960EncoderHandler extends MessageToByteEncoder<GBT32960Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, GBT32960Packet msg, ByteBuf out) {
        // 将起始字符写入到ByteBuf
        out.writeBytes(ProtocolConstants.START);

        // 写入命令标识
        out.writeByte(msg.getCommandFlag());

        // 写入应答标识
        out.writeByte(msg.getAckFlag());

        // 写入VIN号
        out.writeBytes(msg.getVIN().getBytes(StandardCharsets.UTF_8));

        // 写入加密方式
        out.writeByte(msg.getEncryMode());

        // 写入数据单元长度
        out.writeChar(msg.getDataLength());

        // 写入数据单元
        List<Byte> dataList = msg.getData();
        for (Byte b : dataList) {
            out.writeByte(b);
        }

        // 写入校验码
        out.writeByte(msg.getVerify());
    }
}
