package com.mqttsnet.thinglinks.open.exp.example.tcpserver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.net.NetUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 组件管理总线
 *
 * @author mqttsnet
 */
@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MyComponent {

    // 使用守护线程池（防止JVM无法退出）
    private final ScheduledExecutorService tcpServerExecutor =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread t = new Thread(r, "TCP-Server-Thread");
                t.setDaemon(true);
                return t;
            });

    private final ScheduledExecutorService scheduledTaskExecutor =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread t = new Thread(r, "Heartbeat-Thread");
                t.setDaemon(true);
                return t;
            });

    private static boolean initialized = false;
    private final TcpServer tcpServer;

    public MyComponent(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
    }


    /**
     * 初始化组件并启动TcpServer。
     */
    @PostConstruct
    public void init() {
        log.info("初始化 tcpserver 插件...");
        if (initialized) {
            log.warn("重复初始化 tcpserver 插件已被阻止");
            return;
        }
        initialized = true;

        // 启动TCP服务（带异常重试机制）
        tcpServerExecutor.submit(() -> {
            try {
                tcpServer.start();
            } catch (Exception e) {
                log.error("TCP服务启动失败，触发紧急关闭", e);
                shutdown(); // 启动失败时立即清理
            }
        });

        // 定时心跳任务（30秒间隔）
        scheduledTaskExecutor.scheduleAtFixedRate(
                this::sendHeartbeat, 30, 30, TimeUnit.SECONDS
        );
    }

    /**
     * 在应用关闭时优雅地关闭TcpServer和定时任务。
     */
    @PreDestroy
    public void shutdown() {
        log.info("开始关闭 tcpserver 插件...");

        // 阶段1：停止所有定时任务
        shutdownExecutor(scheduledTaskExecutor, "tcpserver 插件心跳线程池");

        // 阶段2：关闭TCP服务
        tcpServer.shutdown();

        // 阶段3：关闭TCP服务线程池（所有资源已释放）
        shutdownExecutor(tcpServerExecutor, "TCP服务线程池");

        log.info("所有资源已释放完毕");
    }

    /**
     * 安全关闭线程池
     */
    private void shutdownExecutor(ScheduledExecutorService executor, String poolName) {
        if (executor != null && !executor.isShutdown()) {
            List<Runnable> tasks = executor.shutdownNow();
            log.info("终止{}中的{}个任务", poolName, tasks.size());

            try {
                if (!executor.awaitTermination(0, TimeUnit.SECONDS)) {
                    log.warn("{}未能及时终止，可能存在资源泄漏", poolName);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * 发送 tcpserver 心跳
     */
    private void sendHeartbeat() {
        try {
            log.info("example-plugin-tcpserver Heartbeat sent successfully at at {} with IP: {}", LocalDateTime.now(), NetUtil.getLocalhostStr());

            // 发送心跳 到 插件服务器
//            sendMqttHeartbeat(Boot.mqttDeviceIdentification.getDefaultValue());

        } catch (Exception e) {
            log.error("Failed to send heartbeat at {}", LocalDateTime.now(), e);
        }
    }

}


