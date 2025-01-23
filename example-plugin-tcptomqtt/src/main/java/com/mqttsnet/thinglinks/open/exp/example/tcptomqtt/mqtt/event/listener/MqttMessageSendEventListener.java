package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.listener;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttMessageSendEvent;
import lombok.extern.slf4j.Slf4j;
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
public class MqttMessageSendEventListener implements ApplicationListener<MqttMessageSendEvent> {
    @Async
    @Override
    public void onApplicationEvent(MqttMessageSendEvent event) {
        log.info("MqttMessageSendEventListener 消息发送成功：Topic = {}, QoS = {}, Payload = {}", event.getTopic(), event.getQos().value(), new String(event.getPayload()));

    }


}
