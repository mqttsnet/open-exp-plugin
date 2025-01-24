package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt;

import com.mqttsnet.thinglinks.open.exp.client.ConfigSupport;
import com.mqttsnet.thinglinks.open.exp.plugin.depend.AbstractBoot;

/**
 * 插件核心配置文件
 *
 * @author mqttsnet
 */
public class Boot extends AbstractBoot {

    /**
     * 租户ID
     */
    public static ConfigSupport tenantId = new ConfigSupport("plugin.tenantId", "1");

    /**
     * TCP Server 启动的端口号
     */
    public static ConfigSupport tcpPort = new ConfigSupport("tcp.port", "50110");

    /**
     * Netty Worker线程数
     */
    public static ConfigSupport workerThreads = new ConfigSupport("netty.workerThreads", "4");

    /**
     * SO_BACKLOG: 全连接队列的最大长度
     */
    public static ConfigSupport soBacklog = new ConfigSupport("netty.soBacklog", "128");

    /**
     * TCP_NODELAY: 是否开启Nagle算法
     */
    public static ConfigSupport tcpNoDelay = new ConfigSupport("netty.tcpNoDelay", "true");

    /**
     * SO_KEEPALIVE: 是否开启TCP KeepAlive
     */
    public static ConfigSupport soKeepAlive = new ConfigSupport("netty.soKeepAlive", "true");


    //   ------------------------------MQTT 相关配置-------------------------------------------------

    /**
     * mqtt broker host
     */
    public static ConfigSupport mqttBrokerHost = new ConfigSupport("mqtt.broker.host", "broker.thinglinks.mqttsnet.com");

    /**
     * mqtt broker port
     */
    public static ConfigSupport mqttBrokerPort = new ConfigSupport("mqtt.broker.port", "11883");

    /**
     * 客户端ID
     * 软网关
     */
    public static ConfigSupport mqttClientClientId = new ConfigSupport("mqtt.client.clientId", "3653578716192768@483305815051076198");


    /**
     * 设备唯一标识
     * 软网关
     */
    public static ConfigSupport mqttClientDeviceIdentification = new ConfigSupport("mqtt.client.deviceIdentification", "3653578720387072");

    /**
     * 客户端用户名
     * 软网关
     */
    public static ConfigSupport mqttClientUsername = new ConfigSupport("mqtt.client.userName", "123456");

    /**
     * 客户端密码
     * 软网关
     */
    public static ConfigSupport mqttClientPassword = new ConfigSupport("mqtt.client.password", "123456");

    /**
     * 订阅的topic
     * ThingLinks 命名下发Topic (云 ——》端)
     */
    public static ConfigSupport mqttClientCommandTopic = new ConfigSupport("mqtt.client.command.topic", "/v1/devices/" + mqttClientDeviceIdentification.getDefaultValue() + "/command");

    /**
     * 订阅的topic
     * ThingLinks 数据上报 Topic (云 ——》端)
     */
    public static ConfigSupport mqttClientDatasTopic = new ConfigSupport("mqtt.client.datas.topic", "/v1/devices/" + mqttClientDeviceIdentification.getDefaultValue() + "/datas");

}
