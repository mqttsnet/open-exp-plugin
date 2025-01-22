package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.createor;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback.MqttCallback;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.DefaultMqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.MqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.store.MqttMsgStore;

/**
 * 默认的MQTT消息委托处理器创建器
 * @author mqttsnet
 */
public class MqttDelegateHandlerObjectCreator implements ObjectCreator<MqttDelegateHandler> {


    @Override
    public MqttDelegateHandler createObject(Object... constructorArgs) {
        MqttConnectParameter mqttConnectParameter = (MqttConnectParameter) constructorArgs[0];
        MqttCallback mqttCallback = (MqttCallback) constructorArgs[1];
        MqttMsgStore mqttMsgStore = (MqttMsgStore) constructorArgs[2];
        return new DefaultMqttDelegateHandler(mqttConnectParameter,mqttCallback,mqttMsgStore);
    }
}
