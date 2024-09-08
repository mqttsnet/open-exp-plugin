package com.mqttsnet.thinglinks.open.exp.example.udpserver;

/**
 * @author lin
 * @date 2024年08月30日 17:00
 */
public interface BootNettyUdpBootstrap {
    void startup(int port);

    void shutdown();
}
