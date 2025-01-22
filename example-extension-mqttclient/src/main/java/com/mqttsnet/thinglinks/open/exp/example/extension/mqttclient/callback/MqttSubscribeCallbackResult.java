package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback;

import java.util.ArrayList;
import java.util.List;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

/**
 * MQTT订阅回调结果
 * @author mqttsnet
 */
public class MqttSubscribeCallbackResult extends MqttCallbackResult {

    /**
     * 订阅回调消息集合
     */
    private final List<MqttSubscribeCallbackInfo> subscribeCallbackInfoList = new ArrayList<>();
    /**
     * 消息ID
     */
    private final int msgId;

    public MqttSubscribeCallbackResult(String clientId,int msgId,List<MqttSubscribeCallbackInfo> subscribeCallbackInfoList) {
        super(clientId);
        AssertUtils.notNull(subscribeCallbackInfoList,"subscribeCallbackInfoList is null");
        this.subscribeCallbackInfoList.addAll(subscribeCallbackInfoList);
        this.msgId = msgId;
    }

    public List<MqttSubscribeCallbackInfo> getSubscribeCallbackInfoList() {
        return subscribeCallbackInfoList;
    }

    public int getMsgId() {
        return msgId;
    }

    /**
     * MQTT5
     * 获取原因字符串
     * @return 原因字符串
     */
    public String getReasonString() {
        String reasonString = null;
        if(mqttProperties != null) {
            reasonString = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.REASON_STRING);
        }
        return reasonString;
    }

    @Override
    public String toString() {
        return "MqttSubscribeCallbackResult{" +
                "subscribeCallbackInfoList=" + subscribeCallbackInfoList +
                ", msgId=" + msgId +
                "} " + super.toString();
    }
}
