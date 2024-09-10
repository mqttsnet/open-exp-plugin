package com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.entity.dao;
import lombok.Data;

import java.util.List;

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

    private Integer driveMotorCount;
    private List<GB32960DriveMotors> driveMotors;
}