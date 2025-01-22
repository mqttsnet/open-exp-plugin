package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.future;

/**
 * MQTTFuture的监听器
 * @author mqttsnet
 */
public interface MqttFutureListener<T> {

    /**
     * 操作完成回调
     *
     * @param mqttFuture Future
     * @throws Exception 异常
     */
    void operationComplete(MqttFuture<T> mqttFuture) throws Throwable;
}
