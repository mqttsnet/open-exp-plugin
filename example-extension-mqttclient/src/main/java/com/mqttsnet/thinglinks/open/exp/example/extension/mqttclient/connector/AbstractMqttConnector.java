package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLException;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConfiguration;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.MqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.AssertUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

/**
 * 抽象的MQTT连接器
 * @author mqttsnet
 */
public abstract class AbstractMqttConnector implements MqttConnector {

    protected final MqttConfiguration configuration;
    protected final MqttConnectParameter mqttConnectParameter;
    protected final MqttDelegateHandler mqttDelegateHandler;

    public AbstractMqttConnector(MqttConfiguration configuration, MqttConnectParameter mqttConnectParameter, Object... handlerCreateArgs) {
        AssertUtils.notNull(configuration, "configuration is null");
        AssertUtils.notNull(mqttConnectParameter, "mqttConnectParameter is null");
        this.configuration = configuration;
        this.mqttConnectParameter = mqttConnectParameter;
        this.mqttDelegateHandler = createDelegateHandle(handlerCreateArgs);
    }

    /**
     * 创建一个MQTT委托处理器，子类实现
     *
     * @param handlerCreateArgs 创建的参数
     * @return MqttDelegateHandler
     */
    protected abstract MqttDelegateHandler createDelegateHandle(Object... handlerCreateArgs);

    /**
     * 添加Netty的TCP参数
     *
     * @param bootstrap 启动器
     * @param optionMap 参数Map
     */
    protected void addOptions(Bootstrap bootstrap, Map<ChannelOption, Object> optionMap) {
        Iterator<Map.Entry<ChannelOption, Object>> optionIterator = optionMap.entrySet().iterator();
        while (optionIterator.hasNext()) {
            Map.Entry<ChannelOption, Object> option = optionIterator.next();
            bootstrap.option(option.getKey(), option.getValue());
        }
    }


    @Override
    public MqttDelegateHandler getMqttDelegateHandler() {
        return this.mqttDelegateHandler;
    }

    @Override
    public MqttConfiguration getMqttConfiguration() {
        return this.configuration;
    }

    /**
     * SSL处理
     *
     * @param allocator 内存分配器
     * @return Ssl处理器
     * @throws SSLException ssl异常
     */
    protected SslHandler getSslHandler(ByteBufAllocator allocator) throws SSLException {
        boolean singleSsl = true;
        File clientCertificateFile = mqttConnectParameter.getClientCertificateFile();
        File clientPrivateKeyFile = mqttConnectParameter.getClientPrivateKeyFile();
        File rootCertificateFile = mqttConnectParameter.getRootCertificateFile();
        //客户端私钥和客户端证书都不为null才是双向认证
        if (clientCertificateFile != null && clientPrivateKeyFile != null) {
            singleSsl = false;
        }
        SslContext sslCtx;
        if (singleSsl) {
            //单向认证
            sslCtx = SslContextBuilder.forClient().trustManager(rootCertificateFile).build();
        } else {
            //双向认证
            sslCtx = SslContextBuilder.forClient().keyManager(clientCertificateFile, clientPrivateKeyFile).trustManager(rootCertificateFile).build();
        }
        return sslCtx.newHandler(allocator, mqttConnectParameter.getHost(), mqttConnectParameter.getPort());

    }


}
