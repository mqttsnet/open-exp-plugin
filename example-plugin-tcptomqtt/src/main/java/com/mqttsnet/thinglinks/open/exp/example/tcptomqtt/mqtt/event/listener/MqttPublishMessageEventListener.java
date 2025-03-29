package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.listener;

import java.nio.charset.StandardCharsets;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.MyComponent;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttPublishMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * -----------------------------------------------------------------------------
 * File Name: MqttMessageEventListener
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * MQTT Publish 消息事件监听器
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
 * @date 2025/1/22 16:16
 */
@Slf4j
@Component
@Order(1)
public class MqttPublishMessageEventListener implements ApplicationListener<MqttPublishMessageEvent> {

    private final MyComponent myComponent;

    public MqttPublishMessageEventListener(MyComponent myComponent) {
        this.myComponent = myComponent;
    }

    @Async
    @Override
    public void onApplicationEvent(MqttPublishMessageEvent event) {
        log.info("Handling event: topic={}, payload={}, qos={}, retain={}", event.getTopic(), new String(event.getPayload(), StandardCharsets.UTF_8), event.getQos(), event.isRetain());
        if (null == myComponent.getMqttClient()) {
            log.warn("MqttClient is not initialized, cannot publish message.");
            return;
        }
        MqttMessage mqttMessage = new MqttMessage(event.getPayload());
        mqttMessage.setQos(event.getQos());
        mqttMessage.setRetained(event.isRetain());
        try {
            myComponent.getMqttClient().publish(event.getTopic(), mqttMessage);
        } catch (MqttException e) {
            log.error("Failed to publish message to topic: {}", event.getTopic(), e);
            throw new RuntimeException(e);
        }
    }


}
