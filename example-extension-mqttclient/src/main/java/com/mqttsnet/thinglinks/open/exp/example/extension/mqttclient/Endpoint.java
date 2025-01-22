package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient;

import java.net.InetSocketAddress;

/**
 * 端点，即客户端和服务端的连接信息
 * @author mqttsnet
 */
public interface Endpoint {

    /**
     * 获取本机的地址，Channel是open时才有值
     *
     * @return InetSocketAddress
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取服务器的地址，Channel是open时才有值
     *
     * @return InetSocketAddress
     */
    InetSocketAddress getRemoteAddress();

}
