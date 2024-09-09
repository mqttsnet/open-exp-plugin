package com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service;

/**
 * @Description: gb32960实时数据解析接口
 * @Author: mqttsnet
 * @E-mail: mqttsnet@163.com
 * @Website: https://www.mqttsnet.com
 * @CreateDate: 2021/11/15$ 18:30$
 * @UpdateUser: mqttsnet
 * @UpdateDate: 2021/11/15$ 18:30$
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */

public interface GB32960DataParseService {

    /**
     * 实时数据解析并返回数据
     *
     * @param readDatas
     */
    String realTimeDataParseAndPushData(String readDatas) throws Exception;
}
