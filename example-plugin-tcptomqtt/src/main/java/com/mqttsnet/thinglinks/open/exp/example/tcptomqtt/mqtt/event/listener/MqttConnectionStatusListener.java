package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.listener;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.MqttConnectionStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * Description:
 * 连接状态监听器
 * ============================================================================
 *
 * @author Sun Shihuan
 * @version 1.0.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2025/3/28      Sun Shihuan        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2025/3/28 18:46
 */
@Slf4j
@Component
@Order(0) // 最高优先级
public class MqttConnectionStatusListener implements ApplicationListener<MqttConnectionStatusEvent> {

    @Override
    public void onApplicationEvent(MqttConnectionStatusEvent event) {
        if (event.isConnected()) {
            log.info("成功连接MQTT服务器 [URI:{}]", event.getServerURI());
            // 可在此触发重订阅等操作
        } else {
            log.error("连接异常断开 [URI:{}] 原因:{}",
                    event.getServerURI(),
                    event.getCause() != null ? event.getCause().getMessage() : "未知"
            );
            // 可在此触发重连策略
        }
    }
}