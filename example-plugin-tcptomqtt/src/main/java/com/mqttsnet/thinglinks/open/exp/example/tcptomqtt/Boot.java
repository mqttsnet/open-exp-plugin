package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt;

import com.mqttsnet.thinglinks.open.exp.client.ConfigSupport;
import com.mqttsnet.thinglinks.open.exp.plugin.depend.AbstractBoot;

public class Boot extends AbstractBoot {
    /**
     * tcpServer启动的端口号
     */
    public static ConfigSupport tcpPort = new ConfigSupport("tcp.port", "8082");
    /**
     * mqtt broker地址
     */
    public static ConfigSupport mqttBrokerUrl = new ConfigSupport("mqtt.brokerUrl", "tcp://127.0.0.1:1883");
    /**
     * 订阅的topic
     */
    public static ConfigSupport mqttTopic = new ConfigSupport("mqtt.topic", "test/zjl");
    /**
     * 客户端id
     */
    public static ConfigSupport mqttClientId = new ConfigSupport("mqtt.clientId", "mqttx_cf427182");
}
