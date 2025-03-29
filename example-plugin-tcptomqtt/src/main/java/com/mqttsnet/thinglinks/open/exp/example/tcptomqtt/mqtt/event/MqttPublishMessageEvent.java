package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event;

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

    private final int qos;

    private final boolean retain;

    /**
     * 构造发布请求事件
     *
     * @param source  事件源
     * @param topic   目标主题
     * @param payload 消息内容
     * @param qos     服务质量
     * @param retain  保留标志
     */
    public MqttPublishMessageEvent(Object source, String topic, byte[] payload, int qos, boolean retain) {
        super(source);
        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
        this.retain = retain;
    }

}