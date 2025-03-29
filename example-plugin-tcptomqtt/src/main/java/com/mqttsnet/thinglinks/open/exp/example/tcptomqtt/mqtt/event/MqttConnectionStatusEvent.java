package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event;

import java.time.LocalDateTime;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * ============================================================================
 * Description:
 * MQTT连接状态变更事件
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
 * @date 2025/3/28 18:45
 */
@Getter
public class MqttConnectionStatusEvent extends ApplicationEvent {
    private final boolean connected;
    private final String serverURI;
    private final LocalDateTime eventTime;
    private final Throwable cause;

    /**
     * 构造连接成功事件
     *
     * @param source    事件源
     * @param connected 连接状态
     * @param serverURI 服务器地址
     */
    public MqttConnectionStatusEvent(Object source, boolean connected, String serverURI) {
        this(source, connected, serverURI, null);
    }

    /**
     * 构造连接异常事件
     *
     * @param source    事件源
     * @param connected 连接状态
     * @param serverURI 服务器地址
     * @param cause     异常原因
     */
    public MqttConnectionStatusEvent(Object source, boolean connected, String serverURI, Throwable cause) {
        super(source);
        this.connected = connected;
        this.serverURI = serverURI;
        this.eventTime = LocalDateTime.now();
        this.cause = cause;
    }

    /**
     * 获取状态描述
     */
    public String getStatus() {
        return connected ? "CONNECTED" : "DISCONNECTED";
    }
}
