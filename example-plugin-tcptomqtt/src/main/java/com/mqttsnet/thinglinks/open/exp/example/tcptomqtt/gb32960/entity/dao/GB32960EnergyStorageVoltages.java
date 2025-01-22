package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao;

import java.util.List;

import lombok.Data;

@Data
public class GB32960EnergyStorageVoltages {

    private Integer energyStorageSubSystemIndex;
    private Double energyStorageVoltage;
    private Double energyStorageCurrent;
    private Integer cellCount;
    private Integer frameCellStartIndex;
    private Integer frameCellCount;
    private List<Double> cellVoltages;

}