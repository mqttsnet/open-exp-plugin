package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector;

import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLException;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConfiguration;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback.MqttCallback;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttAuthState;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttConstant;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttVersion;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.exception.MqttException;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.MqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.channel.MqttChannelHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.future.DefaultMqttFuture;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.future.MqttFuture;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

/**
 * 默认的MQTT连接器
 * @author mqttsnet
 */
public class DefaultMqttConnector extends AbstractMqttConnector {


    private final MqttChannelHandler mqttChannelHandler;
    private final Bootstrap bootstrap = new Bootstrap();
    private final int connectTimeoutMillis;

    public DefaultMqttConnector(MqttConfiguration configuration, MqttConnectParameter mqttConnectParameter, MqttCallback mqttCallback) {
        super(configuration, mqttConnectParameter, mqttCallback);
        mqttChannelHandler = new MqttChannelHandler(mqttDelegateHandler, mqttConnectParameter);
        connectTimeoutMillis = (int) (mqttConnectParameter.getConnectTimeoutSeconds() * 1000);
        initNettyBootstrap();
    }


    private void initNettyBootstrap() {
        addOptions(bootstrap, configuration.getOptionMap());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis);
        bootstrap.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY, MqttAuthState.NOT_AUTH);
        bootstrap.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY, new ConcurrentHashMap(16));
        bootstrap.attr(MqttConstant.RECEIVE_MSG_MAP_ATTRIBUTE_KEY, new ConcurrentHashMap(16));
        bootstrap.attr(MqttConstant.MQTT_CLIENT_ID_ATTRIBUTE_KEY, mqttConnectParameter.getClientId());
        if(mqttConnectParameter.getMqttVersion() == MqttVersion.MQTT_5_0_0) {
            bootstrap.attr(MqttConstant.TOPIC_ALIAS_MAP_ATTRIBUTE_KEY,new ConcurrentHashMap<>());
        }
        bootstrap.group(configuration.getEventLoopGroup()).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws SSLException {
                ChannelPipeline pipeline = ch.pipeline();
                if (mqttConnectParameter.isSsl()) {
                    pipeline.addLast(MqttConstant.NETTY_SSL_HANDLER_NAME,getSslHandler(ch.alloc()));
                }
                //空闲检测在连接完成后再添加
                //Netty自带的编解码器
                pipeline.addLast(MqttConstant.NETTY_DECODER_HANDLER_NAME, new MqttDecoder());
                pipeline.addLast(MqttConstant.NETTY_ENCODER_HANDLER_NAME, MqttEncoder.INSTANCE);
                //Mqtt协议处理
                pipeline.addLast(MqttConstant.NETTY_CHANNEL_HANDLER_NAME, mqttChannelHandler);
            }
        });
    }

    @Override
    public MqttFuture<Channel> connect() {
        LogUtils.info(DefaultMqttConnector.class, "client:" + mqttConnectParameter.getClientId() + " tcp connecting to " + mqttConnectParameter.getHost() + ": mqttConnectParameter.getPort()");
        //Netty进行TCP连接
        ChannelFuture nettyChannelFuture = bootstrap.connect(mqttConnectParameter.getHost(), mqttConnectParameter.getPort());
        Channel channel = nettyChannelFuture.channel();
        //创建一个MQTT的Future
        MqttFuture<Channel> connectMqttFuture = new DefaultMqttFuture(mqttConnectParameter.getClientId(), channel.id().asShortText(), channel);
        //添加TCP的成功或失败监听
        nettyChannelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LogUtils.info(DefaultMqttConnector.class, "client:" + mqttConnectParameter.getClientId() + " listening to connector tcp connected successfully,local:" + channel.localAddress() + ",remote:" + channel.remoteAddress());
                //TCP连接成功，下一步进行MQTT的认证连接
                mqttDelegateHandler.sendConnect(channel);
            } else {
                LogUtils.info(DefaultMqttConnector.class, "client:" + mqttConnectParameter.getClientId() + " listening to connector tcp connected failed,host:" + mqttConnectParameter.getHost() + ",port:" + mqttConnectParameter.getPort() + ",cause:" + future.cause().getMessage());
                //设置失败，唤醒MQTT连接的Future
                connectMqttFuture.setFailure(future.cause());
            }
        });
        //添加TCP的连接断开监听（只有TCP连接成功后再断开才会有该监听）
        channel.closeFuture().addListener((ChannelFutureListener) future -> {
            MqttAuthState mqttAuthState = channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get();
            //判断是否完成认证，如果没有完成认证（可能TCP连接上之后直接断开，没有进行MQTT的连接认证），设置失败，唤醒MQTT连接的Future
            if (MqttAuthState.NOT_AUTH == mqttAuthState) {
                LogUtils.warn(DefaultMqttConnector.class, "client:" + mqttConnectParameter.getClientId() + " disconnect without authentication");
                MqttFuture connectFuture = MqttFuture.getFuture(mqttConnectParameter.getClientId(), channel.id().asShortText());
                if (connectFuture != null) {
                    connectFuture.setFailure(new MqttException("client did not complete authentication, connection has been lost", mqttConnectParameter.getClientId()));
                }
            }
        });
        return connectMqttFuture;
    }

    @Override
    protected MqttDelegateHandler createDelegateHandle(Object... handlerCreateArgs) {
        return configuration.newMqttMsgHandler(mqttConnectParameter, handlerCreateArgs[0], configuration.getMqttMsgStore());
    }
}
