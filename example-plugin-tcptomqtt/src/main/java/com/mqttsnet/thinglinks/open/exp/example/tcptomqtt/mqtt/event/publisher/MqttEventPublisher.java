package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.publisher;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttMessageReceiveEvent;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttMessageSendEvent;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttPublishMessageEvent;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
     * 发布 MQTT MESSAGE 事件
     *
     * @param topic   topic
     * @param payload payload
     * @param qos     QoS
     * @param retain  retain
     */
    public void publishMqttMessageEvent(String topic, byte[] payload, MqttQoS qos, boolean retain) {
        eventPublisher.publishEvent(new MqttPublishMessageEvent(this, topic, payload, qos, retain));
    }

    public void publishMqttMessageSendEvent(String topic, byte[] payload, MqttQoS qos) {
        MqttMessageSendEvent event = new MqttMessageSendEvent(this, topic, payload, qos);
        eventPublisher.publishEvent(event);
    }

    public void publishMqttMessageReceiveEvent(String topic, byte[] payload, MqttQoS qos) {
        MqttMessageReceiveEvent event = new MqttMessageReceiveEvent(this, topic, payload, qos);
        eventPublisher.publishEvent(event);
    }

}
