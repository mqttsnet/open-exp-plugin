package com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.handler;

import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service.DataParseService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * -----------------------------------------------------------------------------
 * File Name: RealTimeDataHandler
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * 实时数据处理器
 * 用于处理 GB32960 协议的实时数据。
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
 * @date 2024/9/8 18:51
 */
@Slf4j
public class RealTimeDataHandler extends ChannelInboundHandlerAdapter {

    private final DataParseService parseService;

    public RealTimeDataHandler(DataParseService parseService) {
        this.parseService = parseService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            String msgStr = (String) msg;
            log.info("[GB32960] Received data: {}", msgStr);
            parseService.realTimeDataParseAndPushData(msgStr);
            // 响应客户端
            ctx.writeAndFlush(Unpooled.copiedBuffer("RealTimeDataProcessed", CharsetUtil.UTF_8));
        } else {
            log.warn("Unsupported message type: {}", msg.getClass().getSimpleName());
        }
    }
}
