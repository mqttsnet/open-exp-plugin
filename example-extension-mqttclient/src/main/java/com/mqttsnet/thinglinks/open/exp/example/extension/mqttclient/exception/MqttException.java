package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.exception;

/**
 * MQTT异常
 * @author mqttsnet
 */
public class MqttException extends RuntimeException {


    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    public MqttException(String message) {
        super(message);
    }

    public MqttException(String message, String clientId) {
        super(message);
        this.clientId = clientId;
    }

    public MqttException(String message, Throwable cause, String clientId) {
        super(message, cause);
        this.clientId = clientId;
    }

    public MqttException(Throwable cause, String clientId) {
        super(cause);
        this.clientId = clientId;
    }

    public MqttException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String clientId) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.clientId = clientId;
    }

    public MqttException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqttException(Throwable cause) {
        super(cause);
    }
}
