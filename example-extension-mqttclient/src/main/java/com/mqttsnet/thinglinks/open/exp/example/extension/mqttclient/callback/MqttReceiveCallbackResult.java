package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback;

import java.util.Arrays;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttMsgState;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.msg.MqttMsg;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * MQTT接受到消息回调结果
 * @author mqttsnet
 */
public class MqttReceiveCallbackResult extends MqttCallbackResult {

    /**
     * 接收到的MQTT消息
     */
    private final MqttMsg mqttMsg;

    /**
     * 消息ID
     */
    private final Integer msgId;
    /**
     * 主题
     */
    private final String topic;
    /**
     * qos
     */
    private final MqttQoS qos;
    /**
     * 是否保存
     */
    private final boolean retain;
    /**
     * 载荷
     */
    private final byte[] payload;

    /**
     * 是否重复发送
     */
    private boolean dup;

    /**
     * 消息状态
     */
    private MqttMsgState msgState;

    /**
     * 原因码
     */
    private Byte reasonCode;


    public MqttReceiveCallbackResult(String clientId, MqttMsg mqttMsg) {
        super(clientId);
        AssertUtils.notNull(mqttMsg, "mqttMsg is null");
        this.mqttMsg = mqttMsg;
        this.msgId = mqttMsg.getMsgId();
        this.payload = mqttMsg.getPayload();
        this.topic = mqttMsg.getTopic();
        this.qos = mqttMsg.getQos();
        this.retain = mqttMsg.isRetain();
        this.dup = mqttMsg.isDup();
        this.msgState = mqttMsg.getMsgState();
        this.mqttProperties = mqttMsg.getMqttProperties();
        this.reasonCode = mqttMsg.getReasonCode();
    }

    @Deprecated
    public MqttMsg getMqttMsg() {
        return mqttMsg;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public String getTopic() {
        return topic;
    }

    public MqttQoS getQos() {
        return qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public byte[] getPayload() {
        return payload;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public MqttMsgState getMsgState() {
        return msgState;
    }

    public void setMsgState(MqttMsgState msgState) {
        this.msgState = msgState;
    }

    /**
     * MQTT5
     * 获取原因字符串
     *
     * @return 原因字符串
     */
    public String getReasonString() {
        String reasonString = null;
        if (mqttProperties != null) {
            reasonString = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.REASON_STRING);
        }
        return reasonString;
    }

    /**
     * MQTT5
     * 获取载荷标识符
     *
     * @return 载荷标识符
     */
    public Integer getPayloadFormatIndicator() {
        Integer payloadFormatIndicator = null;
        if (mqttProperties != null) {
            payloadFormatIndicator = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.PAYLOAD_FORMAT_INDICATOR);
        }
        return payloadFormatIndicator;
    }

    /**
     * MQTT5
     * 获取响应主题
     *
     * @return 响应主题
     */
    public String getResponseTopic() {
        String responseTopic = null;
        if (mqttProperties != null) {
            responseTopic = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.RESPONSE_TOPIC);
        }
        return responseTopic;

    }

    /**
     * MQTT5
     * 获取对比数据
     *
     * @return 对比数据
     */
    public byte[] getCorrelationData() {
        byte[] correlationData = null;
        if (mqttProperties != null) {
            correlationData = getBinaryMqttPropertyValue(MqttProperties.MqttPropertyType.CORRELATION_DATA);
        }
        return correlationData;
    }

    /**
     * MQTT5
     * 获取订阅标识符
     *
     * @return 订阅标识符
     */
    public Integer getSubscriptionIdentifier() {
        Integer subscriptionIdentifier = null;
        if (mqttProperties != null) {
            subscriptionIdentifier = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER);
        }
        return subscriptionIdentifier;
    }

    /**
     * MQTT5
     * 获取内容类型
     *
     * @return 内容类型
     */
    public String getContentType() {
        String contentType = null;
        if (mqttProperties != null) {
            contentType = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.CONTENT_TYPE);
        }
        return contentType;
    }


    public Byte getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Byte reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Override
    public String toString() {
        return "MqttReceiveCallbackResult{" +
                ", msgId=" + msgId +
                ", topic='" + topic + '\'' +
                ", qos=" + qos +
                ", retain=" + retain +
                ", payload=" + Arrays.toString(payload) +
                ", dup=" + dup +
                ", msgState=" + msgState +
                ", reasonCode=" + reasonCode +
                "} " + super.toString();
    }
}
