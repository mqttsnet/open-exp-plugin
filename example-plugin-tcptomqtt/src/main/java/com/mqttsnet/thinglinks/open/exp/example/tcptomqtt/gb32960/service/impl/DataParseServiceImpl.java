package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.service.impl;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.Boot;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960AlertStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960BaseDTO;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960DriveMotorStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960DriveMotors;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960EnergyStorageTemperatureStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960EnergyStorageTemperatures;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960EnergyStorageVoltageStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960EnergyStorageVoltages;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960ExtremeStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960LocateStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960LocationStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960MessageData;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960TransmissionStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960VehicleStatus;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.service.DataParseService;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.mqtt.event.publisher.MqttEventPublisher;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.utils.HexUtils;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.utils.SpringUtils;
import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.utils.SubStringUtil;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: gb32960实时数据解析实现
 * @Author: mqttsnet
 * @E-mail: mqttsnet@163.com
 * @Website: <a href="https://www.mqttsnet.com">mqttsnet</a>
 * @CreateDate: 2021/11/15$ 18:19$
 * @UpdateUser: mqttsnet
 * @UpdateDate: 2021/11/15$ 18:19$
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
@Service
public class DataParseServiceImpl implements DataParseService {

    /**
     * 处理心跳消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    @Override
    public String handleHeartbeat(GB32960MessageData msg) {
        log.info("【GB32960】处理心跳消息: {}", msg);
        //响应标识
        msg.setMsgResponse("01");
        //数据单元长度
        msg.setDataCellLength("0000");

        return generateResponse(msg);
    }

    @Override
    public String handleTimeSynchronization(GB32960MessageData msg) {
        log.info("【GB32960】处理终端校时数据: {}", msg);
        msg.setMsgResponse("01");
        msg.setDataCellLength("0006");

        return generateResponseWithTimestamp(msg);
    }

    /**
     * 处理平台登录消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    @Override
    public String handlePlatformLogin(GB32960MessageData msg) {
        log.info("【GB32960】处理平台登录消息: {}", msg);
        msg.setMsgResponse("01");
        msg.setDataCellLength("0006");

        return generateResponseWithTimestamp(msg);
    }

    /**
     * 处理平台登出消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    @Override
    public String handlePlatformLogout(GB32960MessageData msg) {
        log.info("【GB32960】处理平台登出: {}", msg);
        // 设置响应命令
        msg.setMsgResponse("01");
        // 假设数据单元长度固定为6字节（这里根据具体情况修改）
        msg.setDataCellLength("0006");

        return generateResponseWithTimestamp(msg);
    }

    /**
     * 处理车辆登录消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    @Override
    public String handleVehicleLogin(GB32960MessageData msg) {
        log.info("【GB32960】处理车辆登录消息: {}", msg);

        // 车辆唯一标识（设备唯一标识）
        String uniqueIdentifier = HexUtils.convertHexToString(msg.getUniqueIdentifier());

        // TODO 发送车辆上线 报文

        msg.setMsgResponse("01");
        msg.setDataCellLength("0006");
        return generateResponseWithTimestamp(msg);
    }

    /**
     * 处理车辆登出消息
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    @Override
    public String handleVehicleLogout(GB32960MessageData msg) {
        log.info("【GB32960】处理车辆登出消息: {}", msg);

        // 车辆唯一标识（设备唯一标识）
        String uniqueIdentifier = HexUtils.convertHexToString(msg.getUniqueIdentifier());

        // TODO 发送车辆离线 报文


        // 设置响应命令
        msg.setMsgResponse("01");
        // 假设数据单元长度固定为6字节（这里根据具体情况修改）
        msg.setDataCellLength("0006");

        return generateResponseWithTimestamp(msg);
    }

    /**
     * 处理实时信息上报
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    @Override
    public String handleRealtimeData(GB32960MessageData msg) {
        log.info("【GB32960】处理实时信息上报: {}", msg);
        // 设置响应命令
        msg.setMsgResponse("01");
        //数据单元长度
        msg.setDataCellLength("0006");

        parseRealTimeOrSupplementaryData(msg.clone());

        // 返回解析后的响应
        return generateResponseWithTimestamp(msg);
    }

    /**
     * 处理补发信息上报
     *
     * @param msg GB32960MessageData 消息对象
     * @return 处理后的响应数据字符串
     */
    @Override
    public String handleSupplementaryData(GB32960MessageData msg) {
        log.info("【GB32960】处理补发信息上报: {}", msg);
        // 设置响应命令
        msg.setMsgResponse("01");
        //数据单元长度
        msg.setDataCellLength("0006");

        parseRealTimeOrSupplementaryData(msg.clone());

        return generateResponseWithTimestamp(msg);
    }


