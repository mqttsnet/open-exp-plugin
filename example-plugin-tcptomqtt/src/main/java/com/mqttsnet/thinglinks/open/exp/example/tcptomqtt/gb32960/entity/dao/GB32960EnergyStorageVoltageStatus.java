package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao;

import java.util.List;

import lombok.Data;

/**
 * @Description: 可充电储能装置电压数据
 * @Author: mqttsnet
 * @E-mail: mqttsnet@163.com
 * @CreateDate: 2021/11/5$ 16:33$
 * @UpdateUser: mqttsnet
 * @UpdateDate: 2021/11/5$ 16:33$
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class GB32960EnergyStorageVoltageStatus {

    private Integer subSystemOfEnergyStorageCount;
    private List<GB32960EnergyStorageVoltages> energyStorageVoltages;

}