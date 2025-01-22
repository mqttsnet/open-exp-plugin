package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant;

/**
 * MQTT认证状态
 * @author mqttsnet
 */
public enum MqttAuthState {

    /**
     * 未认证，初始值
     */
    NOT_AUTH,
    /**
     * 认证失败
     */
    AUTH_FAIL,
    /**
     * 认证成功
     */
    AUTH_SUCCESS
}
