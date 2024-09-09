package com.mqttsnet.thinglinks.open.exp.example.tcpserver.decoder;

/**
 * -----------------------------------------------------------------------------
 * File Name: DecoderHandler
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * 解码器，用于将字节流解码为字符串消息。
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
 * @date 2024/9/8 17:25
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class DecoderHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 将字节缓冲区中的数据转换为字符串并传递给下一个处理器
        String decodedMsg = in.toString(StandardCharsets.UTF_8).trim();
        log.info("Decoded message: {}", decodedMsg);
        out.add(decodedMsg);
    }
}
