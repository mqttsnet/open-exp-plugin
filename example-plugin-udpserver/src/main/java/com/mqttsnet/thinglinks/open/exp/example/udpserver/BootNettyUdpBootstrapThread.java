package com.mqttsnet.thinglinks.open.exp.example.udpserver;

/**
 * @author lin
 * @date 2024年08月30日 17:01
 */
public class BootNettyUdpBootstrapThread extends Thread{
    private final int port;

    public BootNettyUdpBootstrapThread(int port){
        this.port = port;
    }

    private BootNettyUdpBootstrap iotUdpBootstrap;
    public void run() {
        if (iotUdpBootstrap == null){
            iotUdpBootstrap = new BootNettyUdpBootstrapServer();
        }
        iotUdpBootstrap.startup(this.port);
    }

    public void shutdown(){
        if (iotUdpBootstrap != null){
            iotUdpBootstrap.shutdown();
        }
    }
}
