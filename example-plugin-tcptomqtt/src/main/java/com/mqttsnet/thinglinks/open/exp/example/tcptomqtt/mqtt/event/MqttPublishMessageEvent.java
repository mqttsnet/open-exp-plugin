package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * -----------------------------------------------------------------------------
 * File Name: MqttMessageEvent
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
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
 * @date 2025/1/22 16:12
 */

@Getter
public class MqttPublishMessageEvent extends ApplicationEvent {
    private final String topic;

    private final byte[] payload;

    private final MqttQoS qos;

    private final boolean retain;

    public MqttPublishMessageEvent(Object source, String topic, byte[] payload, MqttQoS qos, boolean retain) {
        super(source);
        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
        this.retain = retain;
    }

}