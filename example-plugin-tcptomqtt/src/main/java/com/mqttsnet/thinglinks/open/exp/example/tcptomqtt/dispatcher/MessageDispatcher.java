package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.dispatcher;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.Boot;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960MessageData;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.service.DataParseService;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.service.impl.DataParseServiceImpl;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.publisher.MqttEventPublisher;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.utils.SpringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * -----------------------------------------------------------------------------
 * File Name: MessageDispatcher
 * -----------------------------------------------------------------------------
 * Description:
 * 消息分发器，根据消息类型将消息转发到相应的处理器。
 * <p>
 * -----------------------------------------------------------------------------
 *
 * @author xiaonannet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2024/9/8       xiaonannet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2024/9/8 18:54
 */
@Slf4j
@Component
public class MessageDispatcher extends SimpleChannelInboundHandler<GB32960MessageData> {

    // 用于存储每个设备的 TCP ChannelId 和 MQTT 客户端的映射关系
    private static final Map<ChannelId, String> CHANNEL_TO_DEVICE_ID_MAP = new HashMap<>();
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 客户端连接成功
        log.info("客户端连接成功: {}", ctx.channel().remoteAddress());
        CHANNEL_GROUP.add(ctx.channel());  // 将连接的 TCP 通道添加到 ChannelGroup
        // 将设备id和通道id进行映射（假设使用通道的ID作为设备ID）
        CHANNEL_TO_DEVICE_ID_MAP.put(ctx.channel().id(), ctx.channel().id().asLongText());

        // 发布设备上线的 MQTT 消息
        sendDeviceStatusUpdate(ctx.channel().id(), "ONLINE");

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 客户端断开连接
        log.info("客户端断开连接: {}", ctx.channel().remoteAddress());
        CHANNEL_GROUP.remove(ctx.channel());  // 从 ChannelGroup 中移除断开连接的 TCP 通道
        // 发布设备离线的 MQTT 消息
        sendDeviceStatusUpdate(ctx.channel().id(), "OFFLINE");
        CHANNEL_TO_DEVICE_ID_MAP.remove(ctx.channel().id());  // 清除映射关系

        super.channelInactive(ctx);
    }


    /**
     * 发布设备状态更新消息到 MQTT
     */
    private void sendDeviceStatusUpdate(ChannelId channelId, String status) {
        String deviceId = CHANNEL_TO_DEVICE_ID_MAP.get(channelId);
        String topic = Boot.mqttClientTopic.getDefaultValue();
        String payload = String.format("{\"dataBody\":{\"deviceStatuses\":[{\"deviceId\":\"%s\",\"status\":\"%s\"}]}}", deviceId, status);

        MqttEventPublisher mqttEventPublisher = SpringUtils.getBean(MqttEventPublisher.class);
        mqttEventPublisher.publishMqttMessageEvent(topic, payload.getBytes(), MqttQoS.AT_LEAST_ONCE, false);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GB32960MessageData msg) throws Exception {
        DataParseService dataParseService = SpringUtils.getBean(DataParseServiceImpl.class);
        String msgCommand = msg.getMsgCommand();
        String response = "";

        // 根据消息命令分发到不同的处理方法
        switch (msgCommand) {
            case "01": // 处理车辆登录消息
                response = dataParseService.handleVehicleLogin(msg);
                break;
            case "02": // 处理实时信息上报
                response = dataParseService.handleRealtimeData(msg);
                break;
            case "03": // 处理补发信息上报
                response = dataParseService.handleSupplementaryData(msg);
                break;
            case "04": // 处理车辆登出
                response = dataParseService.handleVehicleLogout(msg);
                break;
            case "05": // 处理平台登录
                response = dataParseService.handlePlatformLogin(msg);
                break;
            case "06": // 处理平台登出
                response = dataParseService.handlePlatformLogout(msg);
                break;
            case "07": // 处理心跳
                response = dataParseService.handleHeartbeat(msg);
                break;
            case "08": // 同步校时
                response = dataParseService.handleTimeSynchronization(msg);
                break;
            default:
                log.warn("未处理的 GB32960 消息类型: {}", msgCommand);
                break;
        }

        // 如果有响应数据，返回给客户端
        if (StrUtil.isNotBlank(response)) {
            ctx.writeAndFlush(response);
        }
    }


}
