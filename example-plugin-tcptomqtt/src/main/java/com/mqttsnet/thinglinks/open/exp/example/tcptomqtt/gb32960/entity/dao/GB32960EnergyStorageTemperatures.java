package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao;

import java.util.List;

import lombok.Data;


@Data
public class GB32960EnergyStorageTemperatures {

    private Integer energyStorageSubSystemIndex1;
    private Integer probeCount;
    private List<Integer> cellTemperatures;

}