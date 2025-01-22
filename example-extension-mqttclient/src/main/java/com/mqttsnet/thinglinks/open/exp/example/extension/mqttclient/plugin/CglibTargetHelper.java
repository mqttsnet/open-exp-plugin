package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttClient;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConfiguration;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.callback.MqttCallback;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector.MqttConnector;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.MqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.msg.*;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.future.MqttFuture;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.future.MqttFutureWrapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;


/**
 * cglib拦截器帮助类
 * @author mqttsnet
 */
public abstract class CglibTargetHelper {


    /**
     * 根据具体的对象创建包装对象
     *
     * @param target 目标对象
     * @return 包装对象
     */
    public static Object createCglibTarget(Object target) {
        Object cglibTarget;
        if (target instanceof MqttClient) {
            cglibTarget = new CglibTargetMqttClient();
        } else if (target instanceof MqttConnector) {
            cglibTarget = new CglibTargetMqttConnector();
        } else if (target instanceof MqttDelegateHandler) {
            cglibTarget = new CglibTargetMqttDelegateHandler();
        } else {
            cglibTarget = target;
        }
        return cglibTarget;
    }


    public static class CglibTargetMqttClient implements MqttClient {

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public String getClientId() {
            return null;
        }

        @Override
        public MqttConnectParameter getMqttConnectParameter() {
            return null;
        }

        @Override
        public MqttFutureWrapper connectFuture() {
            return null;
        }

        @Override
        public void connect() {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public MqttFutureWrapper disconnectFuture() {
            return null;
        }

        @Override
        public MqttFutureWrapper disconnectFuture(MqttDisconnectMsg mqttDisconnectMsg) {
            return null;
        }

        @Override
        public void disconnect(MqttDisconnectMsg mqttDisconnectMsg) {

        }

        @Override
        public MqttFutureWrapper publishFuture(MqttMsgInfo mqttMsgInfo) {
            return null;
        }

        @Override
        public MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos, boolean retain) {
            return null;
        }

        @Override
        public MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos) {
            return null;
        }

        @Override
        public MqttFutureWrapper publishFuture(byte[] payload, String topic) {
            return null;
        }

        @Override
        public void publish(MqttMsgInfo mqttMsgInfo) {

        }

        @Override
        public void publish(byte[] payload, String topic, MqttQoS qos, boolean retain) {

        }

        @Override
        public void publish(byte[] payload, String topic, MqttQoS qos) {

        }

        @Override
        public void publish(byte[] payload, String topic) {

        }

        @Override
        public void subscribe(String topic, MqttQoS qos) {

        }

        @Override
        public void subscribe(MqttSubInfo mqttSubInfo) {

        }

        @Override
        public void subscribe(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {

        }

        @Override
        public void subscribes(List<MqttSubInfo> mqttSubInfoList) {

        }

        @Override
        public void subscribes(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {

        }

        @Override
        public void subscribes(List<String> topicList, MqttQoS qos) {

        }

        @Override
        public MqttFutureWrapper subscribesFuture(List<String> topicList, MqttQoS qos) {
            return null;
        }

        @Override
        public MqttFutureWrapper subscribeFuture(String topic, MqttQoS qos) {
            return null;
        }

        @Override
        public MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo) {
            return null;
        }

        @Override
        public MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
            return null;
        }

        @Override
        public MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
            return null;
        }

        @Override
        public MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList) {
            return null;
        }

        @Override
        public void unsubscribes(List<String> topicList, MqttProperties.UserProperties mqttUserProperties) {

        }

        @Override
        public void unsubscribes(List<String> topicList) {

        }

        @Override
        public void unsubscribe(String topic, MqttProperties.UserProperties mqttUserProperties) {

        }

        @Override
        public void unsubscribe(String topic) {

        }

        @Override
        public MqttFutureWrapper unsubscribeFuture(String topic, MqttProperties.UserProperties mqttUserProperties) {
            return null;
        }

        @Override
        public MqttFutureWrapper unsubscribeFuture(String topic) {
            return null;
        }

        @Override
        public MqttFutureWrapper unsubscribesFuture(List<String> topicList) {
            return null;
        }

        @Override
        public MqttFutureWrapper unsubscribesFuture(List<String> topicList, MqttProperties.UserProperties mqttUserProperties) {
            return null;
        }

        @Override
        public void addMqttCallback(MqttCallback mqttCallback) {

        }

        @Override
        public void addMqttCallbacks(Collection<MqttCallback> mqttCallbacks) {

        }

        @Override
        public boolean isOnline() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public boolean isClose() {
            return false;
        }

        @Override
        public void close() {

        }
    }


    public static class CglibTargetMqttConnector implements MqttConnector {

        @Override
        public MqttFuture<Channel> connect() {
            return null;
        }

        @Override
        public MqttDelegateHandler getMqttDelegateHandler() {
            return null;
        }

        @Override
        public MqttConfiguration getMqttConfiguration() {
            return null;
        }
    }

    public static class CglibTargetMqttDelegateHandler implements MqttDelegateHandler {


        @Override
        public void channelConnect(Channel channel) {

        }

        @Override
        public void sendConnect(Channel channel) {

        }

        @Override
        public void connack(Channel channel, MqttConnAckMessage mqttConnAckMessage) {

        }

        @Override
        public void auth(Channel channel, MqttMessage mqttAuthMessage) {

        }

        @Override
        public void sendAuth(Channel channel, byte reasonCode, MqttProperties mqttProperties) {

        }

        @Override
        public void sendDisconnect(Channel channel, MqttFuture mqttFuture, MqttDisconnectMsg mqttDisconnectMsg) {

        }

        @Override
        public void disconnect(Channel channel, MqttMessage mqttMessage) {

        }

        @Override
        public void sendSubscribe(Channel channel, MqttSubMsg mqttSubMsg) {

        }

        @Override
        public void suback(Channel channel, MqttSubAckMessage mqttSubAckMessage) {

        }

        @Override
        public void sendUnsubscribe(Channel channel, MqttUnsubMsg mqttUnsubMsg) {

        }

        @Override
        public void unsuback(Channel channel, MqttUnsubAckMessage mqttUnsubAckMessage) {

        }

        @Override
        public void sendPingreq(Channel channel) {

        }

        @Override
        public void pingresp(Channel channel, MqttMessage mqttPingRespMessage) {

        }

        @Override
        public void sendPublish(Channel channel, MqttMsg mqttMsg, MqttFuture msgFuture) {

        }

        @Override
        public void publish(Channel channel, MqttPublishMessage mqttPublishMessage) {

        }

        @Override
        public void sendPuback(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void puback(Channel channel, MqttPubAckMessage mqttPubAckMessage) {

        }

        @Override
        public void sendPubrec(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void pubrec(Channel channel, MqttMessage mqttMessage) {

        }

        @Override
        public void sendPubrel(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void pubrel(Channel channel, MqttMessage mqttMessage) {

        }

        @Override
        public void sendPubcomp(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void pubcomp(Channel channel, MqttMessage mqttMessage) {

        }

        @Override
        public void exceptionCaught(Channel channel, Throwable cause) {

        }
    }

}
