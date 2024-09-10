package com.mqttsnet.thinglinks.open.exp.example.tcpserver.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义业务处理器，处理TCP连接的读写事件。
 *
 * @author mqttsnet
 */
@Slf4j
@Deprecated
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端连接到服务器时，打印连接信息。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.info("客户端连接建立，地址: {}", clientAddress);
        super.channelActive(ctx);
    }

    /**
     * 处理从客户端读取到的数据，并打印日志。
     *
     * @param ctx ChannelHandlerContext上下文
     * @param msg 读取到的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.info("服务器接收到来自客户端[{}]的消息: {}", clientAddress, msg.toString());

        // 发送消息回客户端
        String response = "服务器回复: Hello!";
        ctx.write(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
        log.info("发送回复给客户端[{}]: {}", clientAddress, response);
    }

    /**
     * 消息读取完成时，刷新消息到客户端并打印日志。
     *
     * @param ctx ChannelHandlerContext上下文
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.info("消息已发送完毕，客户端[{}]", clientAddress);
    }

    /**
     * 当客户端断开连接时，打印断开信息。
     *
     * @param ctx ChannelHandlerContext上下文
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.info("客户端连接断开，地址: {}", clientAddress);
        super.channelInactive(ctx);
    }

    /**
     * 捕获异常并关闭连接，同时打印异常日志。
     *
     * @param ctx   ChannelHandlerContext上下文
     * @param cause 异常信息
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String clientAddress = ctx.channel().remoteAddress().toString();
        log.error("发生异常，关闭客户端连接[{}]", clientAddress, cause);
        ctx.close();
    }
}
