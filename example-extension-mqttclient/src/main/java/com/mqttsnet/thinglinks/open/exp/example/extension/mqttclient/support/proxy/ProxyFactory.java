package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.proxy;


import java.util.List;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttConstant;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Interceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.AssertUtils;

/**
 * 代理工厂接口
 * @author mqttsnet
 */
public interface ProxyFactory {

    /**
     * 获取一个代理对象
     *
     * @param target       目标对象
     * @param interceptors 拦截器集合
     * @return 代理对象
     */
    Object getProxy(Object target, List<Interceptor> interceptors);

    /**
     * 获取代理类别
     *
     * @return 代理类别
     */
    String getProxyType();

    /**
     * 判断对象是否是代理对象
     *
     * @param object 对象
     * @return 是否是代理对象
     */
    static boolean isProxyObject(Object object) {
        AssertUtils.notNull(object, "object is null");
        return object.getClass().getName().contains(MqttConstant.CGLIB_CONTAIN_CONTENT) || object.getClass().getName().contains(MqttConstant.JDK_PROXY_CONTAIN_CONTENT);
    }
}
