package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.interceptor;

import java.lang.reflect.Method;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.MqttClient;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Interceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Intercepts;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Invocation;
import lombok.extern.slf4j.Slf4j;

/**
 * -----------------------------------------------------------------------------
 * File Name: MqttSubscriptionInterceptor
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * -----------------------------------------------------------------------------
 *
 * @author xiaonannet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2025/1/21       xiaonannet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2025/1/21 18:24
 */
@Slf4j
@Intercepts(type = {MqttClient.class})
public class MqttSubscriptionInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Method method = invocation.getMethod();

        // 执行原方法
        Object result = invocation.proceed();

        // 如果是连接成功后，订阅 topic
        if ("connect".equals(method.getName())) {
            // 获取MqttClient对象，进行订阅操作
            MqttClient mqttClient = (MqttClient) invocation.getTarget();
            subscribeToTopic(mqttClient);
        }

        return result;
    }

    private void subscribeToTopic(MqttClient mqttClient) {
        log.info("Subscribing to topic...{}", mqttClient.toString());

    }
}
