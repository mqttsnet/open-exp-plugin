package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * -----------------------------------------------------------------------------
 * File Name: MqttMessageReceiveEvent
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * 消息接收事件
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
 * @date 2025/1/22 18:15
 */
@Getter
public class MqttMessageReceiveEvent extends ApplicationEvent {
    private final String topic;
    private final byte[] payload;
    private final MqttQoS qos;

    public MqttMessageReceiveEvent(Object source, String topic, byte[] payload, MqttQoS qos) {
        super(source);
        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
    }

}