package com.mqttsnet.thinglinks.open.exp.example.tcpserver;

import com.mqttsnet.thinglinks.open.exp.client.ConfigSupport;
import com.mqttsnet.thinglinks.open.exp.plugin.depend.AbstractBoot;

public class Boot extends AbstractBoot {
    /**
     * TCP Server 启动的端口号
     */
    public static ConfigSupport tcpPort = new ConfigSupport("tcp.port", "8081");
}