    // 解析实时数据或补发数据
    private void parseRealTimeOrSupplementaryData(GB32960MessageData msg) {
        // 实时数据处理逻辑
        if ("02".equals(msg.getMsgCommand())) {
            // 处理实时数据
            handleRealtimeDataParsing(msg);
        }

        // 补发数据处理逻辑
        if ("03".equals(msg.getMsgCommand())) {
            // 处理补发数据
            handleSupplementaryDataParsing(msg);
        }
    }


    private void handleRealtimeDataParsing(GB32960MessageData msg) {
        log.info("处理实时数据: {}", msg.getData());

        GB32960BaseDTO gb32960BaseDTO = new GB32960BaseDTO();
        gb32960BaseDTO.setCommand("TERMINAL_VEHICLE_UPLOAD_REALTIME");
        gb32960BaseDTO.setVin(HexUtils.convertHexToString(msg.getUniqueIdentifier()));

        // 解析采集时间
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        String acquisitionTime = year + " " +
                HexUtils.hexStringToDecimal(SubStringUtil.subStr(msg.getData(), 6, 8)) + ":" +
                HexUtils.hexStringToDecimal(SubStringUtil.subStr(msg.getData(), 8, 10)) + ":" +
                HexUtils.hexStringToDecimal(SubStringUtil.subStr(msg.getData(), 10, 12));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
        try {
            Date date = simpleDateFormat.parse(acquisitionTime);
            long ts = date.getTime();
            gb32960BaseDTO.setDataTime(ts);
        } catch (Exception e) {
            log.error("时间解析错误: {}", acquisitionTime, e);
        }

        msg.setData(SubStringUtil.subStr(msg.getData(), 12, msg.getData().length()));

        // 整车数据处理
        if ("01".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
            parseVehicleStatus(gb32960BaseDTO, msg);
        }


        // 解析驱动电机数据
        if ("02".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
            parseDriveMotorStatus(gb32960BaseDTO, msg);
        }

        // 解析车辆位置数据
        if ("05".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
            parseLocationStatus(gb32960BaseDTO, msg);
        }

        // 解析极致数据
        if ("06".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
            parseExtremeStatus(gb32960BaseDTO, msg);
        }

        // 解析报警数据
        if ("07".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
            parseAlertStatus(gb32960BaseDTO, msg);
        }

        // 解析可充电储能装置电压数据
        if ("08".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
            parseEnergyStorageVoltageStatus(gb32960BaseDTO, msg);
        }

        // 解析可充电储能装置温度数据
        if ("09".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
            parseEnergyStorageTemperatureStatus(gb32960BaseDTO, msg);
        }

        processFinalData(gb32960BaseDTO);
    }


    private void handleSupplementaryDataParsing(GB32960MessageData msg) {
        log.info("处理补发数据: {}", msg.getData());

        GB32960BaseDTO gb32960BaseDTO = new GB32960BaseDTO();
        gb32960BaseDTO.setCommand("SUPPLEMENTARY_DATA");
        gb32960BaseDTO.setVin(HexUtils.convertHexToString(msg.getUniqueIdentifier()));

        // 解析补发数据的具体逻辑
        // 例如：解析补发数据中的时间、状态、参数等
        // 请根据补发数据的具体格式和需求添加相应的解析逻辑

        // TODO: 解析补发数据逻辑
        gb32960BaseDTO.setDataTime(System.currentTimeMillis()); // 示例设置时间

        // TODO: 将 gb32960BaseDTO 推送到 Kafka 或其他 API
        processFinalData(gb32960BaseDTO);
    }


