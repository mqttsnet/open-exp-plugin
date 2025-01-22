package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConfiguration;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.MqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.future.MqttFuture;
import io.netty.channel.Channel;

/**
 * MQTT连接器接口
 * @author mqttsnet
 */
public interface MqttConnector {

    /**
     * 进行MQTT连接
     *
     * @return Future
     */
    MqttFuture<Channel> connect();

    /**
     * 获取消息委托处理器
     *
     * @return MqttDelegateHandler
     */
    MqttDelegateHandler getMqttDelegateHandler();

    /**
     * 获取MQTT全局配置
     *
     * @return MqttConfiguration
     */
    MqttConfiguration getMqttConfiguration();

}
