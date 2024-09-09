package com.mqttsnet.thinglinks.open.exp.example.tcpserver.decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * -----------------------------------------------------------------------------
 * File Name: BaseProtocolDecoder
 * -----------------------------------------------------------------------------
 * Description: 基础协议解析处理器
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
 * @date 2024/9/8 18:49
 */
@Slf4j
public abstract class BaseProtocolDecoder extends ChannelInboundHandlerAdapter {

    /**
     * 判断消息是否可以被当前解码器解码
     *
     * @param msg 消息对象
     * @return true 表示可以解码，false 表示不支持
     */
    protected abstract boolean canDecode(Object msg);

    /**
     * 具体的解码逻辑
     *
     * @param msg 消息对象
     * @return 解码后的结果
     * @throws Exception 解析异常
     */
    protected abstract Object decode(Object msg) throws Exception;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (canDecode(msg)) {
            Object decodedMessage = decode(msg);
            if (decodedMessage != null) {
                ctx.fireChannelRead(decodedMessage);
            } else {
                log.warn("Decoding failed, message is null.");
            }
        } else {
            log.warn("Cannot decode message, passing to the next handler.");
            ctx.fireChannelRead(msg);  // 如果不能解码，则传递给下一个处理器
        }
    }
}
