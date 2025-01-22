package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.createor;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.DefaultMqttClient;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttClient;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConfiguration;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;

/**
 * 默认的MQTT客户端创建器
 * @author mqttsnet
 */
public class MqttClientObjectCreator implements ObjectCreator<MqttClient> {

    @Override
    public MqttClient createObject(Object... constructorArgs) {
        MqttConfiguration mqttConfiguration = (MqttConfiguration) constructorArgs[1];
        MqttConnectParameter mqttConnectParameter = (MqttConnectParameter) constructorArgs[2];
        return new DefaultMqttClient(mqttConfiguration,mqttConnectParameter);
    }
}
