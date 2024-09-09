package com.mqttsnet.thinglinks.open.exp.example.tcpserver;

import com.mqttsnet.thinglinks.open.exp.client.ConfigSupport;
import com.mqttsnet.thinglinks.open.exp.plugin.depend.AbstractBoot;

/**
 * 启动类，暴露Netty服务器相关的关键配置参数。
 */
public class Boot extends AbstractBoot {

    /**
     * TCP Server 启动的端口号
     */
    public static ConfigSupport tcpPort = new ConfigSupport("tcp.port", "18081");

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
}
