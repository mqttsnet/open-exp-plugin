package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao;

import java.util.List;

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
public class GB32960DriveMotorStatus {

    /**
     * 驱动电机个数
     */
    private Integer driveMotorCount;

    /**
     * 驱动电机总 成信息列表
     */
    private List<GB32960DriveMotors> driveMotors;
}