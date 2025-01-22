package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.exception;

/**
 * MQTT状态检测异常
 * @author mqttsnet
 */
public class MqttStateCheckException extends MqttException {

    public MqttStateCheckException(String message) {
        super(message);
    }

    public MqttStateCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqttStateCheckException(Throwable cause) {
        super(cause);
    }
}
