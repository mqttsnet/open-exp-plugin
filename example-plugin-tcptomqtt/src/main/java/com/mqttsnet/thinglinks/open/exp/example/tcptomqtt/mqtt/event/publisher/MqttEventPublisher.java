package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.publisher;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttConnectionStatusEvent;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttMessageReceiveEvent;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttMessageSendEvent;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttPublishMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * -----------------------------------------------------------------------------
 * File Name: MqttEventPublisher
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * Mqtt 事件发布器
 * -----------------------------------------------------------------------------
 *
 * @author xiaonannet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2025/1/22       xiaonannet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2025/1/22 16:18
 */
@Component
@Slf4j
public class MqttEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public MqttEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    /**
     * 发布消息接收事件
     *
     * @param topic   消息主题
     * @param payload 消息内容
     * @param qos     服务质量
     */
    public void publishMqttMessageReceiveEvent(String topic, byte[] payload, int qos) {
        eventPublisher.publishEvent(new MqttMessageReceiveEvent(this, topic, payload, qos));
        log.info("已发布消息接收事件 [Topic:{}]", topic);
    }

    /**
     * 发布消息发送成功事件
     *
     * @param messageId 消息ID
     * @param topics    成功投递的主题数组
     */
    public void publishMqttMessageSendEvent(int messageId, String[] topics) {
        eventPublisher.publishEvent(new MqttMessageSendEvent(this, messageId, topics));
        log.info("已发布消息发送事件 [MsgID:{}]", messageId);
    }

    /**
     * 发布消息发布请求事件
     *
     * @param topic   目标主题
     * @param payload 消息内容
     * @param qos     服务质量
     * @param retain  保留标志
     */
    public void publishMqttPublishMessageEvent(String topic, byte[] payload, int qos, boolean retain) {
        eventPublisher.publishEvent(new MqttPublishMessageEvent(this, topic, payload, qos, retain));
        log.info("已发布消息请求事件 [Topic:{}]", topic);
    }

    /**
     * 发布连接状态事件
     *
     * @param connected 是否已连接
     * @param serverUrl 服务器地址
     */
    public void publishConnectionStatusEvent(boolean connected, String serverUrl) {
        eventPublisher.publishEvent(new MqttConnectionStatusEvent(this, connected, serverUrl));
        log.info("已发布连接状态事件 [状态:{}]", connected ? "已连接" : "已断开");
    }

    /**
     * 发布连接状态事件（增强版）
     *
     * @param connected 连接状态
     * @param serverUrl 服务器地址
     * @param cause     连接异常原因（断开时必传）
     */
    public void publishConnectionStatusEvent(boolean connected, String serverUrl, @Nullable Throwable cause) {
        MqttConnectionStatusEvent event = new MqttConnectionStatusEvent(this, connected, serverUrl, cause);
        eventPublisher.publishEvent(event);
        if (connected) {
            log.info("连接状态变更 [成功] => {}", serverUrl);
        } else {
            String reason = cause != null ? cause.getMessage() : "主动断开";
            log.warn("连接状态变更 [断开] => {} 原因: {}", serverUrl, reason);
        }
    }

}