    /**
     * 7.2.3.1 整车数据
     * 整车数据格式和定义见表9。
     *
     * @param gb32960BaseDTO
     * @param msg
     */
    private void parseVehicleStatus(GB32960BaseDTO gb32960BaseDTO, GB32960MessageData msg) {
        String data = msg.getData();
        //整车数据处理
        GB32960VehicleStatus vehicleStatus = new GB32960VehicleStatus();
        //车辆状态
        String vehicleStatusEngineStatus = SubStringUtil.subStr(data, 2, 4);
        switch (vehicleStatusEngineStatus) {
            case "01":
                vehicleStatus.setEngineStatus("STARTED");
                break;
            case "02":
                vehicleStatus.setEngineStatus("STOPPED");
                break;
            case "03":
                vehicleStatus.setEngineStatus("OTHER");
                break;
            case "FE":
                vehicleStatus.setEngineStatus("ERROR");
                break;
            case "FF":
                vehicleStatus.setEngineStatus("INVALID");
                break;
            default:
        }
        //充电状态
        String vehicleStatusChargingStatus = SubStringUtil.subStr(data, 4, 6);
        switch (vehicleStatusChargingStatus) {
            case "01":
                vehicleStatus.setChargingStatus("CHARGING_STOPPED");
                break;
            case "02":
                vehicleStatus.setChargingStatus("CHARGING_DRIVING");
                break;
            case "03":
                vehicleStatus.setChargingStatus("NO_CHARGING");
                break;
            case "04":
                vehicleStatus.setChargingStatus("NO_CHARGING");
                break;
            case "FE":
                vehicleStatus.setChargingStatus("ERROR");
                break;
            case "FF":
                vehicleStatus.setChargingStatus("INVALID");
                break;
            default:
        }
        //运行模式
        String vehicleStatusRunningModel = SubStringUtil.subStr(data, 6, 8);
        switch (vehicleStatusRunningModel) {
            case "01":
                vehicleStatus.setRunningModel("EV");
                break;
            case "02":
                vehicleStatus.setRunningModel("PHEV");
                break;
            case "03":
                vehicleStatus.setRunningModel("FV");
                break;
            case "FE":
                vehicleStatus.setRunningModel("ERROR");
                break;
            case "FF":
                vehicleStatus.setRunningModel("INVALID");
                break;
            default:
        }
        //车速
        Integer vehicleStatusSpeed = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 8, 12));
        Double speed = new BigDecimal(vehicleStatusSpeed).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
        vehicleStatus.setSpeed(speed);
        //累计里程
        Integer vehicleStatusMileage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 12, 20));
        Double mileage = new BigDecimal(vehicleStatusMileage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
        vehicleStatus.setMileage(mileage);
        //总电压
        Integer vehicleStatusVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 20, 24));
        Double voltage = new BigDecimal(vehicleStatusVoltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
        vehicleStatus.setVoltage(voltage);
        //总电流
        Integer vehicleStatusCurrent = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 24, 28).trim());
        Double current = new BigDecimal(vehicleStatusCurrent).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
        vehicleStatus.setCurrent(current);
        //SOC
        Integer vehicleStatusSoc = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 28, 30));
        vehicleStatus.setSoc(vehicleStatusSoc);
        //DC-DC状态
        String vehicleStatusDcStatus = SubStringUtil.subStr(data, 30, 32);
        switch (vehicleStatusDcStatus) {
            case "01":
                vehicleStatus.setDcStatus("NORMAL");
                break;
            case "02":
                vehicleStatus.setDcStatus("OFF");
                break;
            case "FE":
                vehicleStatus.setDcStatus("ERROR");
                break;
            case "FF":
                vehicleStatus.setDcStatus("INVALID");
                break;
            default:
        }

        // 档位处理
        GB32960TransmissionStatus transmissionStatus = new GB32960TransmissionStatus();
        // 获取 vehicleStatusTransmissionStatus 的十六进制值并转换为二进制
        String hexValue = SubStringUtil.subStr(data, 32, 34); // 假设 data 是你原始的数据
        String binaryValue = HexUtils.hexStringToBinaryString(hexValue); // 转换为二进制
        // 确保是 8 位二进制，如果不足 8 位，前补零
        while (binaryValue.length() < 8) {
            binaryValue = "0" + binaryValue;
        }

        // 解析各个状态位(从左到右依次解析)
        boolean hasDriveForce = binaryValue.charAt(2) == '1'; // Bit5 -> 有驱动力
        boolean hasBrakingForce = binaryValue.charAt(3) == '1'; // Bit4 -> 有制动力
        String gearPositionStr = "";

        // 提取挡位的二进制部分（最后4位）
        String gearPositionBinary = binaryValue.substring(4); // 获取挡位部分

        // 根据挡位的二进制值进行映射
        gearPositionStr = switch (gearPositionBinary) {
            case "0000" -> "空挡";
            case "0001" -> "1挡";
            case "0010" -> "2挡";
            case "0011" -> "3挡";
            case "0100" -> "4挡";
            case "0101" -> "5挡";
            case "0110" -> "6挡";
            case "1101" -> "倒挡";
            case "1110" -> "自动D挡";
            case "1111" -> "停车P挡";
            default -> "未知挡位";
        };
        transmissionStatus.setGear(gearPositionStr);
        transmissionStatus.setHasDriverForce(hasDriveForce);
        transmissionStatus.setHasBrakingForce(hasBrakingForce);
        vehicleStatus.setTransmissionStatus(transmissionStatus);
        //绝缘电阻
        Integer vehicleStatusInsulationResistance = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 34, 38));
        vehicleStatus.setInsulationResistance(vehicleStatusInsulationResistance);
        //预留位
        Integer yuliu = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 38, 42));
        gb32960BaseDTO.setVehicleStatus(vehicleStatus);
        // 42 之后的数据为可充电储能装置电压数据
        msg.setData(SubStringUtil.subStr(data, 42, data.length()));
    }


    /**
     * 7.2.3.2 驱动电机数据
     * 驱动电机数据格式和定义见表10
     *
     * @param gb32960BaseDTO
     * @param msg
     */
    private void parseDriveMotorStatus(GB32960BaseDTO gb32960BaseDTO, GB32960MessageData msg) {
        String data = msg.getData();
        // 驱动电机个数
        Integer driveMotorStatusDriveMotorCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 2, 4));

        // 设置驱动电机个数
        GB32960DriveMotorStatus driveMotorStatus = new GB32960DriveMotorStatus();
        driveMotorStatus.setDriveMotorCount(driveMotorStatusDriveMotorCount);

        // 每个驱动电机总成信息长度
        String driveMotorsLength = SubStringUtil.subStr(data, 4, 28 * driveMotorStatusDriveMotorCount);

        List<GB32960DriveMotors> driveMotorsList = new ArrayList<>();

        // 解析每个驱动电机的信息 (TODO 多个电机需多次验证, 2025-01-23)
        for (int i = 0; i < driveMotorStatusDriveMotorCount; i++) {
            GB32960DriveMotors driveMotors = new GB32960DriveMotors();
            String motorData = SubStringUtil.subStr(driveMotorsLength, 28 * i, driveMotorsLength.length());

            // 驱动电机序号
            Integer driveMotorsSn = HexUtils.hexStringToDecimal(SubStringUtil.subStrStart(motorData, 2));
            driveMotors.setSn(driveMotorsSn);

            // 驱动电机状态
            String driveMotorsDriveMotorPower = SubStringUtil.subStr(motorData, 2, 4);
            switch (driveMotorsDriveMotorPower) {
                case "01":
                    driveMotors.setDriveMotorPower("CONSUMING_POWER");
                    break;
                case "02":
                    driveMotors.setDriveMotorPower("GENERATING_POWER");
                    break;
                case "03":
                    driveMotors.setDriveMotorPower("CLOSED");
                    break;
                case "04":
                    driveMotors.setDriveMotorPower("PREPARING");
                    break;
                case "FE":
                    driveMotors.setDriveMotorPower("CLOSED");
                    break;
                case "FF":
                    driveMotors.setDriveMotorPower("INVALID");
                    break;
                default:
                    driveMotors.setDriveMotorPower("UNKNOWN");
            }

            // 驱动电机控制器温度
            Integer driveMotorsControllerTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(motorData, 4, 6));
            driveMotors.setControllerTemperature(driveMotorsControllerTemperature - 40);

            // 驱动电机转速
            Integer driveMotorsDriveMotorSpeed = HexUtils.hexStringToDecimal(SubStringUtil.subStr(motorData, 6, 10));
            driveMotors.setDriveMotorSpeed(driveMotorsDriveMotorSpeed - 20000);

            // 驱动电机转矩
            Integer driveMotorsDriveMotorTorque = HexUtils.hexStringToDecimal(SubStringUtil.subStr(motorData, 10, 14));
            Double driveMotorTorque = new BigDecimal(driveMotorsDriveMotorTorque).subtract(new BigDecimal(20000))
                    .divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
            driveMotors.setDriveMotorTorque(driveMotorTorque);

            // 驱动电机温度
            Integer driveMotorsDriveMotorTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(motorData, 14, 16));
            driveMotors.setDriveMotorTemperature(driveMotorsDriveMotorTemperature - 40);

            // 电机控制器输入电压
            Integer driveMotorsControllerInputVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(motorData, 16, 20));
            Double controllerInputVoltage = new BigDecimal(driveMotorsControllerInputVoltage)
                    .divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
            driveMotors.setControllerInputVoltage(controllerInputVoltage);

            // 电机控制器直流母线电流
            Integer driveMotorsDcBusCurrentOfController = HexUtils.hexStringToDecimal(SubStringUtil.subStr(motorData, 20, 24));
            Double dcBusCurrentOfController = new BigDecimal(driveMotorsDcBusCurrentOfController)
                    .subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
            driveMotors.setDcBusCurrentOfController(dcBusCurrentOfController);

            // 将每个驱动电机添加到列表
            driveMotorsList.add(driveMotors);
        }

        // 设置驱动电机信息列表
        driveMotorStatus.setDriveMotors(driveMotorsList);
        gb32960BaseDTO.setDriveMotorStatus(driveMotorStatus);

        // 更新数据
        msg.setData(SubStringUtil.subStr(data, driveMotorsLength.length() + 4, data.length()));
    }


    /**
     * 7.2.3.5 车辆位置数据
     * 车辆位置数据格式和定义见表14。
     *
     * @param gb32960BaseDTO
     * @param msg
     */
    private void parseLocationStatus(GB32960BaseDTO gb32960BaseDTO, GB32960MessageData msg) {
        String data = msg.getData();
        GB32960LocationStatus locationStatus = new GB32960LocationStatus();

        // 定位状态（使用二进制位解析）
        String locateStatusHex = SubStringUtil.subStr(data, 2, 4);  // 获取定位状态字节
        String locateStatusBinary = HexUtils.hexStringToBinaryString(locateStatusHex); // 转换为二进制字符串

        // 确保是8位二进制，如果不足8位，前补零
        while (locateStatusBinary.length() < 8) {
            locateStatusBinary = "0" + locateStatusBinary;
        }

        // 解析定位状态（从左到右解析每一位）
        String validation = (locateStatusBinary.charAt(0) == '0') ? "VALID" : "INVALID"; // 第0位：有效/无效定位
        String latitudeType = (locateStatusBinary.charAt(1) == '0') ? "NORTH" : "SOUTH";  // 第1位：纬度类型
        String longitudeType = (locateStatusBinary.charAt(2) == '0') ? "EAST" : "WEST";  // 第2位：经度类型

        // 创建定位状态对象
        GB32960LocateStatus locateStatus = new GB32960LocateStatus();
        locateStatus.setValidation(validation);
        locateStatus.setLatitudeType(latitudeType);
        locateStatus.setLongitudeType(longitudeType);
        locationStatus.setLocateStatus(locateStatus);

        // 解析经度（4字节，乘以10⁶）
        Integer longitude = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 4, 12));  // 经度数据
        locationStatus.setLongitude(new BigDecimal(longitude).movePointLeft(6).doubleValue());  // 转换为实际经度

        // 解析纬度（4字节，乘以10⁵）
        Integer latitude = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 12, 20));  // 纬度数据
        locationStatus.setLatitude(new BigDecimal(latitude).movePointLeft(6).doubleValue());  // 转换为实际纬度

        // 设置到 DTO 中
        gb32960BaseDTO.setLocationStatus(locationStatus);

        // 更新消息数据，去除已经解析过的数据
        msg.setData(SubStringUtil.subStr(data, 20, data.length()));
    }

    /**
     * 7.2.3.6 极值数据
     * 极值数据格式和定义见表16。
     *
     * @param gb32960BaseDTO
     * @param msg
     */
    private void parseExtremeStatus(GB32960BaseDTO gb32960BaseDTO, GB32960MessageData msg) {
        String extremeData = SubStringUtil.subStr(msg.getData(), 2, msg.getData().length());

        GB32960ExtremeStatus extremeStatus = new GB32960ExtremeStatus();

        // 最高电压电池子系统号
        Integer maxVoltageSubSystem = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 0, 2));
        extremeStatus.setSubSystemIndexOfMaxVoltage(maxVoltageSubSystem);

        // 最高电压电池单体代号
        Integer maxVoltageCellIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 2, 4));
        extremeStatus.setCellIndexOfMaxVoltage(maxVoltageCellIndex);

        // 电池单体电压最高值
        Integer maxVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 4, 8));
        extremeStatus.setCellMaxVoltage(new BigDecimal(maxVoltage).divide(new BigDecimal("1000")).setScale(3, BigDecimal.ROUND_UP).doubleValue());

        // 最低电压电池子系统号
        Integer minVoltageSubSystem = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 8, 10));
        extremeStatus.setSubSystemIndexOfMinVoltage(minVoltageSubSystem);

        // 最低电压电池单体代号
        Integer minVoltageCellIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 10, 12));
        extremeStatus.setCellIndexOfMinVoltage(minVoltageCellIndex);

        // 电池单体电压最低值
        Integer minVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 12, 16));
        extremeStatus.setCellMinVoltage(new BigDecimal(minVoltage).divide(new BigDecimal("1000")).setScale(3, BigDecimal.ROUND_UP).doubleValue());

        // 最高温度子系统号
        Integer maxTempSubSystem = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 16, 18));
        extremeStatus.setSubSystemIndexOfMaxTemperature(maxTempSubSystem);

        // 最高温度探针序号
        Integer maxTempProbeIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 18, 20));
        extremeStatus.setProbeIndexOfMaxTemperature(maxTempProbeIndex);

        // 最高温度值
        Integer maxTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 20, 22));
        extremeStatus.setMaxTemperature(maxTemperature - 40);

        // 最低温度子系统号
        Integer minTempSubSystem = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 22, 24));
        extremeStatus.setSubSystemIndexOfMinTemperature(minTempSubSystem);

        // 最低温度探针序号
        Integer minTempProbeIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 24, 26));
        extremeStatus.setProbeIndexOfMinTemperature(minTempProbeIndex);

        // 最低温度值
        Integer minTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(extremeData, 26, 28));
        extremeStatus.setMinTemperature(minTemperature - 40);

        gb32960BaseDTO.setExtremeStatus(extremeStatus);

        // 更新消息数据，去除已经解析过的数据
        msg.setData(SubStringUtil.subStr(extremeData, 28, extremeData.length()));
    }


    /**
     * 7.2.3.7 报警数据
     * 报警数据格式和定义见表17。
     *
     * @param gb32960BaseDTO
     * @param msg
     */
    private void parseAlertStatus(GB32960BaseDTO gb32960BaseDTO, GB32960MessageData msg) {
        String alertData = SubStringUtil.subStr(msg.getData(), 2, msg.getData().length());

        GB32960AlertStatus alertStatus = new GB32960AlertStatus();

        // 最高报警等级
        String highestAlertLevel = SubStringUtil.subStr(alertData, 0, 2);
        switch (highestAlertLevel) {
            case "00":
                alertStatus.setHighestAlertLevel("LEVEL_0");
                break;
            case "01":
                alertStatus.setHighestAlertLevel("LEVEL_1");
                break;
            case "02":
                alertStatus.setHighestAlertLevel("LEVEL_2");
                break;
            case "03":
                alertStatus.setHighestAlertLevel("LEVEL_3");
                break;
            case "FE":
                alertStatus.setHighestAlertLevel("CLOSED");
                break;
            case "FF":
                alertStatus.setHighestAlertLevel("INVALID");
                break;
            default:
                alertStatus.setHighestAlertLevel("UNKNOWN");
                break;
        }

        // 通用报警标识
        String universalAlarmIdentification = SubStringUtil.subStr(alertData, 2, 10);
        alertStatus.setUniversalAlarmIdentification(universalAlarmIdentification);

        // 可充电储能装置故障总数
        Integer energyStorageAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 10, 12));
        alertStatus.setEnergyStorageAlertCount(energyStorageAlertCount);

        int currentIndex = 12;
        if (energyStorageAlertCount > 0) {
            List<Integer> energyStorageAlertList = new ArrayList<>();
            for (int i = 0; i < energyStorageAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, currentIndex, currentIndex + 4));
                energyStorageAlertList.add(alertCode);
                currentIndex += 4; // 每个报警占 4 字节
            }
            alertStatus.setEnergyStorageAlertList(energyStorageAlertList);
        }

        // 驱动电机故障总数
        Integer driveMotorAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, currentIndex, currentIndex + 2));
        alertStatus.setDriveMotorAlertCount(driveMotorAlertCount);
        currentIndex += 2;

        if (driveMotorAlertCount > 0) {
            List<Integer> driveMotorAlertList = new ArrayList<>();
            for (int i = 0; i < driveMotorAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, currentIndex, currentIndex + 4));
                driveMotorAlertList.add(alertCode);
                currentIndex += 4; // 每个报警占 4 字节
            }
            alertStatus.setDriveMotorAlertList(driveMotorAlertList);
        }

        // 发动机故障总数
        Integer engineAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, currentIndex, currentIndex + 2));
        alertStatus.setEngineAlertCount(engineAlertCount);
        currentIndex += 2;

        if (engineAlertCount > 0) {
            List<Integer> engineAlertList = new ArrayList<>();
            for (int i = 0; i < engineAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, currentIndex, currentIndex + 4));
                engineAlertList.add(alertCode);
                currentIndex += 4; // 每个报警占 4 字节
            }
            alertStatus.setEngineAlertList(engineAlertList);
        }

        // 其他故障总数
        Integer otherAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, currentIndex, currentIndex + 2));
        alertStatus.setOtherAlertCount(otherAlertCount);
        currentIndex += 2;

        if (otherAlertCount > 0) {
            List<Integer> otherAlertList = new ArrayList<>();
            for (int i = 0; i < otherAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, currentIndex, currentIndex + 4));
                otherAlertList.add(alertCode);
                currentIndex += 4; // 每个报警占 4 字节
            }
            alertStatus.setOtherAlertList(otherAlertList);
        }

        gb32960BaseDTO.setAlertStatus(alertStatus);

        // 更新消息数据，去除已经解析过的数据
        msg.setData(SubStringUtil.subStr(alertData, currentIndex, alertData.length()));
    }


    /**
     * 可充电储能装置电压数据
     *
     * @param gb32960BaseDTO
     * @param msg
     */
    private void parseEnergyStorageVoltageStatus(GB32960BaseDTO gb32960BaseDTO, GB32960MessageData msg) {
        String voltageData = SubStringUtil.subStr(msg.getData(), 2, msg.getData().length());

        GB32960EnergyStorageVoltageStatus voltageStatus = new GB32960EnergyStorageVoltageStatus();

        // 获取子系统个数
        Integer subSystemCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, 0, 2));
        voltageStatus.setSubSystemOfEnergyStorageCount(subSystemCount);

        List<GB32960EnergyStorageVoltages> voltagesList = new ArrayList<>();
        int offset = 2;

        // 解析每个子系统的数据
        for (int i = 0; i < subSystemCount; i++) {
            GB32960EnergyStorageVoltages voltages = new GB32960EnergyStorageVoltages();

            // 子系统索引
            Integer subSystemIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 2));
            voltages.setEnergyStorageSubSystemIndex(subSystemIndex);
            offset += 2;

            // 储能电压
            Integer energyStorageVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setEnergyStorageVoltage(new BigDecimal(energyStorageVoltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue());
            offset += 4;

            // 储能电流
            Integer energyStorageCurrent = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setEnergyStorageCurrent(new BigDecimal(energyStorageCurrent).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue());
            offset += 4;

            // 电池数量
            Integer cellCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setCellCount(cellCount);
            offset += 4;

            // 电池框起始索引
            Integer frameCellStartIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setFrameCellStartIndex(frameCellStartIndex);
            offset += 4;

            // 电池框电池数量
            Integer frameCellCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 2));
            voltages.setFrameCellCount(frameCellCount);
            offset += 2;

            // 解析每个电池的电压
            List<Double> cellVoltages = new ArrayList<>();
            for (int j = 0; j < frameCellCount; j++) {
                Integer cellVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
                cellVoltages.add(new BigDecimal(cellVoltage).divide(new BigDecimal("1000")).setScale(3, BigDecimal.ROUND_UP).doubleValue());
                offset += 4;
            }
            voltages.setCellVoltages(cellVoltages);

            // 将解析的数据添加到列表
            voltagesList.add(voltages);
        }

        // 设置能量存储电压数据
        voltageStatus.setEnergyStorageVoltages(voltagesList);

        gb32960BaseDTO.setEnergyStorageVoltageStatus(voltageStatus);

        // 更新消息数据，去除已经解析过的数据
        msg.setData(SubStringUtil.subStr(voltageData, offset, voltageData.length()));
    }


    /**
     * 可充电储能装置温度数据
     *
     * @param gb32960BaseDTO
     * @param msg
     */
    private void parseEnergyStorageTemperatureStatus(GB32960BaseDTO gb32960BaseDTO, GB32960MessageData msg) {
        String temperatureData = SubStringUtil.subStr(msg.getData(), 2, msg.getData().length());

        GB32960EnergyStorageTemperatureStatus temperatureStatus = new GB32960EnergyStorageTemperatureStatus();

        // 获取子系统个数
        Integer subSystemCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, 0, 2));
        temperatureStatus.setSubEnergyStorageSystemCount(subSystemCount);

        List<GB32960EnergyStorageTemperatures> temperaturesList = new ArrayList<>();
        int offset = 2;

        // 解析每个子系统的数据
        for (int i = 0; i < subSystemCount; i++) {
            GB32960EnergyStorageTemperatures temperatures = new GB32960EnergyStorageTemperatures();

            // 子系统索引
            Integer subSystemIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, offset, offset + 2));
            temperatures.setEnergyStorageSubSystemIndex1(subSystemIndex);
            offset += 2;

            // 探针数量
            Integer probeCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, offset, offset + 4));
            temperatures.setProbeCount(probeCount);
            offset += 4;

            List<Integer> cellTemperatures = new ArrayList<>();
            for (int j = 0; j < probeCount; j++) {
                // 电池温度，减去40℃来转换为实际温度
                Integer cellTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, offset, offset + 2)) - 40;
                cellTemperatures.add(cellTemperature);
                offset += 2;
            }
            temperatures.setCellTemperatures(cellTemperatures);

            // 将解析的数据添加到列表
            temperaturesList.add(temperatures);
        }

        // 设置能量存储温度数据
        temperatureStatus.setEnergyStorageTemperatures(temperaturesList);

        gb32960BaseDTO.setEnergyStorageTemperatureStatus(temperatureStatus);

        // 更新消息数据，去除已经解析过的数据
        msg.setData(SubStringUtil.subStr(temperatureData, offset, temperatureData.length()));
    }


    /**
     * 处理最终的数据实体，推送到 Kafka 或调用 API
     *
     * @param data GB32960BaseDTO 数据对象
     */
    private void processFinalData(GB32960BaseDTO data) {
        // 提取 vehicleStatus 数据
        GB32960VehicleStatus vehicleStatus = data.getVehicleStatus();
        if (vehicleStatus == null) {
            log.warn("没有找到 'vehicleStatus' 数据");
            return;
        }

        // 提取相关字段
        String engineStatus = vehicleStatus.getEngineStatus();
        String runningModel = vehicleStatus.getRunningModel();
        String chargingStatus = vehicleStatus.getChargingStatus();
        Double speed = vehicleStatus.getSpeed();
        Double mileage = vehicleStatus.getMileage();
        Double voltage = vehicleStatus.getVoltage();
        Double current = vehicleStatus.getCurrent();
        Integer soc = vehicleStatus.getSoc();
        String dcStatus = vehicleStatus.getDcStatus();
        GB32960TransmissionStatus transmissionStatus = vehicleStatus.getTransmissionStatus();
        Integer insulationResistance = vehicleStatus.getInsulationResistance();
        Integer accelerationPedalTravel = vehicleStatus.getAccelerationPedalTravel();
        Integer brakePedalState = vehicleStatus.getBrakePedalState();

        // 构建服务数据
        JSONObject dataJson = new JSONObject();
        dataJson.set("engine_status", engineStatus);
        dataJson.set("running_model", runningModel);
        dataJson.set("charging_status", chargingStatus);
        dataJson.set("speed", speed != null ? speed : 0);
        dataJson.set("mileage", mileage != null ? mileage : 0);
        dataJson.set("voltage", voltage != null ? voltage : 0);
        dataJson.set("current", current != null ? current : 0);
        dataJson.set("soc", soc != null ? soc : 0);
        dataJson.set("dc_status", dcStatus != null ? dcStatus : "UNKNOWN");
        dataJson.set("transmission_status_gear", transmissionStatus != null ? transmissionStatus.getGear() : "UNKNOWN");
        dataJson.set("transmission_status_driver_force", transmissionStatus != null && transmissionStatus.isHasDriverForce());
        dataJson.set("transmission_status_braking_force", transmissionStatus != null && transmissionStatus.isHasBrakingForce());
        dataJson.set("insulation_resistance", insulationResistance != null ? insulationResistance : 0);
        dataJson.set("acceleration_pedal_travel", accelerationPedalTravel != null ? accelerationPedalTravel : 0);
        dataJson.set("brake_pedal_state", brakePedalState != null ? brakePedalState : 0);

        // 创建 device JSON 对象
        JSONObject deviceJson = new JSONObject();
        deviceJson.set("deviceId", data.getVin());
        deviceJson.set("services", new JSONArray()
                .put(new JSONObject()
                        .set("serviceCode", "vehicle_status")
                        .set("data", dataJson)
                        .set("eventTime", System.currentTimeMillis() * 1000000)  // 19位纳秒时间戳
                )
        );

        // 构建数据体
        JSONObject dataBodyJson = new JSONObject();
        dataBodyJson.set("devices", new JSONArray().put(deviceJson));

        // 构建 head 部分
        JSONObject headJson = new JSONObject();
        headJson.set("cipherFlag", 0);
        headJson.set("mid", 3342);
        headJson.set("timeStamp", System.currentTimeMillis());

        // 构建最终的 payload
        JSONObject payloadJson = new JSONObject();
        payloadJson.set("dataBody", dataBodyJson);
        payloadJson.set("dataSign", "");
        payloadJson.set("head", headJson);

        // 推送数据
        MqttEventPublisher mqttEventPublisher = SpringUtils.getBean(MqttEventPublisher.class);
        String topic = Boot.mqttClientDatasTopic.getDefaultValue();
        mqttEventPublisher.publishMqttPublishMessageEvent(topic, payloadJson.toString().getBytes(), MqttQoS.AT_LEAST_ONCE.value(), false);

        // Log 推送信息
        log.info("整车数据推送到 Kafka 或 API: {}", payloadJson.toString());
    }


    /**
     * 生成响应数据的通用方法
     *
     * @param msg GB32960MessageData 消息对象
     * @return 返回给客户端的响应数据
     */
    private String generateResponse(GB32960MessageData msg) {
        // 拼接需要计算校验码的数据内容
        StringBuilder checkCodeData = new StringBuilder()
                .append(msg.getMsgCommand())
                .append(msg.getMsgResponse())
                .append(msg.getUniqueIdentifier())
                .append(msg.getEncryption())
                .append(msg.getDataCellLength());

        // 计算校验码
        String checkCode = HexUtils.getBCC(checkCodeData.toString());
        msg.setCheckCode(checkCode);

        // 构建完整响应数据
        return new StringBuilder()
                .append(msg.getMsgHead())            // 消息头
                .append(msg.getMsgCommand())         // 命令标识
                .append(msg.getMsgResponse())        // 应答标识
                .append(msg.getUniqueIdentifier())   // 唯一识别码
                .append(msg.getEncryption())         // 加密方式
                .append(msg.getDataCellLength())     // 数据单元长度
                .append(msg.getCheckCode())          // 校验码
                .toString();
    }

    /**
     * 带时间戳的响应生成
     *
     * @param msg 消息对象
     * @return 拼接响应数据
     */
    private String generateResponseWithTimestamp(GB32960MessageData msg) {
        String data = msg.getData();
        //数据单元-时间获取 （TODO 处理为当前时间）
        StringBuilder timestamp = new StringBuilder().append(SubStringUtil.subStrStart(data, 12));

        StringBuilder checkCodeData = new StringBuilder()
                .append(msg.getMsgCommand())
                .append(msg.getMsgResponse())
                .append(msg.getUniqueIdentifier())
                .append(msg.getEncryption())
                .append(msg.getDataCellLength())
                .append(timestamp);

        msg.setCheckCode(HexUtils.getBCC(checkCodeData.toString()));

        return new StringBuilder()
                .append(msg.getMsgHead())
                .append(msg.getMsgCommand())
                .append(msg.getMsgResponse())
                .append(msg.getUniqueIdentifier())
                .append(msg.getEncryption())
                .append(msg.getDataCellLength())
                .append(timestamp)
                .append(msg.getCheckCode())
                .toString();
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String a = "4c464341483935570344d33303130303936";
        String newstr = new String(a.getBytes(Charset.defaultCharset()), "US-ASCII");
        String bcc = HexUtils.getBCC("4C46434148393657374D3330363032303101001E1508010A1228000A38393836303434393138313938303037363136370100");
        System.out.println(bcc);
    }


}
