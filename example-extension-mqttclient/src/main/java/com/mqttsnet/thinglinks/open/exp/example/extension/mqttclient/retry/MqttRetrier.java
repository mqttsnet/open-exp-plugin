package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.retry;

import java.util.concurrent.TimeUnit;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttConnectParameter;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.future.MqttFuture;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.AssertUtils;
import io.netty.channel.EventLoopGroup;

/**
 * MQTT消息重试器
 * @author mqttsnet
 */
public class MqttRetrier {

    /**
     * 线程组
     */
    private final EventLoopGroup eventLoopGroup;

    private final MqttConnectParameter mqttConnectParameter;

    public MqttRetrier(MqttConnectParameter mqttConnectParameter,EventLoopGroup eventLoopGroup) {
        AssertUtils.notNull(mqttConnectParameter, "mqttConnectParameter is null");
        AssertUtils.notNull(eventLoopGroup, "eventLoopGroup is null");
        this.mqttConnectParameter = mqttConnectParameter;
        this.eventLoopGroup = eventLoopGroup;
    }

    /**
     * 重试
     * @param msgFuture Future，用来判断是否完成
     * @param intervalMills 间隔毫秒
     * @param task 要执行的任务
     * @param nowExecute 是否立即执行
     */
    public void retry(MqttFuture msgFuture, long intervalMills, Runnable task, boolean nowExecute) {
        if (!eventLoopGroup.isShutdown()) {
            if (nowExecute) {
                eventLoopGroup.execute(task);
            }
            eventLoopGroup.schedule(() -> {
                //如果没有完成，则执行任务
                if (!msgFuture.isDone()) {
                    task.run();
                    //下一次执行间隔
                    long nextDelayMills = intervalMills + mqttConnectParameter.getRetryIntervalIncreaseMillis();
                    if (nextDelayMills > mqttConnectParameter.getRetryIntervalMaxMillis()) {
                        nextDelayMills = mqttConnectParameter.getRetryIntervalMaxMillis();
                    }
                    retry(msgFuture, nextDelayMills, task, false);
                }
            }, intervalMills, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 重试
     * @param msgFuture Future，用来判断是否完成
     * @param task 要执行的任务
     * @param nowExecute 是否立即执行
     */
    public void retry(MqttFuture msgFuture,Runnable task, boolean nowExecute) {
        this.retry(msgFuture,mqttConnectParameter.getRetryIntervalMillis(),task,nowExecute);
    }

}
