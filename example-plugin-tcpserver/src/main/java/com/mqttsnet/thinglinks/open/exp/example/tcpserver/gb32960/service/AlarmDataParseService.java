package com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service;

/**
 * -----------------------------------------------------------------------------
 * File Name: AlarmDataParseService
 * -----------------------------------------------------------------------------
 * Description:
 * 告警Server
 * -----------------------------------------------------------------------------
 *
 * @author xiaonannet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2024/9/8       xiaonannet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2024/9/8 18:58
 */
public interface AlarmDataParseService {
    /**
     * 告警数据解析并返回数据
     *
     * @param readDatas
     */
    String alarmDataParseAndPushData(String readDatas) throws Exception;
}
