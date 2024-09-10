package com.mqttsnet.thinglinks.open.exp.example.tcpserver.initializer;

import com.mqttsnet.thinglinks.open.exp.example.tcpserver.decoder.GB32960Decoder;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.dispatcher.MessageDispatcher;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.encoder.EncoderHandler;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.handler.AlarmDataHandler;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.handler.RealTimeDataHandler;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service.AlarmDataParseService;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service.GB32960DataParseService;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.handler.ExceptionHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GB32960DataParseService gb32960Service;

    @Autowired
    private AlarmDataParseService alarmService;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        log.info("Initializing channel for client: {}", ch.remoteAddress());

        // 添加日志处理器，用于记录入站和出站事件
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));

        // 添加帧解码器以防止粘包、拆包问题（可以根据协议的具体需求调整）
//        pipeline.addLast(new LengthFieldBasedFrameDecoder(
//                Integer.MAX_VALUE, 0, 4, 0, 4));  // 假设前4字节是消息长度

        // 采用定界符方案处理消息拆包和粘包，使用换行符作为消息的结束标志
        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));


        // 添加 GB32960 协议解码器
        pipeline.addLast(new GB32960Decoder());


        // 添加自定义协议解码器
//        pipeline.addLast(new CustomProtocolDecoder());

        // 添加通用解码器
//        pipeline.addLast(new DecoderHandler());

        // 添加通用编码器
        pipeline.addLast(new EncoderHandler());

        // 添加业务逻辑处理器
        MessageDispatcher dispatcher = new MessageDispatcher();

        // 注册不同消息类型的处理器 start

        // 注册 GB32960 协议处理器
        // 实时数据处理
        dispatcher.registerHandler("GB32960-RealTimeData", new RealTimeDataHandler(gb32960Service));
        // 报警数据处理
        dispatcher.registerHandler("GB32960-AlarmData", new AlarmDataHandler(alarmService));

        // 注册不同消息类型的处理器 end

        // 将消息分发器添加到管道中
        pipeline.addLast(dispatcher);

        // 添加异常处理器
        pipeline.addLast(new ExceptionHandler());  // 异常处理器
    }
}

