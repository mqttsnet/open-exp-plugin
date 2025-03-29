package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event;

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
    private final int qos;

    /**
     * 构造消息接收事件
     *
     * @param source  事件源(通常为发布者实例)
     * @param topic   消息主题
     * @param payload 消息内容(字节数组)
     * @param qos     服务质量等级(0-2)
     */
    public MqttMessageReceiveEvent(Object source, String topic, byte[] payload, int qos) {
        super(source);
        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
    }

}