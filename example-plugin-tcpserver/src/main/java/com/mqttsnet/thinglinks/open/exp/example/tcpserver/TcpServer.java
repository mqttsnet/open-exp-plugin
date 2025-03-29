package com.mqttsnet.thinglinks.open.exp.example.tcpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.mqttsnet.thinglinks.open.exp.example.tcpserver.initializer.MyServerInitializer;
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
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * TCP服务器，使用Netty框架实现，支持高并发和高性能。
 *
 * @author xiaonannet
 */
@Slf4j
@Component
public class TcpServer {

    // 线程组配置
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    // 主服务通道
    private Channel serverChannel;
    // 客户端连接管理组
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 服务端口（从配置读取）
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

            // 阶段2：关闭客户端连接
            closeClientConnections();

            // 阶段3：关闭线程池
            shutdownEventLoopGroups();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("关闭流程被中断，启动强制关闭");
            shutdownNow();
        } finally {
            cleanResources();
            boolean portAvailable = canBindPort(port);
            String status = portAvailable ? "已释放" : "仍被占用";
            if (portAvailable) {
                log.info("TCP服务终止成功 | 端口:{} 可用 | 状态:{}", port, status);
            } else {
                log.warn("TCP服务端口释放异常 | 端口:{} 不可用 | 状态:{} | 端口完全释放一般延迟1分钟左右 | 建议执行命令查询端口最终释放情况: netstat -an | grep {}", port, status, port);
            }
        }
    }

    private void closeMainChannel() throws InterruptedException {
        if (serverChannel != null && serverChannel.isOpen()) {
            log.info("关闭主监听通道...");
            serverChannel.close();
        }
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

    /**
     * 关闭事件循环组
     * 1. 增加关闭状态追踪
     * 2. 参数优化（静默期0ms+超时1000ms）
     * 3. 增加关闭进度日志
     * 4. 性能耗时监控
     */
    private void shutdownEventLoopGroups() {
        final long start = System.currentTimeMillis();
        log.info("启动线程池关闭流程");

        // BossGroup关闭流程
        if (bossGroup != null && !bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully(0, 1000, TimeUnit.MILLISECONDS)
                    .addListener(f -> log.debug("BossGroup关闭状态: {}", f.isSuccess()))
                    .awaitUninterruptibly(50, TimeUnit.MILLISECONDS);
        }

        // WorkerGroup关闭流程
        if (workerGroup != null && !workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully(0, 1000, TimeUnit.MILLISECONDS)
                    .addListener(f -> log.debug("WorkerGroup关闭状态: {}", f.isSuccess()))
                    .awaitUninterruptibly(100, TimeUnit.MILLISECONDS);
        }

        log.info("线程池关闭耗时: {}ms", System.currentTimeMillis() - start);
    }

    /**
     * 强制立即关闭方法（应急处理）
     * 1. 强制释放端口
     * 2. 带超时的通道关闭
     * 3. 显式内存释放
     *
     * @apiNote 该方法会跳过正常关闭流程，可能造成资源未完全释放
     */
    public void shutdownNow() {
        log.warn("进入强制关闭模式");
        try {
            // 暴力关闭客户端连接（带重试机制）
            int retryCount = 0;
            while (!channelGroup.isEmpty() && retryCount++ < 3) {
                channelGroup.forEach(ch -> {
                    if (ch.isActive()) {
                        ch.unsafe().closeForcibly(); // 强制关闭连接
                    }
                });
                Thread.sleep(50); // 等待系统处理时间
            }

            // 带超时的通道关闭（最多等待100ms）
            if (serverChannel != null) {
                serverChannel.close().awaitUninterruptibly(500, TimeUnit.MILLISECONDS);
            }

            // 执行快速线程池关闭
            shutdownEventLoopGroups();

            // 显式内存释放（针对Netty的堆外内存）
            if (PlatformDependent.hasUnsafe()) {
                // 触发内存清理
                PlatformDependent.freeDirectNoCleaner(ByteBuffer.allocate(1));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            cleanResources();
            log.info("强制关闭完成");
        }
    }

    /**
     * 资源清理方法
     * 1. 增加线程池状态二次检查
     * 2. 终极关闭模式（参数0,0立即终止）
     * 3. 显式置空资源引用
     */
    private void cleanResources() {
        try {
            // BossGroup补偿关闭
            if (bossGroup != null && !bossGroup.isTerminated()) {
                log.warn("BossGroup未完全关闭，执行补偿关闭");
                // 立即终止、使用新实例避免空指针
                EventLoopGroup bg = bossGroup;
                bg.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
            }

            // WorkerGroup补偿关闭
            if (workerGroup != null && !workerGroup.isTerminated()) {
                EventLoopGroup wg = workerGroup;
                wg.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
            }
        } finally {
            new Thread(() -> {
                try {
                    // 等待补偿关闭完成
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                // 释放资源引用
                serverChannel = null;
                bossGroup = null;
                workerGroup = null;
            }).start();
        }
    }


    /**
     * 端口是否可用检测
     *
     * @param port 端口号
     * @return {@link boolean} true:可用、false:不可用
     */
    private boolean canBindPort(int port) {
        try (ServerSocket ss = new ServerSocket()) {
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(port));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}

