package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.createor;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConfiguration;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback.MqttCallback;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector.DefaultMqttConnector;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector.MqttConnector;

/**
 * 默认的MQTT连接器创建器
 * @author mqttsnet
 */
public class MqttConnectorObjectCreator implements ObjectCreator<MqttConnector> {


    @Override
    public MqttConnector createObject(Object... constructorArgs) {
        MqttConfiguration mqttConfiguration = (MqttConfiguration) constructorArgs[0];
        MqttConnectParameter mqttConnectParameter = (MqttConnectParameter) constructorArgs[1];
        MqttCallback mqttCallback = (MqttCallback) constructorArgs[2];
        return new DefaultMqttConnector(mqttConfiguration,mqttConnectParameter,mqttCallback);
    }
}
