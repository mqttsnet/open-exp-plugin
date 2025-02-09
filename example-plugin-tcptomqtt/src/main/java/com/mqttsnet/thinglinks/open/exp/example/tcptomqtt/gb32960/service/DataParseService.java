package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.service;


import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960MessageData;

/**
 * @Description: GB32960数据解析服务接口。
 * @Author: mqttsnet
 * @E-mail: mqttsnet@163.com
 * @Website: https://www.mqttsnet.com
 * @CreateDate: 2024/09/10$ 18:30$
 * @UpdateUser: mqttsnet
 * @UpdateDate: 2024/09/10$ 18:30$
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */

public interface DataParseService {
    /**
     * 处理车辆登录消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handleVehicleLogin(GB32960MessageData msg);

    /**
     * 处理实时信息上报
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handleRealtimeData(GB32960MessageData msg);

    /**
     * 处理补发信息上报
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handleSupplementaryData(GB32960MessageData msg);

    /**
     * 处理车辆登出消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handleVehicleLogout(GB32960MessageData msg);

    /**
     * 处理平台登录消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handlePlatformLogin(GB32960MessageData msg);

    /**
     * 处理平台登出消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handlePlatformLogout(GB32960MessageData msg);

    /**
     * 处理心跳消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handleHeartbeat(GB32960MessageData msg);

    /**
     * 处理同步校时消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    String handleTimeSynchronization(GB32960MessageData msg);
}
