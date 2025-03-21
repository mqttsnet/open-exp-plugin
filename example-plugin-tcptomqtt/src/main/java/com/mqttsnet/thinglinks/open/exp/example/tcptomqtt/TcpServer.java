package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.initializer.MyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lin
 * @date 2024年08月29日 16:33
 */
@Slf4j
@Component
public class TcpServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final Integer port = Integer.parseInt(Boot.tcpPort.getDefaultValue());

    /**
     * 安全启动方法（带状态检查和中断处理）
     */
    public synchronized void start() {

        // 使用干净的线程组（防止旧线程残留）
        bossGroup = new NioEventLoopGroup(4);
        workerGroup = new NioEventLoopGroup(Integer.parseInt(Boot.workerThreads.getDefaultValue()));

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, Integer.parseInt(Boot.soBacklog.getDefaultValue()))
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(Boot.tcpNoDelay.getDefaultValue()))
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.parseBoolean(Boot.soKeepAlive.getDefaultValue()))
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MyServerInitializer());

            // 绑定端口并同步等待
            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();
            log.info("TCP服务已在端口 {} 启动", port);

            // 添加关闭监听器
            future.channel().closeFuture().addListener(f -> {
                log.info("TCP通道已关闭");
                shutdown();
            });

            // 阻塞直到通道关闭（支持中断）
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.warn("TCP服务启动被中断，执行清理");
            Thread.currentThread().interrupt();
            throw new IllegalStateException("启动被取消");
        } finally {
            // 确保意外退出时释放资源
            if (Thread.currentThread().isInterrupted()) {
                shutdownNow();
            }
        }
    }

    /**
     * 安全关闭方法（分阶段释放资源）
     */
    public void shutdown() {
        log.info("开始安全关闭TCP服务...");
        try {
            // 阶段1：关闭主通道
            closeMainChannel();

            // 阶段2：释放端口（核心改进点）
            releasePortWithGuarantee();

            // 阶段3：关闭客户端连接
            closeClientConnections();

            // 阶段4：关闭线程池
            shutdownEventLoopGroups();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("关闭流程被中断，启动强制关闭");
            shutdownNow();
        } catch (Exception e) {
            log.error("关闭异常", e);
            shutdownNow();
        } finally {
            cleanResources();
            log.info("TCP服务完全终止，端口:{}可用", port);
        }
    }

    private void closeMainChannel() throws InterruptedException {
        if (serverChannel != null && serverChannel.isOpen()) {
            log.info("关闭主监听通道...");
            serverChannel.close();
        }
    }

    private void releasePortWithGuarantee() {
        final int maxRetries = 5;
        final int retryIntervalMs = 1000;

        for (int i = 1; i <= maxRetries; i++) {
            try (ServerSocket testSocket = new ServerSocket()) {
                testSocket.setReuseAddress(true);
                testSocket.bind(new InetSocketAddress(port));
                log.info("端口验证成功：{} 已释放", port);
                return;
            } catch (IOException e) {
                log.warn("端口释放检查,端口释放失败（尝试 {}/{}）: {}", i, maxRetries, e.getMessage());
                try {
                    Thread.sleep(retryIntervalMs);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        log.error("端口:{} 强制释放失败，需要手动处理", port);
    }

    private void closeClientConnections() {
        if (channelGroup.isEmpty()) return;

        log.info("正在关闭{}个客户端连接...", channelGroup.size());
        // 温和关闭
        if (!channelGroup.close().awaitUninterruptibly(3, TimeUnit.SECONDS)) {
            int remaining = channelGroup.size();
            log.warn("检测到{}个顽固连接，强制关闭", remaining);
            // 暴力关闭
            channelGroup.forEach(channel -> {
                if (channel.isActive()) {
                    channel.unsafe().closeForcibly();
                }
            });
        }
    }

    private void shutdownEventLoopGroups() throws InterruptedException {
        // 温和模式关闭
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    private void shutdownNow() {
        try {
            // 立即关闭所有资源
            if (serverChannel != null) {
                serverChannel.close().syncUninterruptibly();
            }
            channelGroup.close().syncUninterruptibly();
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        } finally {
            cleanResources();
        }
    }

    private void cleanResources() {
        serverChannel = null;
        if (bossGroup != null) {
            bossGroup = null;
        }
        if (workerGroup != null) {
            workerGroup = null;
        }
    }


}


