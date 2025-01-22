package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.custom.CustomMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * -----------------------------------------------------------------------------
 * File Name: CustomProtocolDecoder
 * -----------------------------------------------------------------------------
 * Description:
 * 自定义协议解析器
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
public class CustomProtocolDecoder extends ByteToMessageDecoder {

    private static final String DELIMITER = "\n"; // 使用换行符作为定界符

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有数据可读取
        if (in.readableBytes() <= 0) {
            return;
        }

        // 将 ByteBuf 转换为字符串
        String decodedData = in.toString(StandardCharsets.UTF_8);

        // 根据定界符拆分消息
        String[] messages = decodedData.split(DELIMITER);

        for (String message : messages) {
            if (message.isEmpty()) {
                continue;
            }

            // 处理每个消息，这里可以根据实际协议进行解析
            log.info("Decoded custom protocol message: {}", message);

            // 将解析后的消息传递给下一个处理器
            out.add(parseCustomMessage(message));
        }
    }

    // 自定义消息的解析逻辑
    private CustomMessage parseCustomMessage(String message) {
        // 假设消息格式为: "messageType:payload"
        String[] parts = message.split(":");

        if (parts.length == 2) {
            String messageType = parts[0].trim();
            String payload = parts[1].trim();

            log.info("Parsed custom message - Type: {}, Payload: {}", messageType, payload);

            // 创建并返回 CustomMessage 对象
            return new CustomMessage(messageType, payload);
        } else {
            log.warn("Received invalid message format: {}", message);
            return null;  // 如果消息格式不正确，返回null或者抛出异常
        }
    }

}

