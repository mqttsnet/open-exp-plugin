package cn.think.in.java.open.exp.example.tcptomqtt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

/**
 * @author lin
 * @date 2024年08月29日 17:32
 */
@Component
public class MyServerHandler extends SimpleChannelInboundHandler<String> {
    private MqttClient mqttClient;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("msg:" + msg);
        publishToMqtt(msg);
    }

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
