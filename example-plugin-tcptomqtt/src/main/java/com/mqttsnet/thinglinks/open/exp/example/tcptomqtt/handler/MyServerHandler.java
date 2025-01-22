package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.handler;

import java.util.HashMap;
import java.util.Map;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.MyComponent;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.utils.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mqttsnet
 * @date 2024年11月29日 17:32
 */
@Slf4j
@Component
public class MyServerHandler extends SimpleChannelInboundHandler<String> {


    // 用于存储每个设备的 TCP ChannelId 和 MQTT 客户端的映射关系
    private static final Map<ChannelId, String> channelToDeviceIdMap = new HashMap<>();
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 客户端连接成功
        log.info("客户端连接成功: {}", ctx.channel().remoteAddress());
        channelGroup.add(ctx.channel());  // 将连接的 TCP 通道添加到 ChannelGroup
        // 将设备id和通道id进行映射（假设使用通道的ID作为设备ID）
        channelToDeviceIdMap.put(ctx.channel().id(), ctx.channel().id().asLongText());

        // 发布设备上线的 MQTT 消息
        sendDeviceStatusUpdate(ctx.channel().id(), "ONLINE");

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 客户端断开连接
        log.info("客户端断开连接: {}", ctx.channel().remoteAddress());
        channelGroup.remove(ctx.channel());  // 从 ChannelGroup 中移除断开连接的 TCP 通道
        // 发布设备离线的 MQTT 消息
        sendDeviceStatusUpdate(ctx.channel().id(), "OFFLINE");
        channelToDeviceIdMap.remove(ctx.channel().id());  // 清除映射关系

        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("收到客户端消息: {}", msg);
        // 发布消息到 MQTT，使用全局的 MQTT 客户端
       /* String deviceId = channelToDeviceIdMap.get(ctx.channel().id());
        MyComponent myComponent = SpringUtils.getBean(MyComponent.class);
        if (deviceId != null && myComponent.getMqttClient() != null && myComponent.getMqttClient().isConnected()) {
            publishToMqtt(deviceId, msg);  // 将消息发布到 MQTT
        } else {
            log.warn("MQTT 客户端未连接，无法发布消息");
        }*/
    }

    /**
     * 发布设备状态更新消息到 MQTT
     */
    private void sendDeviceStatusUpdate(ChannelId channelId, String status) {
        String deviceId = channelToDeviceIdMap.get(channelId);
       /* MyComponent myComponent = SpringUtils.getBean(MyComponent.class);
        if (deviceId != null && myComponent.getMqttClient() != null && myComponent.getMqttClient().isConnected()) {
            String topic = "/v1/devices/" + deviceId + "/topo/update";
            String payload = String.format("{\"dataBody\":{\"deviceStatuses\":[{\"deviceId\":\"%s\",\"status\":\"%s\"}]}}", deviceId, status);
            myComponent.publishMessage(topic, payload, 0, false);
            log.info("设备状态已发布到 MQTT: {}", payload);
        } else {
            log.warn("MQTT 客户端未连接，无法继续操作");
        }*/
    }

    /**
     * 将消息发布到 MQTT
     */
    private void publishToMqtt(String deviceId, String message) {
        String topic = "/v1/devices/" + deviceId + "/command";
        String payload = String.format("{\"dataBody\":{\"command\":\"%s\"}}", message);
//        MyComponent myComponent = SpringUtils.getBean(MyComponent.class);
//        myComponent.publishMessage(topic, payload, 0, false);
        log.info("消息已发布到 MQTT: {}", message);
    }

    // 通过 ChannelId 获取 ChannelHandlerContext
    private ChannelHandlerContext getChannelHandlerContext(ChannelId channelId) {
        for (Channel channel : channelGroup) {
            if (channel.id().equals(channelId)) {
                return channel.pipeline().context(MyServerHandler.class);
            }
        }
        return null;
    }


}