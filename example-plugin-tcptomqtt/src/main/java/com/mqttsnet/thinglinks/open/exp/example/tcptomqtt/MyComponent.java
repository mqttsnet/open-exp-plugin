package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import io.netty.handler.codec.mqtt.MqttQoS;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 组件管理总线
 */

@Slf4j
@Component
public class MyComponent {

    private final ScheduledExecutorService tcpServerExecutor = new ScheduledThreadPoolExecutor(10);
    private final ScheduledExecutorService scheduledTaskExecutor = new ScheduledThreadPoolExecutor(10);

    private static final MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();

    private TcpServer tcpServer = null;

    @PostConstruct
    public void init() {
        log.info("启动 TcpToMqttServer 插件...");

        // 启动 TcpServer
        startTcpServer();

        // 启动 MQTT 客户端
        startMqttClient();

        // 使用定时任务执行心跳功能
        scheduledTaskExecutor.scheduleAtFixedRate(this::sendHeartbeat, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 启动 TcpServer
     */
    private void startTcpServer() {
        String tcpPort = Boot.tcpPort.getDefaultValue();

        // 使用线程池启动 TcpServer
        tcpServerExecutor.submit(() -> {
            try {
                if (tcpServer == null) {
                    tcpServer = new TcpServer();
                }
                int port = Integer.parseInt(tcpPort);
                log.info("尝试在端口 {} 上启动 TcpToMqttServer", port);
                tcpServer.start(port);
                log.info("TcpToMqttServer 在端口 {} 上启动成功", port);
            } catch (NumberFormatException e) {
                log.error("端口号格式错误: {}，请检查配置", tcpPort, e);
            } catch (Exception e) {
                log.error("启动 TcpServer 时发生异常", e);
                throw new RuntimeException("TcpToMqttServer 启动失败", e);
            }
        });
    }

    /**
     * 启动并连接 MQTT 客户端
     */
    private void startMqttClient() {
        try {
            // 获取默认配置参数
            String brokerHostDefaultValue = Boot.mqttBrokerHost.getDefaultValue();
            String brokerPortDefaultValue = Boot.mqttBrokerPort.getDefaultValue();
            String clientId = Boot.mqttClientClientId.getDefaultValue();
            String username = Boot.mqttClientUsername.getDefaultValue();
            String password = Boot.mqttClientPassword.getDefaultValue();
            String mqttTopicDefaultValue = Boot.mqttClientTopic.getDefaultValue();
            byte[] payload = "ThingLinks".getBytes(StandardCharsets.UTF_8);
            MqttQoS mqttQoS = MqttQoS.EXACTLY_ONCE;

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

            // 创建 MQTT 客户端
            MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);

            // 添加回调函数，处理消息发送和接收
            mqttClient.addMqttCallback(new MqttCallback() {
                @Override
                public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
                    String topic = mqttSendCallbackResult.getTopic();
                    MqttQoS qos = mqttSendCallbackResult.getQos();
                    byte[] payload = mqttSendCallbackResult.getPayload();

                    // 判断发送的消息是否匹配指定的条件
                    if (Arrays.equals(payload, payload) && mqttTopicDefaultValue.equals(topic) && mqttQoS.equals(qos)) {
                        log.info("消息发送成功！");
                    }
                }

                @Override
                public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                    // 获取接收到的消息的相关信息
                    byte[] payload = receiveCallbackResult.getPayload();
                    String topic = receiveCallbackResult.getTopic();
                    MqttQoS qos = receiveCallbackResult.getQos();

                    // 输出接收到的消息
                    log.info("接收到消息：Topic = {}, QoS = {}, Payload = {}", topic, qos, new String(payload));

                    // 判断接收到的消息是否符合预期条件
                    if (mqttTopicDefaultValue.equals(topic) && mqttQoS.equals(qos)) {
                        // 处理接收到的消息
                        handleReceivedMessage(payload);
                    }
                }
            });

            // 连接到 MQTT 服务器
            mqttClient.connect();

            // 订阅指定的 topic
            mqttClient.subscribe(mqttTopicDefaultValue, mqttQoS);
            log.info("订阅成功：Topic = {}, QoS = {}", mqttTopicDefaultValue, mqttQoS);

            // 发布消息
            mqttClient.publish(payload, mqttTopicDefaultValue, mqttQoS);
            log.info("消息发送：Topic = {}, Payload = {}", mqttTopicDefaultValue, new String(payload));

            // 在这里不再需要阻塞线程，客户端会通过回调不断接收消息并处理
        } catch (Exception e) {
            log.error("发生异常：连接 MQTT 服务器失败: " + e.getMessage(), e);
        }
    }


    // 处理接收到的消息
    private void handleReceivedMessage(byte[] payload) {
        // 在这里可以添加自定义的消息处理逻辑
        String receivedMessage = new String(payload, StandardCharsets.UTF_8);
        log.info("处理接收到的消息: {}", receivedMessage);

        // 可以根据消息内容进行进一步的业务处理
        // 比如解析 JSON 数据，更新 UI，或者触发其他操作
    }


    /**
     * 发送 MQTT 心跳
     */
    private void sendHeartbeat() {
        try {
            log.info("Preparing to send heartbeat at {} with IP: {}", LocalDateTime.now(), NetUtil.getLocalhostStr());

            // 发送心跳 到 插件服务器
//            sendMqttHeartbeat(Boot.mqttDeviceIdentification.getDefaultValue());

            log.info("Heartbeat sent successfully at {}", LocalDateTime.now());
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

    /**
     * 在应用关闭时优雅地关闭TcpServer和定时任务。
     */
    @PreDestroy
    public void shutdown() {
        log.info("关闭 TcpToMqttServer 插件...");

        // 优雅关闭 TcpServer
        if (tcpServer != null) {
            tcpServer.shutdown();
            log.info("TcpToMqttServer 已成功关闭");
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
                    log.warn("TcpToMqttServer 线程未在5秒内完成，强制关闭...");
                    tcpServerExecutor.shutdownNow();
                } else {
                    log.info("TcpToMqttServer 线程已成功关闭");
                }
            } catch (InterruptedException e) {
                log.error("关闭 TcpToMqttServer 线程池时发生异常", e);
                Thread.currentThread().interrupt();
            }
        }

        // 关闭 MQTT 客户端
        mqttClientFactory.close();
        log.info("MQTT client disconnected.");
    }


}



