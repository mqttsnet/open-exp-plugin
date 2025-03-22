package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.net.NetUtil;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.DefaultMqttClientFactory;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttClient;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttClientFactory;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback.MqttCallback;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback.MqttReceiveCallbackResult;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback.MqttSendCallbackResult;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttVersion;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.publisher.MqttEventPublisher;
import io.netty.handler.codec.mqtt.MqttQoS;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 组件管理总线
 *
 * @author mqttsnet
 */

@Getter
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

    private final ScheduledExecutorService mqttHeartbeatExecutor =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread t = new Thread(r, "MQTT-Heartbeat-Thread");
                t.setDaemon(true);
                return t;
            });

    private static final MqttClientFactory MQTT_CLIENT_FACTORY = new DefaultMqttClientFactory();
    private final TcpServer tcpServer;
    private final MqttEventPublisher mqttEventPublisher;
    private volatile MqttClient mqttClient;

    private static boolean initialized = false;

    public MyComponent(TcpServer tcpServer, MqttEventPublisher mqttEventPublisher) {
        this.tcpServer = tcpServer;
        this.mqttEventPublisher = mqttEventPublisher;
    }

    @PostConstruct
    public synchronized void init() {
        log.info("初始化 TcpToMqtt 插件...");
        if (initialized) {
            log.warn("重复初始化 TcpToMqtt 插件已被阻止");
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

        // 启动MQTT客户端（带连接重试）
        startMqttClient();

        // 定时心跳任务（30秒间隔）
        mqttHeartbeatExecutor.scheduleAtFixedRate(
                this::sendHeartbeat, 30, 30, TimeUnit.SECONDS
        );
    }

    /**
     * 安全关闭入口（严格顺序执行）
     */
    @PreDestroy
    public synchronized void shutdown() {
        log.info("开始关闭 TcpToMqtt 插件...");

        // 阶段1：停止所有定时任务
        shutdownExecutor(mqttHeartbeatExecutor, "TcpToMqtt 插件心跳线程池");

        // 阶段2：关闭MQTT客户端（必须先于TCP服务）
        shutdownMqttClient();

        // 阶段3：关闭TCP服务
        tcpServer.shutdown();

        // 阶段4：关闭TCP服务线程池
        shutdownExecutor(tcpServerExecutor, "TCP服务线程池");

        // 阶段5：最后关闭工厂（所有资源已释放）
        MQTT_CLIENT_FACTORY.close();
        log.info("所有资源已释放完毕");
    }

    /**
     * 启动MQTT客户端（带旧连接清理）
     */
    private void startMqttClient() {
        try {
            // 清理旧连接（防止重复连接）
            if (mqttClient != null) {
                shutdownMqttClient();
            }

            // 获取默认配置参数
            String brokerHostDefaultValue = Boot.mqttBrokerHost.getDefaultValue();
            String brokerPortDefaultValue = Boot.mqttBrokerPort.getDefaultValue();
            String clientId = Boot.mqttClientClientId.getDefaultValue();
            String username = Boot.mqttClientUsername.getDefaultValue();
            String password = Boot.mqttClientPassword.getDefaultValue();
            String mqttTopicDefaultValue = Boot.mqttClientCommandTopic.getDefaultValue();
            MqttQoS mqttQoS = MqttQoS.EXACTLY_ONCE;

            log.info("尝试连接MQTT服务器 {}:{} | ClientID: {}| username: {}| password: {}", brokerHostDefaultValue, brokerPortDefaultValue, clientId, username, password);

            // 设置连接参数
            MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
            mqttConnectParameter.setHost(brokerHostDefaultValue);
            mqttConnectParameter.setPort(Integer.parseInt(brokerPortDefaultValue));
            mqttConnectParameter.setUsername(username);
            mqttConnectParameter.setPassword(password);
            mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
            mqttConnectParameter.setKeepAliveTimeSeconds(120);  // 设置心跳间隔为 120 秒
            mqttConnectParameter.setConnectTimeoutSeconds(30);  // 设置连接超时为 30 秒
            mqttConnectParameter.setAutoReconnect(true);

            log.info("MQTT客户端初始化完成，准备连接...|连接参数: {}", mqttConnectParameter);
            // 创建 MQTT 客户端
            mqttClient = MQTT_CLIENT_FACTORY.createMqttClient(mqttConnectParameter);

            // 添加回调函数，处理消息发送和接收
            mqttClient.addMqttCallback(new MqttCallback() {
                @Override
                public void messageSendCallback(MqttSendCallbackResult result) {
                    mqttEventPublisher.publishMqttMessageSendEvent(
                            result.getTopic(), result.getPayload(), result.getQos()
                    );
                }

                @Override
                public void messageReceiveCallback(MqttReceiveCallbackResult result) {
                    mqttEventPublisher.publishMqttMessageReceiveEvent(
                            result.getTopic(), result.getPayload(), result.getQos()
                    );
                }
            });

            // 连接到 MQTT 服务器
            mqttClient.connect();
            log.info("MQTT服务器连接状态: {}", mqttClient.isActive() ? "成功" : "失败");

            // 订阅主题
            mqttClient.subscribe(mqttTopicDefaultValue, mqttQoS);
            log.info("已成功订阅主题: {}", mqttTopicDefaultValue);
            // 在这里不再需要阻塞线程，客户端会通过回调不断接收消息并处理
        } catch (Exception e) {
            log.error("MQTT客户端启动失败", e);
            shutdown(); // 启动失败触发整体关闭
        }
    }

    /**
     * 分阶段关闭MQTT客户端
     */
    private void shutdownMqttClient() {
        if (mqttClient != null) {
            try {
                // 阶段1：检查连接状态（使用正确的方法名）
                if (mqttClient.isActive()) {
                    // 优雅断开（保持原始API调用）
                    mqttClient.disconnectFuture().addListener(future -> {
                        if (!future.isSuccess()) {
                            log.warn("MQTT优雅断开失败，尝试强制关闭");
                        }
                    });
                }

                // 阶段2：强制关闭
                mqttClient.close();

                log.info("MQTT客户端已关闭");
            } catch (Exception e) {
                log.error("MQTT关闭异常", e);
            } finally {
                mqttClient = null;
            }
        }
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
     * 发送 MQTT 心跳
     */
    private void sendHeartbeat() {
        try {
            log.info("example-plugin-tcptomqtt Heartbeat sent successfully at at {} with IP: {}", LocalDateTime.now(), NetUtil.getLocalhostStr());

            // 发送心跳 到 插件服务器
//            sendMqttHeartbeat(Boot.mqttDeviceIdentification.getDefaultValue());

        } catch (Exception e) {
            log.error("Failed to send heartbeat at {}", LocalDateTime.now(), e);
        }
    }

    /**
     * 发送 MQTT 心跳消息
     */
    /*private void sendMqttHeartbeat(String deviceIdentification) throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            String heartbeatMessage = "{\"status\":\"ONLINE\"}";
            MqttMessage message = new MqttMessage(heartbeatMessage.getBytes());
            mqttClient.publish("/v1/devices/" + deviceIdentification + "/status", message);
            log.info("MQTT Heartbeat sent successfully");
        } else {
            log.warn("MQTT client is not connected, cannot send heartbeat");
        }
    }*/

}



