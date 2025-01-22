package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.proxy;

import java.util.List;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttConstant;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.CglibMethodInterceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.CglibTargetHelper;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Interceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.EmptyUtils;
import net.sf.cglib.proxy.Enhancer;

/**
 * cglib代理工厂
 * @author mqttsnet
 */
public class CglibProxyFactory implements ProxyFactory {
    @Override
    public Object getProxy(Object target, List<Interceptor> interceptors) {
        Object obj = target;
        if (target == null || EmptyUtils.isEmpty(interceptors)) {
            return obj;
        }else {
            //创建一个Enhancer
            Enhancer enhancer = new Enhancer();
            //设置父类的class
            enhancer.setSuperclass(CglibTargetHelper.createCglibTarget(target).getClass());
            //设置回调时拦截器
            enhancer.setCallback(new CglibMethodInterceptor(interceptors, target));
            //创建代理对象
            obj = enhancer.create();
        }
        return obj;
    }

    @Override
    public String getProxyType() {
        return MqttConstant.PROXY_TYPE_CGLIB;
    }
}
