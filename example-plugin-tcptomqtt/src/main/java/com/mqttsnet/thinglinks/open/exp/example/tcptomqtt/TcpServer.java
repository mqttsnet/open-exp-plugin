package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.initializer.MyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lin
 * @date 2024年08月29日 16:33
 */
@Slf4j
@Component
public class TcpServer {

    private final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 初始化并启动Netty TCP服务器。
     *
     * @param port 启动时使用的端口号
     * @throws Exception 初始化失败时抛出异常
     */
    public void start(int port) throws Exception {
        // Boss线程组：用于处理连接请求
        EventLoopGroup bossGroup = new NioEventLoopGroup(4);

        // Worker线程组：用于处理连接后的I/O事件
        EventLoopGroup workerGroup = new NioEventLoopGroup(Integer.parseInt(Boot.workerThreads.getDefaultValue()));

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)  // 设置为NIO通道
                    .option(ChannelOption.SO_BACKLOG, Integer.parseInt(Boot.soBacklog.getDefaultValue()))  // 设置连接队列最大长度
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(Boot.tcpNoDelay.getDefaultValue()))  // 是否禁用Nagle算法
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.parseBoolean(Boot.soKeepAlive.getDefaultValue()))  // 是否启用TCP KeepAlive
                    .handler(new LoggingHandler())  // 日志处理器
                    .childHandler(new MyServerInitializer());

            log.info("Netty TCP服务器正在端口 {} 上启动", port);
            ChannelFuture future = bootstrap.bind(port).sync();  // 绑定端口并启动
            future.channel().closeFuture().sync();  // 等待关闭
        } finally {
            // 优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Netty TCP服务器已停止。");
        }
    }

    /**
     * 优雅关闭服务器
     */
    @PreDestroy
    public void shutdown() {
        log.info("正在关闭Netty TCP服务器...");
        if (allChannels != null) {
            allChannels.close();  // 关闭所有活动的通道
        }
        log.info("Netty TCP服务器已成功关闭");
    }
}


