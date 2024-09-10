package com.mqttsnet.thinglinks.open.exp.example.tcpserver.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * -----------------------------------------------------------------------------
 * File Name: GB32960Decoder
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * -----------------------------------------------------------------------------
 *
 * @author xiaonannet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2024/9/8       xiaonannet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2024/9/8 18:50
 */
@Slf4j
public class GB32960Decoder extends BaseProtocolDecoder {

    private static final int MIN_MESSAGE_LENGTH = 12; // 假设GB32960最小消息长度

    @Override
    protected boolean canDecode(Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        // 判断是否为GB32960协议，起始符号为0x2323
        if (buf.readableBytes() >= 2) {
            byte firstByte = buf.getByte(buf.readerIndex());
            byte secondByte = buf.getByte(buf.readerIndex() + 1);
            boolean canDecode = (firstByte == 0x23) && (secondByte == 0x23);
            log.info("GB32960 protocol start detected: {}", canDecode);
            return canDecode;
        }
        return false;
    }

    @Override
    protected Object decode(Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            log.error("Expected ByteBuf but received: {}", msg.getClass().getName());
            throw new IllegalArgumentException("Invalid message type");
        }

        ByteBuf buf = (ByteBuf) msg;

        if (buf.readableBytes() < MIN_MESSAGE_LENGTH) {
            log.warn("Message is too short, ignoring.");
            return null;
        }

        // 读取消息头：0x23 0x23
        buf.skipBytes(2);

        // 获取剩余的消息内容
        byte[] msgBytes = new byte[buf.readableBytes()];
        buf.readBytes(msgBytes);

        String message = ByteBufUtil.hexDump(msgBytes).toUpperCase();
        log.info("Decoded GB32960 message: {}", message);

        // 返回字符串消息，而不是 ByteBuf，防止重复解析
        return message;
    }
}

