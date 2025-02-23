package com.camellya.gbt32960_3_tcp_server.handler;

import com.camellya.gbt32960_3_tcp_server.constant.consist.ProtocolConstants;
import com.camellya.gbt32960_3_tcp_server.protocol.GBT32960Packet;
import com.camellya.gbt32960_3_tcp_server.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class GBT32960DecoderHandler extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        List<Byte> list = new ArrayList<>(50);
        // 首先检查最小长度是否满足（起始字符 + 命令标识 + 应答标识 + VIN号 + 加密方式 + 数据单元长度 + 校验码）
        while (in.readableBytes() >= GBT32960Packet.MIN_LENGTH) {
            // 记录开始位置
            in.markReaderIndex();
            byte[] startBytes = new byte[2];
            in.readBytes(startBytes);
            if (ByteUtil.byteArrayEquals(startBytes, ProtocolConstants.START)) {
                GBT32960Packet message = new GBT32960Packet();

                list.clear();
                list.add(startBytes[0]);
                list.add(startBytes[1]);
                // 读取命令标识
                message.setCommandFlag(in.readByte());
                list.add(message.getCommandFlag());
                // 读取应答标识
                message.setAckFlag(in.readByte());
                list.add(message.getAckFlag());
                // 读取VIN号 (假设是17个字节)
                byte[] vinBytes = new byte[17];
                in.readBytes(vinBytes);
                message.setVin(new String(vinBytes));
                for (byte vinByte : vinBytes) {
                    list.add(vinByte);
                }
                // 读取加密方式
                byte encryptMode = in.readByte();
                message.setEncryptMode(encryptMode);
                list.add(message.getEncryptMode());
                // 读取数据单元长度
                char dataLength = in.readChar();
                list.add((byte) (dataLength >> 8));
                list.add((byte) (dataLength & 0x00FF));
                if (dataLength > 400) {
                    // 如果此包数据大于400字节，则为错误包，丢弃
                    continue;
                }

                message.setDataLength(dataLength);

                // 如果可读字节数少于剩余总长度，则回退并等待更多数据
                if (in.readableBytes() <= dataLength) {
                    in.resetReaderIndex();
                    return;
                }

                // 读取数据单元
                byte[] dataArray = new byte[dataLength];
                in.readBytes(dataArray);
                List<Byte> byteList = new ArrayList<>(dataLength);
                for (byte b : dataArray) {
                    byteList.add(b);
                    list.add(b);
                }
                message.setData(byteList);

                // 读取校验码
                message.setVerify(in.readByte());
                list.add(message.getVerify());
                if (!Objects.equals(message.calcVerifyCode(), message.getVerify())) {
                    log.info("接受数据包，校验错误");
                    // TODO: continue;
                }

                // 将解析后的对象添加到输出列表
                out.add(message);
            } else {
                // 没有匹配到"##"，退回到标记的位置再跳过一个字节，再重新读取一遍
                in.resetReaderIndex();
                in.readByte();
            }
        }
    }
}
