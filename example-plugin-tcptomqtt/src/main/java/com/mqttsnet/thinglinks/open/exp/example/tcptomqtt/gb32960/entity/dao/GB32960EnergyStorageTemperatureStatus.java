package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao;

import java.util.List;

import lombok.Data;

/**
 * @author sunshihuan
 * 可充电储能装置温度数据
 */
@Data
public class GB32960EnergyStorageTemperatureStatus {

    private int subEnergyStorageSystemCount;

    private List<GB32960EnergyStorageTemperatures> energyStorageTemperatures;
}