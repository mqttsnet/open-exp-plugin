package com.mqttsnet.thinglinks.open.exp.example.tcpserver.dispatcher;

import cn.hutool.core.util.StrUtil;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.entity.dao.GB32960MessageData;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service.GB32960DataParseService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GB32960DataParseService dataParseService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GB32960MessageData msg) throws Exception {
//        GB32960DataParseService dataParseService = SpringUtils.getBean(GB32960DataParseService.class);
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
