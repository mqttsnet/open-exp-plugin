package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin;

/**
 * 拦截器接口
 * @author mqttsnet
 */
public interface Interceptor {

    /**
     * 拦截某个方法
     *
     * @param invocation 方法调用器
     * @return 方法返回值
     * @throws Throwable 异常
     */
    Object intercept(Invocation invocation) throws Throwable;
}
