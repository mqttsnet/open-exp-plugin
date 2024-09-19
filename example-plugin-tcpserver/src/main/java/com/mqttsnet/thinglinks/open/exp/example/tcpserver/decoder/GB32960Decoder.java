package com.mqttsnet.thinglinks.open.exp.example.tcpserver.decoder;

import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.entity.GB32960MessageParser;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.entity.dao.GB32960MessageData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * -----------------------------------------------------------------------------
 * File Name: GB32960Decoder
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * 处理GB32960协议的粘包、拆包问题，并负责帧的完整性校验和具体解析业务逻辑。
 * -----------------------------------------------------------------------------
 *
 * @author xiaonannet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2024/9/9       xiaonannet        1.0        完整代码整合和优化
 * -----------------------------------------------------------------------------
 * @email
 * @date 2024/9/9 12:43
 */
@Slf4j
public class GB32960Decoder extends ByteToMessageDecoder {

    private static final int MIN_MESSAGE_LENGTH = 24; // 最小消息长度（包括固定头部）

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("GB32960Decoder Received bytes: {}", ByteBufUtil.hexDump(in).toUpperCase());

        // 如果可读字节数少于最小长度，返回并等待更多数据
        if (in.readableBytes() < MIN_MESSAGE_LENGTH) {
            return;
        }

        // 标记当前读指针，以便在数据不足时回退
        in.markReaderIndex();

        // 检查起始符号是否为 0x23 0x23（不移动读指针）
        if (!canDecodeGB32960(in)) {
            log.warn("Invalid start bytes, resetting reader index");
            in.resetReaderIndex(); // 恢复指针位置
            return;
        }

        // 保存当前完整的 ByteBuf 副本，以便稍后解析
        ByteBuf fullFrame = in.copy(); // 使用 copy() 创建完整的副本，包含全部未读字节

        // 读取命令标识 (1字节) 使用 getByte 方法而不改变读指针
        byte commandFlag = in.getByte(in.readerIndex() + 2);
        log.info("Command Flag: {}", commandFlag);

        // 读取应答标识 (1字节) 使用 getByte
        byte responseFlag = in.getByte(in.readerIndex() + 3);
        log.info("Response Flag: {}", responseFlag);

        // 读取唯一识别码 (17字节) 使用 getBytes
        byte[] vinBytes = new byte[17];
        in.getBytes(in.readerIndex() + 4, vinBytes);
        String uniqueIdentifier = new String(vinBytes, StandardCharsets.UTF_8);
        log.info("Unique Identifier (VIN): {}", uniqueIdentifier);

        // 读取加密方式 (1字节) 使用 getByte
        byte encryptionFlag = in.getByte(in.readerIndex() + 21);
        log.info("Encryption Flag: {}", encryptionFlag);

        // 读取数据单元长度字段（2字节） 使用 getUnsignedShort
        int dataCellLength = in.getUnsignedShort(in.readerIndex() + 22);
        log.info("Data Cell Length: {}", dataCellLength);

        // 计算消息的总长度 = 固定头部 + 数据单元长度 + 校验码（1字节）
        int totalMessageLength = MIN_MESSAGE_LENGTH + dataCellLength + 1;

        // 检查是否有足够的可读数据，如果没有则等待
        if (in.readableBytes() < totalMessageLength) {
            log.warn("Not enough data, waiting for more");
            in.resetReaderIndex(); // 恢复读指针，等待更多数据
            return;
        }

        // TODO BCC验证

        // 将完整的消息传递给解析器
        GB32960MessageData messageData = GB32960MessageParser.parse(fullFrame);
        // 将解析后的数据传递给下一个处理器
        out.add(messageData);
        in.clear();
    }

    /**
     * 计算校验码（BCC），根据 GB32960 协议从命令标识开始到数据单元的最后一个字节
     *
     * @param frame 完整的 ByteBuf 帧
     * @return 计算出的校验码
     */
    public static byte calculateCheckCode(ByteBuf frame) {
        byte checkCode = 0;
        for (int i = 0; i < frame.readableBytes(); i++) {
            checkCode ^= frame.getByte(i);
        }
        return checkCode;
    }

    /**
     * 判断消息是否为 GB32960 协议
     * 通过检查前两个字节是否为 0x23 0x23
     */
    private boolean canDecodeGB32960(ByteBuf buf) {
        if (buf.readableBytes() < 2) {
            return false;
        }

        byte firstByte = buf.getByte(buf.readerIndex());
        byte secondByte = buf.getByte(buf.readerIndex() + 1);

        return firstByte == 0x23 && secondByte == 0x23;
    }
}
