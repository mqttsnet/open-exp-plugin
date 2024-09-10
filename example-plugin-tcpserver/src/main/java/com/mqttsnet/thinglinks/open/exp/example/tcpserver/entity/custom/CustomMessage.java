package com.mqttsnet.thinglinks.open.exp.example.tcpserver.entity.custom;

import lombok.Data;

/**
 * -----------------------------------------------------------------------------
 * File Name: CustomMessage
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
 * 2024/9/8       xiaonannet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2024/9/8 19:07
 */

@Data
public class CustomMessage {
    private String messageType;  // 消息类型
    private String payload;      // 消息内容
    private long timestamp;      // 消息接收时间

    // 构造函数
    public CustomMessage(String messageType, String payload) {
        this.messageType = messageType;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();  // 获取消息的接收时间
    }

    // Getter 和 Setter 方法
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CustomMessage{" +
                "messageType='" + messageType + '\'' +
                ", payload='" + payload + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
