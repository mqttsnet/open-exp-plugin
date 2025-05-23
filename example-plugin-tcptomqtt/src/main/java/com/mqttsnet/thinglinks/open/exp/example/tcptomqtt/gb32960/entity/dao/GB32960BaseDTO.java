package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao;

import java.util.Date;

import lombok.Data;

@Data
public class GB32960BaseDTO {

    /**
     * 车辆VIN
     */
    private String vin;
    /**
     * 电池包编码
     */
    private String batteryPackNumbers;
    /**
     * 整车数据
     */
    private GB32960VehicleStatus vehicleStatus;
    /**
     * 驱动电机数据
     */
    private GB32960DriveMotorStatus driveMotorStatus;

    private String fuelCellStatus;

    private String engineStatus;
    /**
     * 车辆位置数据
     */
    private GB32960LocationStatus locationStatus;
    /**
     * 极值数据
     */
    private GB32960ExtremeStatus extremeStatus;
    /**
     * 报警数据
     */
    private GB32960AlertStatus alertStatus;

    /**
     * 可充电储能装置电压数据
     */
    private GB32960EnergyStorageVoltageStatus energyStorageVoltageStatus;

    /**
     * 可充电储能装置温度数据
     */
    private GB32960EnergyStorageTemperatureStatus energyStorageTemperatureStatus;


    private GB32960CustomData customData;

    private Date acquisitionTime;

    private Long dataTime;

    private String command;


}