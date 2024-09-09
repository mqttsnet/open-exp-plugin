package com.mqttsnet.thinglinks.open.exp.example.tcpserver.dispatcher;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * -----------------------------------------------------------------------------
 * File Name: MessageDispatcher
 * -----------------------------------------------------------------------------
 * Description:
 * 消息分发器，根据消息类型将消息转发到相应的处理器。
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
 * @date 2024/9/8 18:54
 */
@Slf4j
public class MessageDispatcher extends ChannelInboundHandlerAdapter {

    // 使用线程安全的 ConcurrentHashMap 存储消息类型和处理器
    private final Map<String, ChannelHandler> handlerMap = new ConcurrentHashMap<>();

    /**
     * 为特定的协议类型注册处理器。
     *
     * @param protocolType 协议类型的字符串
     * @param handler      处理该协议类型的处理器
     */
    public void registerHandler(String protocolType, ChannelHandler handler) {
        handlerMap.put(protocolType, handler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof String)) {
            log.warn("Received message is not of type String: {}", msg.getClass().getSimpleName());
            ctx.fireChannelRead(msg); // 将消息传递给下一个处理器
            return;
        }

        String message = (String) msg;
        String protocolType = extractProtocolType(message);  // 提取协议类型

        ChannelHandler handler = handlerMap.get(protocolType);

        if (handler != null) {
            log.info("Dispatching message of type {} to handler {}", protocolType, handler.getClass().getSimpleName());

            // 调用相应的处理器
            if (handler instanceof ChannelInboundHandlerAdapter) {
                ((ChannelInboundHandlerAdapter) handler).channelRead(ctx, msg);
            } else {
                log.warn("Handler for protocol type {} is not a valid inbound handler", protocolType);
            }

            // 处理完毕，不传递给下一个 Handler
            return;  // 停止消息继续传播
        }

        // 如果没有匹配到处理器，传递消息给下一个处理器
        log.warn("No handler found for protocol type: {}", protocolType);
        ctx.fireChannelRead(msg);  // 传递给下一个 Handler
    }

    /**
     * 根据消息内容提取协议类型
     *
     * @param msg 消息内容
     * @return 协议类型字符串
     */
    private String extractProtocolType(String msg) {
        // 示例：假设前两个字符是协议类型标识符
        if (msg.startsWith("2323")) {
            return "GB32960-RealTimeData";
        } else if (msg.startsWith("2424")) {
            return "GB32960-AlarmData";
        } else {
            return "Unknown";
        }
    }
}
