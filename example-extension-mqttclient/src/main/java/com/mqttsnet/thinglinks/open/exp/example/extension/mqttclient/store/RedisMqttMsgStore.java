package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.store;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttMsgDirection;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.msg.MqttMsg;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.AssertUtils;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.EmptyUtils;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.MqttUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * MQTT的Redis消息存储器
 * @author: zizai
 **/
public class RedisMqttMsgStore implements MqttMsgStore {


    /**
     * 获取redis发送消息的key，后面跟客户端ID
     */
    private static final String MQTT_SEND_MSG_REDIS_PREFIX = "mqtt-client:send-message:";
    /**
     * 获取redis接受消息的key，后面跟客户端ID
     */
    private static final String MQTT_RECEIVE_MSG_REDIS_PREFIX = "mqtt-client:receive-message:";

    private final JedisPool jedisPool;


    public RedisMqttMsgStore(JedisPool jedisPool) {
        AssertUtils.notNull(jedisPool, "jedisPool is null");
        this.jedisPool = jedisPool;
    }


    @Override
    public MqttMsg getMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            byte[] field = String.valueOf(msgId).getBytes(StandardCharsets.UTF_8);
            mqttMsg = MqttUtils.deserializableMsg(jedis.hget(key, field));
            return mqttMsg;
        }
    }

    @Override
    public void putMsg(MqttMsgDirection mqttMsgDirection, String clientId, MqttMsg mqttMsg) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsg, "mqttMsg is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            byte[] field = String.valueOf(mqttMsg.getMsgId()).getBytes(StandardCharsets.UTF_8);
            jedis.hset(key, field, MqttUtils.serializableMsg(mqttMsg));
        }
    }

    @Override
    public MqttMsg removeMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            byte[] field = String.valueOf(msgId).getBytes(StandardCharsets.UTF_8);
            mqttMsg = MqttUtils.deserializableMsg(jedis.hget(key, field));
            jedis.hdel(key, field);
            return mqttMsg;
        }
    }

    @Override
    public List<MqttMsg> getMsgList(MqttMsgDirection mqttMsgDirection, String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        List<MqttMsg> mqttMsgList = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            Map<byte[], byte[]> msgMap = jedis.hgetAll(key);
            if (EmptyUtils.isNotEmpty(msgMap)) {
                for (byte[] msgBytes : msgMap.values()) {
                    MqttMsg mqttMsg = MqttUtils.deserializableMsg(msgBytes);
                    mqttMsgList.add(mqttMsg);
                }
            }
        }
        return mqttMsgList;
    }


    @Override
    public void clearMsg(MqttMsgDirection mqttMsgDirection, String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        byte[] key = getKey(mqttMsgDirection, clientId);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }


    private byte[] getKey(MqttMsgDirection mqttMsgDirection, String clientId) {
        byte[] key;
        switch (mqttMsgDirection) {
            case SEND:
                key = (MQTT_SEND_MSG_REDIS_PREFIX + clientId).getBytes(StandardCharsets.UTF_8);
                break;
            case RECEIVE:
                key = (MQTT_RECEIVE_MSG_REDIS_PREFIX + clientId).getBytes(StandardCharsets.UTF_8);
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
        return key;
    }
}
