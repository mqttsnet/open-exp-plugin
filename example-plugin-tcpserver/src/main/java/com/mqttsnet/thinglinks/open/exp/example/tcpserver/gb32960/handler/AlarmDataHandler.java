package com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.handler;

import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service.AlarmDataParseService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 告警数据处理器
 * 用于处理 GB32960 协议的告警数据。
 *
 * @author xiaonannet
 */
@Slf4j
public class AlarmDataHandler extends ChannelInboundHandlerAdapter {

    private final AlarmDataParseService alarmService;

    public AlarmDataHandler(AlarmDataParseService alarmService) {
        this.alarmService = alarmService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            String msgStr = (String) msg;
            log.info("[GB32960-Alarm] Received data: {}", msgStr);
            alarmService.alarmDataParseAndPushData(msgStr);
            // 响应客户端
            ctx.writeAndFlush(Unpooled.copiedBuffer("AlarmDataProcessed", CharsetUtil.UTF_8));
        } else {
            log.warn("Unsupported message type: {}", msg.getClass().getSimpleName());
        }
    }
}
