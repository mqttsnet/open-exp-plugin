package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.store;

import java.util.List;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttMsgDirection;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.msg.MqttMsg;

/**
 * MQTT消息储存器接口
 * @author mqttsnet
 */
public interface MqttMsgStore {

    /**
     * 获取一个MQTT消息
     *
     * @param mqttMsgDirection 消息的方向
     * @param clientId         客户端ID
     * @param msgId            消息ID
     * @return MQTT消息
     */
    MqttMsg getMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId);

    /**
     * 存储一个MQTT消息
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     * @param mqttMsg          MQTT消息
     */
    void putMsg(MqttMsgDirection mqttMsgDirection, String clientId, MqttMsg mqttMsg);

    /**
     * 移除一个MQTT消息
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     * @param msgId            消息ID
     * @return MQTT消息
     */
    MqttMsg removeMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId);

    /**
     * 拿到客户端的MQTT消息列表
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     * @return MQTT消息列表
     */
    List<MqttMsg> getMsgList(MqttMsgDirection mqttMsgDirection, String clientId);

    /**
     * 清理客户端的MQTT消息
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     */
    void clearMsg(MqttMsgDirection mqttMsgDirection, String clientId);

    /**
     * 清理客户端的MQTT消息
     *
     * @param clientId 客户端ID
     */
    default void clearMsg(String clientId) {
        clearMsg(MqttMsgDirection.SEND, clientId);
        clearMsg(MqttMsgDirection.RECEIVE, clientId);
    }

    /**
     * 关闭消息存储器
     */
    default void close() {

    }
}
