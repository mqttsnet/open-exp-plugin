package cn.think.in.java.open.exp.example.tcpserver;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author lin
 * @date 2024年08月29日 17:32
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Server received: " + msg.toString());

        // 发送消息回客户端
        ctx.write(Unpooled.copiedBuffer("Hello from server!", CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();  // 刷新消息到客户端
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
