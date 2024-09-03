package cn.think.in.java.open.exp.example.udptomqtt;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author lin
 * @date 2024年08月30日 16:40
 */
public class BootNettyUdpBootstrapServer extends BootNettyUdpAbstractBootstrapServer{
    private EventLoopGroup eventLoopGroup;

    /**
     * 启动服务
     */
    public void startup(int port) {

        eventLoopGroup = new NioEventLoopGroup(20);
        try {
            Bootstrap serverBootstrap = new Bootstrap();
            serverBootstrap = serverBootstrap.group(eventLoopGroup);
            serverBootstrap = serverBootstrap.channel(NioDatagramChannel.class);
            serverBootstrap = serverBootstrap.option(ChannelOption.SO_BROADCAST, true);
            serverBootstrap = serverBootstrap.handler(new ChannelInitializer<NioDatagramChannel>(){
                @Override
                protected void initChannel(NioDatagramChannel ch) throws Exception {
                    initChannelHandler(ch.pipeline());
                }
            });
            ChannelFuture f = serverBootstrap.bind(port).sync();
            if(f.isSuccess()) {
                System.out.println("netty udp start "+port);
                f.channel().closeFuture().sync();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            System.out.println("netty udp close!");
            eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }
}
