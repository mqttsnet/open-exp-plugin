package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.listener;

import java.nio.charset.StandardCharsets;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttMessageReceiveEvent;
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
public class MqttMessageReceiveEventListener implements ApplicationListener<MqttMessageReceiveEvent> {
    @Async
    @Override
    public void onApplicationEvent(MqttMessageReceiveEvent event) {
        log.info("MqttMessageReceiveEventListener 接收到消息：Topic = {}, QoS = {}, Payload = {}", event.getTopic(), event.getQos(), new String(event.getPayload(), StandardCharsets.UTF_8));


    }


}
