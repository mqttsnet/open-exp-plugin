package cn.think.in.java.open.exp.example.udpserver;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * @author lin
 * @date 2024年08月30日 17:03
 */
public class BootNettyUdpSimpleChannelInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        long time_stamp = System.currentTimeMillis()/1000;
        System.out.println("received--data:"+packet.content().toString(CharsetUtil.UTF_8)+"--ip:"+packet.sender().getAddress()+"--port:"+packet.sender().getPort());
        try {
            BootNettyUdpData bootNettyUdpData = new BootNettyUdpData();
            bootNettyUdpData.setAddress(packet.sender().getAddress().toString());
            bootNettyUdpData.setContent(packet.content().toString(CharsetUtil.UTF_8));
            bootNettyUdpData.setTime_stamp(time_stamp);
            BootNettyUdpDataCache.bootNettyUdpDataList.add(bootNettyUdpData);
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(time_stamp+"", CharsetUtil.UTF_8), packet.sender()));
        } catch (Exception e) {
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("exception", CharsetUtil.UTF_8), packet.sender()));
            System.out.println("received exception--data:"+packet.content().toString(CharsetUtil.UTF_8)+"--"+e.toString());
        }

    }
}
