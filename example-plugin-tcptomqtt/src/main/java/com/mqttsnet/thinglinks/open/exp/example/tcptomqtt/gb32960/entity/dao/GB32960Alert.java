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
public class GB32960Alert {

    private boolean driveMotorControllerTemperatureAlert;
    private boolean highVoltageInterlockStatusAlert;
    private boolean driveMotorTemperatureAlert;
    private boolean energyStorageOvercharge;
    private boolean soclowAlert;
    private boolean socjumpAlert;
    private boolean temperatureDifferenceAlert;
    private boolean cellHighTemperatureAlert;
    private boolean energyStorageOverVoltageAlert;
    private boolean energyStorageUnderVoltageAlert;
    private boolean brakingSystemAlert;
    private boolean cellOverVoltageAlert;
    private boolean cellUnderVoltageAlert;
    private boolean insulationAlert;
    private boolean dctemperatureAlert;
    private boolean energyStorageSystemNotMatchAlert;
    private boolean cellConsistencyDifferenceAlert;
    private boolean dcstatusAlert;
    private boolean sochighAlert;

}