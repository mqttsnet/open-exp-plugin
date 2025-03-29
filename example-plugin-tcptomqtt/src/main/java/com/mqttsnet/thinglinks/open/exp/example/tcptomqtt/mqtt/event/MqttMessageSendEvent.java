package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * -----------------------------------------------------------------------------
 * File Name: MqttMessageSendEvent
 * -----------------------------------------------------------------------------
 * Description:
 * 消息发送事件
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
 * @date 2025/1/22 18:14
 */
@Getter
public class MqttMessageSendEvent extends ApplicationEvent {
    private final int messageId;
    private final String[] topics;

    /**
     * 构造消息发送事件
     *
     * @param source    事件源
     * @param messageId 消息ID
     * @param topics    成功投递的主题数组
     */
    public MqttMessageSendEvent(Object source, int messageId, String[] topics) {
        super(source);
        this.messageId = messageId;
        this.topics = topics;
    }

}