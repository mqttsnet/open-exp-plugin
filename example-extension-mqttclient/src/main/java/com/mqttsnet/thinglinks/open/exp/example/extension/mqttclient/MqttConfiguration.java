package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector.MqttConnector;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttConstant;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.createor.MqttClientObjectCreator;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.createor.MqttConnectorObjectCreator;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.createor.MqttDelegateHandlerObjectCreator;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.createor.ObjectCreator;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.MqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Interceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.store.MemoryMqttMsgStore;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.store.MqttMsgStore;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.proxy.JdkProxyFactory;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.proxy.ProxyFactory;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.AssertUtils;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.LogUtils;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * MQTT全局配置
 * @author mqttsnet
 */
public class MqttConfiguration {

    /**
     * MQTT的客户端工厂
     */
    private MqttClientFactory mqttClientFactory;
    /**
     * Netty中的TCP相关的参数Map
     */
    private final Map<ChannelOption, Object> optionMap = new ConcurrentHashMap<>();
    /**
     * 代理工厂
     */
    private ProxyFactory proxyFactory = new JdkProxyFactory();
    /**
     * 拦截器集合
     */
    private final List<Interceptor> interceptorList = new CopyOnWriteArrayList<>();
    /**
     * IO的最大线程数，默认为1，根据要创建的客户端数量进行调整
     */
    private final int maxThreadNumber;
    /**
     * Netty的线程组
     */
    private final EventLoopGroup eventLoopGroup;
    /**
     * MQTT客户端创建器
     */
    private ObjectCreator<MqttClient> mqttClientObjectCreator = new MqttClientObjectCreator();
    /**
     * MQTT连接器创建器
     */
    private ObjectCreator<MqttConnector> mqttConnectorObjectCreator = new MqttConnectorObjectCreator();
    /**
     * MQTT委托处理器创建器
     */
    private ObjectCreator<MqttDelegateHandler> mqttDelegateHandlerObjectCreator = new MqttDelegateHandlerObjectCreator();
    /**
     * MQTT消息存储器
     */
    private MqttMsgStore mqttMsgStore = new MemoryMqttMsgStore();


    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public MqttConfiguration() {
        this(1);
    }

    public MqttConfiguration(int maxThreadNumber) {
        if (maxThreadNumber <= 0) {
            maxThreadNumber = 1;
        }
        this.maxThreadNumber = maxThreadNumber;
        this.eventLoopGroup = new NioEventLoopGroup(maxThreadNumber, new DefaultThreadFactory(MqttConstant.THREAD_FACTORY_POOL_NAME));

    }

    public void addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptorList.add(interceptor);
        }
    }

    public MqttClient newMqttClient(Object... constructorArgs) {
        MqttClient mqttClient = mqttClientObjectCreator.createObject(constructorArgs);
        return (MqttClient) proxyFactory.getProxy(mqttClient, interceptorList);
    }

    public MqttConnector newMqttConnector(Object... constructorArgs) {
        MqttConnector mqttConnector = mqttConnectorObjectCreator.createObject(constructorArgs);
        return (MqttConnector) proxyFactory.getProxy(mqttConnector, interceptorList);
    }

    public MqttDelegateHandler newMqttMsgHandler(Object... constructorArgs) {
        MqttDelegateHandler mqttDelegateHandler = mqttDelegateHandlerObjectCreator.createObject(constructorArgs);
        return (MqttDelegateHandler) proxyFactory.getProxy(mqttDelegateHandler, interceptorList);
    }

    public List<Interceptor> getInterceptorList() {
        return Collections.unmodifiableList(interceptorList);
    }

    public int getMaxThreadNumber() {
        return maxThreadNumber;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        AssertUtils.notNull(proxyFactory,"proxyFactory is null");
        this.proxyFactory = proxyFactory;
    }

    /**
     * 添加一个Netty中的TCP参数
     *
     * @param option 参数项
     * @param value  值（值不能为null，为null时则为删除该参数项）
     */
    public void option(ChannelOption option, Object value) {
        if (value == null) {
            optionMap.remove(option);
        } else {
            optionMap.put(option, value);
        }
    }

    /**
     * 获取Netty中的TCP参数Map
     *
     * @return 不可修改的TCP参数Map
     */
    public Map<ChannelOption, Object> getOptionMap() {
        return Collections.unmodifiableMap(optionMap);
    }

    /**
     * 关闭配置，在关闭MqttClientFactory时调用，会释放线程资源
     */
    public synchronized void close() {
        LogUtils.info(MqttConfiguration.class,"MqttConfiguration close");
        eventLoopGroup.shutdownGracefully().syncUninterruptibly();
        LogUtils.info(MqttConfiguration.class,"MqttMsgStore close");
        mqttMsgStore.close();
    }

    public ObjectCreator<MqttClient> getMqttClientObjectCreator() {
        return mqttClientObjectCreator;
    }

    public void setMqttClientObjectCreator(ObjectCreator<MqttClient> mqttClientObjectCreator) {
        AssertUtils.notNull(mqttClientObjectCreator, "mqttClientObjectCreator is null");
        this.mqttClientObjectCreator = mqttClientObjectCreator;
    }

    public ObjectCreator<MqttConnector> getMqttConnectorObjectCreator() {
        return mqttConnectorObjectCreator;
    }

    public void setMqttConnectorObjectCreator(ObjectCreator<MqttConnector> mqttConnectorObjectCreator) {
        AssertUtils.notNull(mqttConnectorObjectCreator, "mqttConnectorObjectCreator is null");
        this.mqttConnectorObjectCreator = mqttConnectorObjectCreator;
    }

    public ObjectCreator<MqttDelegateHandler> getMqttDelegateHandlerObjectCreator() {
        return mqttDelegateHandlerObjectCreator;
    }

    public void setMqttDelegateHandlerObjectCreator(ObjectCreator<MqttDelegateHandler> mqttDelegateHandlerObjectCreator) {
        AssertUtils.notNull(mqttDelegateHandlerObjectCreator, "mqttDelegateHandlerObjectCreator is null");
        this.mqttDelegateHandlerObjectCreator = mqttDelegateHandlerObjectCreator;
    }

    public MqttMsgStore getMqttMsgStore() {
        return mqttMsgStore;
    }

    public void setMqttMsgStore(MqttMsgStore mqttMsgStore) {
        AssertUtils.notNull(mqttMsgStore, "mqttMsgStore is null");
        this.mqttMsgStore = mqttMsgStore;
    }

    public MqttClientFactory getMqttClientFactory() {
        return mqttClientFactory;
    }

    public void setMqttClientFactory(MqttClientFactory mqttClientFactory) {
        this.mqttClientFactory = mqttClientFactory;
    }
}
