package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.net.NetUtil;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.publisher.MqttEventPublisher;
import io.netty.handler.codec.mqtt.MqttQoS;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

/**
 * 组件管理总线
 * <p>
 * 核心职责：
 * 1. 统一管理TCP服务端与MQTT客户端生命周期
 * 2. 实现TCP与MQTT协议间的双向通信桥接
 * 3. 维护系统稳定性（异常恢复/资源回收）
 *
 * @author mqttsnet
 * @version 1.1
 */

@Getter
@Slf4j
@Component
public class MyComponent {
    /**
     * TCP服务守护线程池（daemon模式防止JVM无法退出）
     */
    private final ScheduledExecutorService tcpServerExecutor =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread t = new Thread(r, "TCP-Server-Thread");
                t.setDaemon(true);
                return t;
            });

    /**
     * MQTT心跳守护线程池（定时发送存活信号）
     */
    private final ScheduledExecutorService mqttHeartbeatExecutor =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread t = new Thread(r, "MQTT-Heartbeat-Thread");
                t.setDaemon(true);
                return t;
            });

    private final TcpServer tcpServer;
    private final MqttEventPublisher mqttEventPublisher;
    private MqttClient mqttClient;


    /**
     * 组件构造器（Spring依赖注入）
     *
     * @param tcpServer          TCP服务实例
     * @param mqttEventPublisher MQTT事件发布器
     */
    public MyComponent(TcpServer tcpServer, MqttEventPublisher mqttEventPublisher) {
        this.tcpServer = tcpServer;
        this.mqttEventPublisher = mqttEventPublisher;
    }


    /**
     * 组件初始化入口（Spring生命周期回调）
     * <p>执行顺序：<br>
     * 1. 启动TCP监听服务<br>
     * 2. 建立MQTT Broker连接<br>
     * 3. 初始化心跳监测机制</p>
     */
    @PostConstruct
    public synchronized void initComponent() {
        log.info("[组件初始化] 启动TcpToMqtt插件");
        if (isMqttConnected()) {
            log.warn("[组件初始化] 检测到已连接的MQTT客户端，跳过重复初始化");
            return;
        }

        startTcpServerWithRetry();

        connectToMqttBroker();

        scheduleHeartbeatTask();
    }

    /**
     * 组件销毁入口（Spring生命周期回调）
     * <p>执行顺序：<br>
     * 1. 停止心跳任务<br>
     * 2. 断开MQTT连接<br>
     * 3. 关闭TCP服务<br>
     * 4. 回收线程资源</p>
     */
    @PreDestroy
    public synchronized void shutdownComponent() {
        log.info("[组件关闭] 开始释放TcpToMqtt插件资源");
        stopHeartbeatService();
        cleanupExistingMqttConnection();
        shutdownTcpServer();
        cleanupThreadPools();
        log.info("[组件关闭] 资源回收完成");
    }

    /**
     * 启动TCP服务（带异常重试机制）
     * <p>在守护线程中启动服务，失败时触发紧急关闭流程</p>
     */
    private void startTcpServerWithRetry() {
        tcpServerExecutor.submit(() -> {
            try {
                log.info("[TCP服务] 正在启动...");
                tcpServer.start();
                log.info("[TCP服务] 启动成功");
            } catch (Exception e) {
                log.error("[TCP服务] 启动失败 触发紧急关闭 | 错误详情：{}", e.getMessage());
                shutdownComponent();
            }
        });
    }


    /**
     * 连接MQTT Broker（带旧连接清理）
     * <p>执行步骤：<br>
     * 1. 清理残留连接<br>
     * 2. 加载配置参数<br>
     * 3. 初始化客户端实例<br>
     * 4. 建立网络连接<br>
     * 5. 订阅目标主题</p>
     */
    private void connectToMqttBroker() {
        try {
            if (isMqttConnected()) {
                log.warn("[MQTT连接] 检测到已连接的MQTT客户端，正在清理旧连接...");
                mqttClient.disconnect();
            }


            // 获取默认配置参数
            String brokerServerUrlDefaultValue = Boot.mqttBrokerServerUrl.getDefaultValue();
            String clientId = Boot.mqttClientClientId.getDefaultValue();
            String username = Boot.mqttClientUsername.getDefaultValue();
            String password = Boot.mqttClientPassword.getDefaultValue();
            String mqttTopicDefaultValue = Boot.mqttClientCommandTopic.getDefaultValue();
            MqttQoS mqttQoS = MqttQoS.EXACTLY_ONCE;

            log.info("尝试连接MQTT服务器 {} | ClientID: {}| username: {}| password: {}", brokerServerUrlDefaultValue, clientId, username, password);

            // 创建 MQTT 客户端
            mqttClient = new MqttClient(
                    brokerServerUrlDefaultValue,
                    clientId,
                    new MemoryPersistence(),
                    Executors.newScheduledThreadPool(10)
            );


            // 设置连接参数
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            options.setCleanSession(false);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(30);
            log.info("MQTT客户端初始化完成，准备连接...|连接参数: {}", options);

            // 添加回调函数，处理消息发送和接收
            mqttClient.setCallback(createMqttCallback());

            // 连接到 MQTT 服务器
            mqttClient.connect(options);
            log.info("MQTT服务器连接状态: {}", mqttClient.isConnected() ? "成功" : "失败");

            // 订阅主题
            mqttClient.subscribe(mqttTopicDefaultValue, mqttQoS.value());
            log.info("[MQTT订阅] 成功 | 主题:{} QoS:{} 线程:{}", mqttTopicDefaultValue, mqttQoS.value(), Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("[MQTT连接] 初始化失败 | 错误详情：{}", e.getMessage());
            shutdownComponent();
        }
    }


    /**
     * 创建MQTT回调处理器
     *
     * @return 配置完成的回调处理器实例
     */
    private MqttCallback createMqttCallback() {
        return new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // 连接断开时触发（网络故障/服务端主动断开）
                log.warn("MQTT连接丢失: {},断开详情:", cause.getMessage(), cause);
                // 建议增加重连逻辑（示例）：
                if (!mqttClient.isConnected()) {
                    log.info("尝试自动重连...");
                    try {
                        mqttClient.reconnect();
                    } catch (MqttException e) {
                        log.error("重连失败: {}", e.getMessage());
                    }
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // 收到消息时触发（核心业务入口）
                try {
                    mqttEventPublisher.publishMqttMessageReceiveEvent(topic, message.getPayload(), message.getQos());
                } catch (Exception e) {
                    log.error("消息处理异常 [Topic:{}]: {}", topic, e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 消息成功投递到broker时触发（QoS>=1）
                try {
                    log.info("消息投递完成 [MessageId:{} Topics:{}]", token.getMessageId(), token.getTopics());
                    mqttEventPublisher.publishMqttMessageSendEvent(token.getMessageId(), token.getTopics());
                } catch (Exception e) {
                    log.warn("投递状态获取失败: {}", e.getMessage());
                }
            }
        };
    }

    /**
     * 关闭TCP服务
     */
    private void shutdownTcpServer() {
        try {
            log.info("[TCP服务] 正在关闭...");
            tcpServer.shutdown();
            log.info("[TCP服务] 已正常关闭");
        } catch (Exception e) {
            log.error("[TCP服务] 关闭异常 | 错误详情：{}", e.getMessage());
        }
    }

    /**
     * 清理现有MQTT连接
     * <p>断开旧连接并释放资源</p>
     */
    private void cleanupExistingMqttConnection() {
        if (mqttClient != null) {
            try {
                log.warn("[连接管理] 发现旧连接，正在清理...");
                mqttClient.disconnect();
                mqttClient.close();
            } catch (MqttException e) {
                log.error("[连接管理] 旧连接清理失败 错误: {}", e.getMessage());
            } finally {
                mqttClient = null;
            }
        }
    }

    /**
     * 停止心跳服务
     * <p>立即终止心跳线程并回收资源</p>
     */
    private void stopHeartbeatService() {
        log.info("[心跳服务] 正在停止...");
        shutdownExecutor(mqttHeartbeatExecutor, "MQTT心跳线程池");
    }

    /**
     * 清理线程池资源
     * <p>关闭所有守护线程池并释放资源</p>
     */
    private void cleanupThreadPools() {
        log.info("[资源回收] 开始清理线程池");
        shutdownExecutor(tcpServerExecutor, "TCP服务线程池");
        shutdownExecutor(mqttHeartbeatExecutor, "MQTT心跳线程池");
    }

    /**
     * 安全关闭线程池
     */
    private void shutdownExecutor(ScheduledExecutorService executor, String poolName) {
        if (executor != null && !executor.isShutdown()) {
            List<Runnable> tasks = executor.shutdownNow();
            log.info("终止{}中的{}个任务", poolName, tasks.size());

            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    log.warn("{}未能及时终止，可能存在资源泄漏", poolName);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 检查MQTT连接状态
     *
     * @return true表示已连接，false表示未连接
     */
    private boolean isMqttConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }


    /**
     * 调度心跳任务
     * <p>以固定间隔发送存活信号，间隔30秒</p>
     */
    private void scheduleHeartbeatTask() {
        log.info("[心跳服务] 启动定时任务 间隔: 30秒");
        mqttHeartbeatExecutor.scheduleAtFixedRate(this::sendHeartbeat, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 发送 MQTT 心跳
     */
    private void sendHeartbeat() {
        try {
            log.info("example-plugin-tcptomqtt Heartbeat sent successfully at at {} with IP: {}", LocalDateTime.now(), NetUtil.getLocalhostStr());

            // TODO 发送心跳 到 插件服务器
//            sendMqttHeartbeat(Boot.mqttDeviceIdentification.getDefaultValue());

        } catch (Exception e) {
            log.error("Failed to send heartbeat at {}", LocalDateTime.now(), e);
        }
    }

}



