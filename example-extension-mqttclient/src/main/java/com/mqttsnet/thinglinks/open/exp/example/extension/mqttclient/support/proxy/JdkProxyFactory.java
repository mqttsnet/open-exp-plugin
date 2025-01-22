package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.proxy;


import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttConstant;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Interceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.JdkMethodInterceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.EmptyUtils;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.ReflectionUtils;

/**
 * JDK动态代理工厂
 * @author mqttsnet
 */
public class JdkProxyFactory implements ProxyFactory {

    /**
     * 对象接口缓存
     */
    private static final Map<Class, Class[]> interfacesMap = new ConcurrentHashMap<>();

    @Override
    public Object getProxy(Object target, List<Interceptor> interceptors) {
        if (target == null || EmptyUtils.isEmpty(interceptors)) {
            return target;
        } else {
            //创建代理对象
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), getInterfaces(target.getClass()), new JdkMethodInterceptor(interceptors, target));
        }
    }

    @Override
    public String getProxyType() {
        return MqttConstant.PROXY_TYPE_JDK;
    }

    /**
     * 获取类的所有接口
     *
     * @param clazz 类信息
     * @return 所有接口
     */
    private Class<?>[] getInterfaces(Class<?> clazz) {
        //从缓存获取
        Class[] interfaces = interfacesMap.get(clazz);
        if (interfaces == null) {
            //反射工具类获取所有接口
            interfaces = ReflectionUtils.getAllInterfaces(clazz);
            //添加到缓存中
            Class[] result = interfacesMap.putIfAbsent(clazz, interfaces);
            if (result != null) {
                interfaces = result;
            }
        }
        return interfaces;
    }
}
