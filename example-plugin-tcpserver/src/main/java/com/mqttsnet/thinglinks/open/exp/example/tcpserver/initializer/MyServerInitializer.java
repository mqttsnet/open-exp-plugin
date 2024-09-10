package com.mqttsnet.thinglinks.open.exp.example.tcpserver.initializer;

import com.mqttsnet.thinglinks.open.exp.example.tcpserver.decoder.GB32960Decoder;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.dispatcher.MessageDispatcher;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.handler.ExceptionHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * -----------------------------------------------------------------------------
 * File Name: MyServerInitializer
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * 初始化服务器通道，配置管道中各个处理器。
 * 采用定界符方案来解决 TCP 粘包和拆包问题。
 * </p>
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
 * @date 2024/9/8 18:48
 */

@Slf4j
@Component
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        log.info("Initializing channel for client: {}", ch.remoteAddress());

        // 添加日志处理器，用于记录入站和出站事件
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));

        // 添加空闲检测处理器，检测是否有读写超时
        pipeline.addLast(new IdleStateHandler(60, 0, 0));

        // 添加 GB32960 协议解码器，解决粘包和拆包问题
        pipeline.addLast(new GB32960Decoder());

        // 添加业务逻辑处理器，处理解码后的消息
        pipeline.addLast(new MessageDispatcher());

        // 添加异常处理器
        pipeline.addLast(new ExceptionHandler());
    }
}

