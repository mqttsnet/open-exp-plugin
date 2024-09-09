package com.mqttsnet.thinglinks.open.exp.example.tcpserver;

import cn.hutool.core.net.NetUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 负责管理TcpServer的生命周期，并执行定时任务。
 *
 * @author mqttsnet
 */
@Slf4j
@Component
public class MyComponent {

    private final ScheduledExecutorService tcpServerExecutor = new ScheduledThreadPoolExecutor(10);
    private final ScheduledExecutorService scheduledTaskExecutor = new ScheduledThreadPoolExecutor(10);

    private TcpServer tcpServer = null;

    /**
     * 初始化组件并启动TcpServer。
     */
    @PostConstruct
    public void init() {
        String tcpPort = Boot.tcpPort.getDefaultValue();
        log.info("启动 TcpServer 插件...");

        // 使用单独的线程池来启动TcpServer
        tcpServerExecutor.submit(() -> {
            try {
                if (tcpServer == null) {
                    tcpServer = new TcpServer();
                }
                int port = Integer.parseInt(tcpPort);  // 确保端口号可以正确解析
                log.info("尝试在端口 {} 上启动 TcpServer", port);
                tcpServer.start(port);
                log.info("TcpServer 在端口 {} 上启动成功", port);
            } catch (NumberFormatException e) {
                log.error("端口号格式错误: {}，请检查配置", tcpPort, e);
            } catch (Exception e) {
                log.error("启动 TcpServer 时发生异常", e);
                throw new RuntimeException("TcpServer 启动失败", e);
            }
        });

        // 使用单独的线程池来执行定时任务
        scheduledTaskExecutor.scheduleAtFixedRate(() -> {
            try {
                log.info("Preparing to send heartbeat at {} with IP: {}", LocalDateTime.now(), NetUtil.getLocalhostStr());

                // 发送心跳 到 插件服务器
//                pluginServer.heartbeat(ip, port, applicationName);

                log.info("Heartbeat sent successfully at {}", LocalDateTime.now());
            } catch (Exception e) {
                log.error("Failed to send heartbeat at {}", LocalDateTime.now(), e);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 在应用关闭时优雅地关闭TcpServer和定时任务。
     */
    @PreDestroy
    public void shutdown() {
        log.info("关闭 TcpServer 插件...");

        // 优雅关闭 TcpServer
        if (tcpServer != null) {
            tcpServer.shutdown();
            log.info("TcpServer 已成功关闭");
        }

        // 优雅关闭定时任务线程池
        if (!scheduledTaskExecutor.isShutdown()) {
            scheduledTaskExecutor.shutdown();
            try {
                if (!scheduledTaskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("定时任务未在5秒内完成，强制关闭...");
                    scheduledTaskExecutor.shutdownNow();
                } else {
                    log.info("定时任务已成功关闭");
                }
            } catch (InterruptedException e) {
                log.error("关闭定时任务时发生异常", e);
                Thread.currentThread().interrupt();
            }
        }

        // 关闭 TcpServer 线程池
        if (!tcpServerExecutor.isShutdown()) {
            tcpServerExecutor.shutdown();
            try {
                if (!tcpServerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("TcpServer 线程未在5秒内完成，强制关闭...");
                    tcpServerExecutor.shutdownNow();
                } else {
                    log.info("TcpServer 线程已成功关闭");
                }
            } catch (InterruptedException e) {
                log.error("关闭 TcpServer 线程池时发生异常", e);
                Thread.currentThread().interrupt();
            }
        }
    }

}


