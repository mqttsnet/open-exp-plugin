package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao;

import lombok.Data;

/**
 * @Description:
 * @Author: mqttsnet
 * @E-mail: mqttsnet@163.com
 * @CreateDate: 2021/11/5$ 16:33$
 * @UpdateUser: mqttsnet
 * @UpdateDate: 2021/11/5$ 16:33$
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class GB32960TransmissionStatus {

    /**
     * 档位
     */
    private String gear;

    /**
     * 有驱动力
     */
    private boolean hasDriverForce;

    /**
     * 有制动力
     */
    private boolean hasBrakingForce;

}