package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.encoder;


import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * -----------------------------------------------------------------------------
 * File Name: EncoderHandler
 * -----------------------------------------------------------------------------
 * Description:
 * 编码器，将字符串消息编码为字节流。
 * -----------------------------------------------------------------------------
 *
 * @author mqttsnet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2024/9/8       mqttsnet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2024/9/8 17:26
 */
@Slf4j
public class EncoderHandler extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        // 将字符串编码为字节并写入缓冲区
        byte[] msgBytes = (msg + "\n").getBytes(StandardCharsets.UTF_8);  // 加上定界符（换行符）
        out.writeBytes(msgBytes);
        log.info("Encoded message: {}", msg);
    }
}

