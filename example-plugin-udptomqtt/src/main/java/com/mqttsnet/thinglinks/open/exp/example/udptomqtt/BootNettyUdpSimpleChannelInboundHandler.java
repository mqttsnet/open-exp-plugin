package com.mqttsnet.thinglinks.open.exp.example.udptomqtt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.eclipse.paho.client.mqttv3.*;

/**
 * @author lin
 * @date 2024年08月30日 17:03
 */
public class BootNettyUdpSimpleChannelInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String message = packet.content().toString(CharsetUtil.UTF_8);
        publishToMqtt(message);
    }

    private MqttClient mqttClient;

    private void publishToMqtt(String message) {
        String brokerUrl = Boot.mqttBrokerUrl.getDefaultValue();
        String topic = Boot.mqttTopic.getDefaultValue();
        String clientId = Boot.mqttClientId.getDefaultValue();
        try {
            if (mqttClient == null || ! mqttClient.isConnected()) {
                mqttClient = new MqttClient(brokerUrl, clientId);
                mqttClient.connect();
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection lost");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println("Topic:" + topic);
                        byte[] payload = message.getPayload();
                        System.out.println("Message Arrived:" + new String(payload));
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("Message delivered");
                    }
                });
            }
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttClient.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
