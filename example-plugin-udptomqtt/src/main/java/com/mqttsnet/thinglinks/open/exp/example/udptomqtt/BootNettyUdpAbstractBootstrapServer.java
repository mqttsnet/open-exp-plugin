package com.mqttsnet.thinglinks.open.exp.example.udptomqtt;

import io.netty.channel.ChannelPipeline;

/**
 * @author lin
 * @date 2024年08月30日 16:59
 */
public abstract class BootNettyUdpAbstractBootstrapServer implements BootNettyUdpBootstrap {
    void initChannelHandler(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(new BootNettyUdpSimpleChannelInboundHandler());
    }
}
