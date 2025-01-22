package com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service.impl;


import cn.hutool.json.JSONUtil;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.entity.dao.*;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.gb32960.service.DataParseService;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.utils.HexUtils;
import com.mqttsnet.thinglinks.open.exp.example.tcpserver.utils.SubStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

   /* @Autowired
    private RedisService redisService;*/

    /**
     * 实时数据解析并返回数据
     *
     * @param readDatas
     */
    @Override
    @Deprecated
    public String realTimeDataParseAndPushData(String readDatas) throws Exception {
        //心跳
        if ("07".equals(SubStringUtil.subStr(readDatas, 4, 6))) {
            GB32960MessageData gb32960MessageData = new GB32960MessageData();
            gb32960MessageData.setMsgHead(SubStringUtil.subStrStart(readDatas, 4));
            gb32960MessageData.setMsgCommand(SubStringUtil.subStr(readDatas, 4, 6));
            gb32960MessageData.setMsgResponse(SubStringUtil.subStr(readDatas, 6, 8));
            gb32960MessageData.setUniqueIdentifier(SubStringUtil.subStr(readDatas, 8, 42));
            gb32960MessageData.setEncryption(SubStringUtil.subStr(readDatas, 42, 44));
            gb32960MessageData.setDataCellLength(SubStringUtil.subStr(readDatas, 44, 48));
//            gb32960MessageData.setData(SubStringUtil.subStr(readDatas,48,-2));
            gb32960MessageData.setCheckCode(SubStringUtil.subStrEnd(readDatas, 2));
            log.info("GB32960心跳数据处理---->");
            //响应标识
            gb32960MessageData.setMsgResponse("01");
            //数据单元长度
            gb32960MessageData.setDataCellLength("0000");
            //校验码计算
            StringBuffer checkCodeData = new StringBuffer().append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength());
            gb32960MessageData.setCheckCode(HexUtils.getBCC(String.valueOf(checkCodeData)));
            StringBuffer pushData = new StringBuffer().append(gb32960MessageData.getMsgHead()).append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(gb32960MessageData.getCheckCode());
            return String.valueOf(pushData);
        }
        //终端校时
        if ("08".equals(SubStringUtil.subStr(readDatas, 4, 6))) {
            GB32960MessageData gb32960MessageData = new GB32960MessageData();
            gb32960MessageData.setMsgHead(SubStringUtil.subStrStart(readDatas, 4));
            gb32960MessageData.setMsgCommand(SubStringUtil.subStr(readDatas, 4, 6));
            gb32960MessageData.setMsgResponse(SubStringUtil.subStr(readDatas, 6, 8));
            gb32960MessageData.setUniqueIdentifier(SubStringUtil.subStr(readDatas, 8, 42));
            gb32960MessageData.setEncryption(SubStringUtil.subStr(readDatas, 42, 44));
            gb32960MessageData.setDataCellLength(SubStringUtil.subStr(readDatas, 44, 48));
//            gb32960MessageData.setData(SubStringUtil.subStr(readDatas,48,-2));
            gb32960MessageData.setCheckCode(SubStringUtil.subStrEnd(readDatas, 2));
            log.info("GB32960终端校时数据处理---->");
            //响应标识
            gb32960MessageData.setMsgResponse("01");
            //数据单元长度
            gb32960MessageData.setDataCellLength("0000");
            //校验码计算
            StringBuffer checkCodeData = new StringBuffer().append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength());
            gb32960MessageData.setCheckCode(HexUtils.getBCC(String.valueOf(checkCodeData)));
            StringBuffer pushData = new StringBuffer().append(gb32960MessageData.getMsgHead()).append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(gb32960MessageData.getCheckCode());
            return String.valueOf(pushData);
        }
        //TODO 其他报文与数据单元B3.5.2一致 数据单元不为空，特殊处理
        GB32960MessageData gb32960MessageData = new GB32960MessageData();
        gb32960MessageData.setMsgHead(SubStringUtil.subStrStart(readDatas, 4));
        gb32960MessageData.setMsgCommand(SubStringUtil.subStr(readDatas, 4, 6));
        gb32960MessageData.setMsgResponse(SubStringUtil.subStr(readDatas, 6, 8));
        gb32960MessageData.setUniqueIdentifier(SubStringUtil.subStr(readDatas, 8, 42));
        gb32960MessageData.setEncryption(SubStringUtil.subStr(readDatas, 42, 44));
        gb32960MessageData.setDataCellLength(SubStringUtil.subStr(readDatas, 44, 48));
        gb32960MessageData.setData(SubStringUtil.subStr(readDatas, 48, -2));
        gb32960MessageData.setCheckCode(SubStringUtil.subStrEnd(readDatas, 2));
        //原生数据单元报文
        String primaryData = SubStringUtil.subStr(readDatas, 48, -2);
        //TODO 平台登录数据处理(目前没有走ThingLinks平台Link模块校验,可 通过 ThingLinks 提供的spi 做扩展)
        if ("05".equals(gb32960MessageData.getMsgCommand()) && "01".equals(gb32960MessageData.getEncryption())) {
            log.info("GB32960平台登录数据处理---->");
            /*if (redisService.hasKey(ctx.channel().remoteAddress().toString())) {
                String count = redisService.get(ctx.channel().remoteAddress().toString());
                if ("3".equals(count)) {
                    redisService.deleteObject(ctx.channel().remoteAddress().toString());
                    log.info("GB32960平台登录次数上限，断开连接" + ctx.channel().remoteAddress().toString());
                    ctx.channel().close();
                } else {
                    count += 1;
                    redisService.set(ctx.channel().remoteAddress().toString(), count);
                }
            } else {
                redisService.set(ctx.channel().remoteAddress().toString(), "1");
            }*/
            //响应标识
            gb32960MessageData.setMsgResponse("01");
            //数据单元长度
            gb32960MessageData.setDataCellLength("0006");
            //数据单元
            String data = gb32960MessageData.getData();
            StringBuffer timestamp = new StringBuffer().append(SubStringUtil.subStrStart(data, 12));
            //校验码计算
            StringBuffer checkCodeData = new StringBuffer().append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(timestamp);
            gb32960MessageData.setCheckCode(HexUtils.getBCC(String.valueOf(checkCodeData)));
            StringBuffer pushData = new StringBuffer().append(gb32960MessageData.getMsgHead()).append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(timestamp).append(gb32960MessageData.getCheckCode());
            return String.valueOf(pushData);
        }
        //车辆登录数据处理
        if ("01".equals(gb32960MessageData.getMsgCommand()) && "01".equals(gb32960MessageData.getEncryption())) {
            log.info("GB32960车辆登录数据处理----->");
            //响应标识
            gb32960MessageData.setMsgResponse("01");
            //数据单元长度
            gb32960MessageData.setDataCellLength("0006");
            //数据单元
            String data = gb32960MessageData.getData();
            StringBuffer timestamp = new StringBuffer().append(SubStringUtil.subStrStart(data, 12));
            //校验码计算
            StringBuffer checkCodeData = new StringBuffer().append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(timestamp);
            gb32960MessageData.setCheckCode(HexUtils.getBCC(String.valueOf(checkCodeData)));
            StringBuffer pushData = new StringBuffer().append(gb32960MessageData.getMsgHead()).append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                    .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(timestamp).append(gb32960MessageData.getCheckCode());
            return String.valueOf(pushData);
        }
        //实时数据单元处理(不加密)
        if ("02".equals(gb32960MessageData.getMsgCommand()) || "03".equals(gb32960MessageData.getMsgCommand()) && "01".equals(gb32960MessageData.getEncryption())) {
            log.info("32960实时数据处理----->");
            GB32960BaseDTO gb32960BaseDTO = new GB32960BaseDTO();
            gb32960BaseDTO.setCommand("TERMINAL_VEHICLE_UPLOAD_REALTIME");
            gb32960BaseDTO.setVin(HexUtils.convertHexToString(gb32960MessageData.getUniqueIdentifier()));
            String data = gb32960MessageData.getData();
            //采集时间处理
            log.info("采集时间-年：" + HexUtils.hexStringToDecimal(SubStringUtil.subStrStart(data, 2)));
            log.info("采集时间-月：" + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 2, 4)));
            log.info("采集时间-日：" + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 4, 6)));
            log.info("采集时间-小时：" + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 6, 8)));
            log.info("采集时间-分：" + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 8, 10)));
            log.info("采集时间-秒：" + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 10, 12)));
            StringBuffer timestamp = new StringBuffer().append(HexUtils.hexStringToDecimal(SubStringUtil.subStrStart(data, 2))).append(HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 2, 4))).append(HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 4, 6)))
                    .append(HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 6, 8))).append(HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 8, 10))).append(HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 10, 12)));
            final String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//转换为年月日格式
            String acquisitionTime = year + " " + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 6, 8)) + ":" + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 8, 10)) + ":" + HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 10, 12));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(acquisitionTime);
            long ts = date.getTime();
            gb32960BaseDTO.setDataTime(ts);
            if ("01".equals(SubStringUtil.subStr(data, 12, 14))) {
                //整车数据处理
                GB32960VehicleStatus vehicleStatus = new GB32960VehicleStatus();
                //车辆状态
                String vehicleStatusEngineStatus = SubStringUtil.subStr(data, 14, 16);
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
                String vehicleStatusChargingStatus = SubStringUtil.subStr(data, 16, 18);
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
                String vehicleStatusRunningModel = SubStringUtil.subStr(data, 18, 20);
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
                Integer vehicleStatusSpeed = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 20, 24));
                Double speed = new BigDecimal(vehicleStatusSpeed).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                vehicleStatus.setSpeed(speed);
                //累计里程
                Integer vehicleStatusMileage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 24, 32));
                Double mileage = new BigDecimal(vehicleStatusMileage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                vehicleStatus.setMileage(mileage);
                //总电压
                Integer vehicleStatusVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 32, 36));
                Double voltage = new BigDecimal(vehicleStatusVoltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                vehicleStatus.setVoltage(voltage);
                //总电流
                Integer vehicleStatusCurrent = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 36, 40));
                Double current = new BigDecimal(vehicleStatusCurrent).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                vehicleStatus.setCurrent(current);
                //SOC
                Integer vehicleStatusSoc = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 40, 42));
                switch (vehicleStatusSoc) {
                    default:
                        vehicleStatus.setSoc(vehicleStatusSoc);
                        break;
                }
                //DC-DC状态
                String vehicleStatusDcStatus = SubStringUtil.subStr(data, 42, 44);
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
                //TODO 档位处理
                GB32960TransmissionStatus transmissionStatus = new GB32960TransmissionStatus();
                Integer vehicleStatusTransmissionStatus = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 44, 46));
                switch (vehicleStatusTransmissionStatus) {
                    default:
                        transmissionStatus.setGear(vehicleStatusTransmissionStatus);
                        break;
                }
                vehicleStatus.setTransmissionStatus(transmissionStatus);
                //绝缘电阻
                Integer vehicleStatusInsulationResistance = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 46, 50));
                switch (vehicleStatusInsulationResistance) {
                    default:
                        vehicleStatus.setInsulationResistance(vehicleStatusInsulationResistance);
                        break;
                }
                //预留位
                Integer yuliu = HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 50, 54));

                gb32960BaseDTO.setVehicleStatus(vehicleStatus);
                gb32960MessageData.setData(SubStringUtil.subStr(data, 54, gb32960MessageData.getData().length()));
            }
            if ("02".equals(SubStringUtil.subStrStart(gb32960MessageData.getData(), 2))) {
                //TODO 驱动电机数据(目前只处理了单个驱动电机)
                GB32960DriveMotorStatus driveMotorStatus = new GB32960DriveMotorStatus();
                //驱动机个数
                Integer driveMotorStatusDriveMotorCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 2, 4));
                switch (driveMotorStatusDriveMotorCount) {
                    default:
                        driveMotorStatus.setDriveMotorCount(driveMotorStatusDriveMotorCount);
                        break;
                }
                //每个驱动电机总成信息长度
                String driveMotorsLength = SubStringUtil.subStr(gb32960MessageData.getData(), 4, 28 * driveMotorStatusDriveMotorCount);
                //驱动机详情
                GB32960DriveMotors driveMotors = new GB32960DriveMotors();
                //驱动电机序号
                Integer driveMotorsSn = HexUtils.hexStringToDecimal(SubStringUtil.subStrStart(driveMotorsLength, 2));
                switch (driveMotorsSn) {
                    default:
                        driveMotors.setSn(driveMotorsSn);
                        break;
                }
                //驱动电机序号
                String driveMotorsDriveMotorPower = SubStringUtil.subStr(driveMotorsLength, 2, 4);
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
                }
                //驱动电机控制器温度
                Integer driveMotorsControllerTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorsLength, 4, 6));
                switch (driveMotorsControllerTemperature) {
                    default:
                        driveMotors.setControllerTemperature(driveMotorsControllerTemperature - 40);
                        break;
                }
                //驱动电机转速
                Integer driveMotorsDriveMotorSpeed = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorsLength, 6, 10));
                switch (driveMotorsDriveMotorSpeed) {
                    default:
                        driveMotors.setDriveMotorSpeed(driveMotorsDriveMotorSpeed - 20000);
                        break;
                }
                //驱动电机转矩
                Integer driveMotorsDriveMotorTorque = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorsLength, 10, 14));
                Double driveMotorTorque = new BigDecimal(driveMotorsDriveMotorTorque).subtract(new BigDecimal(20000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                driveMotors.setDriveMotorTorque(driveMotorTorque);
                //驱动电机温度
                Integer driveMotorsDriveMotorTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorsLength, 14, 16));
                switch (driveMotorsDriveMotorTemperature) {
                    default:
                        driveMotors.setDriveMotorTemperature(driveMotorsDriveMotorTemperature - 40);
                        break;
                }
                //电机控制器输入电压
                Integer driveMotorsControllerInputVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorsLength, 16, 20));
                Double controllerInputVoltage = new BigDecimal(driveMotorsControllerInputVoltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                driveMotors.setControllerInputVoltage(controllerInputVoltage);
                //电机控制器直流母线电流
                Integer driveMotorsDcBusCurrentOfController = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorsLength, 20, 24));
                Double dcBusCurrentOfController = new BigDecimal(driveMotorsDcBusCurrentOfController).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                driveMotors.setDcBusCurrentOfController(dcBusCurrentOfController);
                //TODO 数据赋值未处理
                List<GB32960DriveMotors> list = new ArrayList<>();
                list.add(driveMotors);
                driveMotorStatus.setDriveMotors(list);
                gb32960BaseDTO.setDriveMotorStatus(driveMotorStatus);
                gb32960MessageData.setData(SubStringUtil.subStr(gb32960MessageData.getData(), driveMotorsLength.length() + 4, gb32960MessageData.getData().length()));
            }
            if ("05".equals(SubStringUtil.subStrStart(gb32960MessageData.getData(), 2))) {
                //TODO 车辆位置数据
                GB32960LocationStatus locationStatus = new GB32960LocationStatus();
                //定位状态
                GB32960LocateStatus locateStatus = new GB32960LocateStatus();
                locateStatus.setValidation("VALID");
                //0:北纬、1:南纬
                Integer locateStatusLatitudeType = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 2, 3));
                switch (locateStatusLatitudeType) {
                    case 0:
                        locateStatus.setLatitudeType("NORTH");
                        break;
                    case 1:
                        locateStatus.setLatitudeType("SOUTH");
                        break;
                }
                //0:东经、1:西经
                Integer locateStatusLongitudeType = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 3, 4));
                switch (locateStatusLongitudeType) {
                    case 0:
                        locateStatus.setLongitudeType("EAST");
                        break;
                    case 1:
                        locateStatus.setLongitudeType("WEST");
                        break;
                }
                locationStatus.setLocateStatus(locateStatus);
                //精度
                Integer locationStatusLongitude = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 4, 12));
                switch (locationStatusLongitude) {
                    default:
                        BigDecimal b = new BigDecimal(locationStatusLongitude);
                        Double result = b.movePointLeft(6).doubleValue();
                        locationStatus.setLongitude(result);
                        break;
                }
                //精度
                Integer locationStatusLatitude = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 12, 20));
                switch (locationStatusLatitude) {
                    default:
                        BigDecimal b = new BigDecimal(locationStatusLatitude);
                        Double result = b.movePointLeft(6).doubleValue();
                        locationStatus.setLatitude(result);
                        break;
                }
                gb32960BaseDTO.setLocationStatus(locationStatus);
                gb32960MessageData.setData(SubStringUtil.subStr(gb32960MessageData.getData(), 20, gb32960MessageData.getData().length()));
            }
            if ("06".equals(SubStringUtil.subStrStart(gb32960MessageData.getData(), 2))) {
                //TODO 极致数据处理
                GB32960ExtremeStatus extremeStatus = new GB32960ExtremeStatus();
                //最高电压电池子系统号
                Integer extremeStatusSubSystemIndexOfMaxVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 2, 4));
                switch (extremeStatusSubSystemIndexOfMaxVoltage) {
                    default:
                        extremeStatus.setSubSystemIndexOfMaxVoltage(extremeStatusSubSystemIndexOfMaxVoltage);
                        break;
                }
                //最高电压电池单体代号
                Integer extremeStatusCellIndexOfMaxVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 4, 6));
                switch (extremeStatusCellIndexOfMaxVoltage) {
                    default:
                        extremeStatus.setCellIndexOfMaxVoltage(extremeStatusCellIndexOfMaxVoltage);
                        break;
                }
                //电池单体电压最高值
                Integer extremeStatusCellMaxVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 6, 10));
                Double cellMaxVoltage = new BigDecimal(extremeStatusCellMaxVoltage).divide(new BigDecimal("1000")).setScale(3, BigDecimal.ROUND_UP).doubleValue();
                extremeStatus.setCellMaxVoltage(cellMaxVoltage);
                //最低电压电池子系统号
                Integer extremeStatusSubSystemIndexOfMinVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 10, 12));
                switch (extremeStatusSubSystemIndexOfMinVoltage) {
                    default:
                        extremeStatus.setSubSystemIndexOfMinVoltage(extremeStatusSubSystemIndexOfMinVoltage);
                        break;
                }
                //最低电压电池单体代号
                Integer extremeStatusCellIndexOfMinVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 12, 14));
                switch (extremeStatusCellIndexOfMinVoltage) {
                    default:
                        extremeStatus.setCellIndexOfMinVoltage(extremeStatusCellIndexOfMinVoltage);
                        break;
                }
                //电池单体电压最低值
                Integer extremeStatusCellMinVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 14, 18));
                Double cellMinVoltage = new BigDecimal(extremeStatusCellMinVoltage).divide(new BigDecimal("1000")).setScale(3, BigDecimal.ROUND_UP).doubleValue();
                extremeStatus.setCellMinVoltage(cellMinVoltage);
                //最高温度子系统号
                Integer extremeStatusSubSystemIndexOfMaxTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 18, 20));
                switch (extremeStatusSubSystemIndexOfMaxTemperature) {
                    default:
                        extremeStatus.setSubSystemIndexOfMaxTemperature(extremeStatusSubSystemIndexOfMaxTemperature);
                        break;
                }
                //最高温度探针序号
                Integer extremeStatusProbeIndexOfMaxTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 20, 22));
                switch (extremeStatusProbeIndexOfMaxTemperature) {
                    default:
                        extremeStatus.setProbeIndexOfMaxTemperature(extremeStatusProbeIndexOfMaxTemperature);
                        break;
                }
                //最高温度值
                Integer extremeStatusMaxTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 22, 24));
                switch (extremeStatusMaxTemperature) {
                    default:
                        extremeStatus.setMaxTemperature(extremeStatusMaxTemperature - 40);
                        break;
                }
                //最低温度子系统号
                Integer extremeStatusSubSystemIndexOfMinTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 24, 26));
                switch (extremeStatusSubSystemIndexOfMinTemperature) {
                    default:
                        extremeStatus.setSubSystemIndexOfMinTemperature(extremeStatusSubSystemIndexOfMinTemperature);
                        break;
                }
                //最低温度探针序号
                Integer extremeStatusProbeIndexOfMinTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 26, 28));
                switch (extremeStatusProbeIndexOfMinTemperature) {
                    default:
                        extremeStatus.setProbeIndexOfMinTemperature(extremeStatusProbeIndexOfMinTemperature);
                        break;
                }
                //最低温度值
                Integer extremeStatusMinTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), 28, 30));
                switch (extremeStatusMinTemperature) {
                    default:
                        extremeStatus.setMinTemperature(extremeStatusMinTemperature - 40);
                        break;
                }
                gb32960BaseDTO.setExtremeStatus(extremeStatus);
                gb32960MessageData.setData(SubStringUtil.subStr(gb32960MessageData.getData(), 30, gb32960MessageData.getData().length()));
            }
            if ("07".equals(SubStringUtil.subStrStart(gb32960MessageData.getData(), 2))) {
                //计数器
                Integer i = 2;
                //TODO 报警数据处理
                GB32960AlertStatus alertStatus = new GB32960AlertStatus();
                //最高报警等级,为当前发生的故障中的最高等级值，有效值范围：0～3
                String alertStatusHighestAlertLevel = SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2);
                switch (alertStatusHighestAlertLevel) {
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
                        alertStatus.setHighestAlertLevel(alertStatusHighestAlertLevel);
                        break;
                }
                //通用报警标识
                String universalAlarmIdentification = SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 8);
                log.info("Universal alarm identification--" + universalAlarmIdentification);

                //可充电储能装置故障总数N1,N1个可充电储能装置故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效
                Integer alertStatusEnergyStorageAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                switch (alertStatusEnergyStorageAlertCount) {
                    case 0:
                        alertStatus.setEnergyStorageAlertCount(alertStatusEnergyStorageAlertCount);
                        break;
                    default:
                        alertStatus.setEnergyStorageAlertCount(alertStatusEnergyStorageAlertCount);
                        //可充电储能装置故障代码列表
                        Integer alertStatusEnergyStorageAlertList = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4 * alertStatusEnergyStorageAlertCount));
                        log.info("可充电储能装置故障代码列表--" + alertStatusEnergyStorageAlertList);
                        switch (alertStatusEnergyStorageAlertList) {
                            default:
                                alertStatus.setEnergyStorageAlertList(new ArrayList<>());
                                break;
                        }
                        break;
                }
                //驱动电机,故障总数N2,N2个驱动电机故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效
                Integer alertStatusDriveMotorAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                switch (alertStatusDriveMotorAlertCount) {
                    case 0:
                        alertStatus.setDriveMotorAlertCount(alertStatusDriveMotorAlertCount);
                        break;
                    default:
                        alertStatus.setDriveMotorAlertCount(alertStatusDriveMotorAlertCount);
                        //驱动电机故障代码列表
                        Integer alertStatusDriveMotorAlertList = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4 * alertStatusDriveMotorAlertCount));
                        log.info("驱动电机故障代码列表--" + alertStatusDriveMotorAlertList);
                        switch (alertStatusDriveMotorAlertList) {
                            default:
                                alertStatus.setDriveMotorAlertList(new ArrayList<>());
                                break;
                        }
                        break;
                }

                //发动机故障总数N3,N3个驱动电机故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效
                Integer alertStatusEngineAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                switch (alertStatusEngineAlertCount) {
                    case 0:
                        alertStatus.setEngineAlertCount(alertStatusEngineAlertCount);
                        break;
                    default:
                        alertStatus.setEngineAlertCount(alertStatusEngineAlertCount);
                        //发动机故障列表
                        Integer alertStatusEngineAlertList = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4 * alertStatusEngineAlertCount));
                        log.info("发动机故障列表--" + alertStatusEngineAlertList);
                        switch (alertStatusEngineAlertList) {
                            default:
                                alertStatus.setEngineAlertList(new ArrayList<>());
                                break;
                        }
                        break;
                }
                //其他故障总数N4,N4个驱动电机故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效
                Integer otherAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                switch (otherAlertCount) {
                    case 0:
                        alertStatus.setOtherAlertCount(otherAlertCount);
                        break;
                    default:
                        alertStatus.setOtherAlertCount(otherAlertCount);
                        //其他故障列表
                        Integer otherFaultList = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4 * otherAlertCount));
                        log.info("其他故障列表--" + otherFaultList);
                        switch (otherFaultList) {
                            default:
                                alertStatus.setOtherAlertList(new ArrayList<>());
                                break;
                        }
                        break;
                }
                gb32960BaseDTO.setAlertStatus(alertStatus);
                gb32960MessageData.setData(SubStringUtil.subStr(gb32960MessageData.getData(), i += 4 * alertStatusEngineAlertCount, gb32960MessageData.getData().length()));
            }
            if ("08".equals(SubStringUtil.subStrStart(gb32960MessageData.getData(), 2))) {
                //TODO 可充电储能装置电压数据
                //计数器
                Integer i = 2;
                GB32960EnergyStorageVoltageStatus energyStorageVoltageStatus = new GB32960EnergyStorageVoltageStatus();
                //可充电储能子系统个数,N个可充电储能子系统，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效
                Integer subSystemOfEnergyStorageCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                energyStorageVoltageStatus.setSubSystemOfEnergyStorageCount(subSystemOfEnergyStorageCount);
                //可充电储能子系统电压信息列表
                List<GB32960EnergyStorageVoltages> energyStorageVoltagesList = new ArrayList<>();
                for (int j = 1; j <= subSystemOfEnergyStorageCount; j++) {
                    GB32960EnergyStorageVoltages energyStorageVoltages = new GB32960EnergyStorageVoltages();
                    //可充电储能子系统号.有效值范围：1～250。有效值范围：1～250
                    Integer energyStorageSubSystemIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                    energyStorageVoltages.setEnergyStorageSubSystemIndex(energyStorageSubSystemIndex);
                    //可充电储能装置电压,有效值范围：0～10000（表示0V～1000V），最小计量单元：0.1V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效
                    Integer energyStorageVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4));
                    Double energyStorageVoltageDouble = new BigDecimal(energyStorageVoltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                    energyStorageVoltages.setEnergyStorageVoltage(energyStorageVoltageDouble);
                    //可充电储能装置电流,有效值范围： 0～20000（数值偏移量1000A，表示-1000A～+1000A），最小计量单元：0.1A，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效
                    Integer energyStorageCurrent = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4));
                    Double energyStorageCurrentDouble = new BigDecimal(energyStorageCurrent).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
                    energyStorageVoltages.setEnergyStorageCurrent(energyStorageCurrentDouble);
                    //单体电池总数,N个电池单体，有效值范围：1～65531，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效
                    Integer cellCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4));
                    energyStorageVoltages.setCellCount(cellCount);
                    //本帧起始电池序号,当本帧单体个数超过200时，应拆分成多帧数据进行传输，有效值范围：1～65531
                    Integer frameCellStartIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4));
                    energyStorageVoltages.setFrameCellStartIndex(frameCellStartIndex);
                    //本帧单体电池总数,本帧单体总数 m;有效值范围：1～200
                    Integer frameCellCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                    energyStorageVoltages.setFrameCellCount(frameCellCount);
                    List<Double> cellVoltages = new ArrayList<>();
                    for (int k = 1; k <= frameCellCount; k++) {
                        //单体电池电电压
                        Integer cellVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4));
                        Double cellVoltageDouble = new BigDecimal(cellVoltage).divide(new BigDecimal("1000")).setScale(3, BigDecimal.ROUND_UP).doubleValue();
                        cellVoltages.add(cellVoltageDouble);
                    }
                    energyStorageVoltages.setCellVoltages(cellVoltages);
                    energyStorageVoltagesList.add(energyStorageVoltages);
                }
                //可充电储能子系统电压信息列表
                energyStorageVoltageStatus.setEnergyStorageVoltages(energyStorageVoltagesList);
                gb32960BaseDTO.setEnergyStorageVoltageStatus(energyStorageVoltageStatus);
                gb32960MessageData.setData(SubStringUtil.subStr(gb32960MessageData.getData(), i, gb32960MessageData.getData().length()));
            }
            if ("09".equals(SubStringUtil.subStrStart(gb32960MessageData.getData(), 2))) {
                //TODO 可充电储能装置温度数据
                //计数器
                Integer i = 2;
                GB32960EnergyStorageTemperatureStatus energyStorageTemperatureStatus = new GB32960EnergyStorageTemperatureStatus();
                //可充电储能子系统个数,N个可充电储能装置，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效
                Integer subEnergyStorageSystemCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                energyStorageTemperatureStatus.setSubEnergyStorageSystemCount(subEnergyStorageSystemCount);
                //可充电储能子系统温度信息列表,按可充电储能子系统代号依次排列，每个可充电储能子系统温度分布数据格
                List<GB32960EnergyStorageTemperatures> energyStorageTemperaturesList = new ArrayList<>();
                for (int j = 1; j <= subEnergyStorageSystemCount; j++) {
                    GB32960EnergyStorageTemperatures energyStorageTemperatures = new GB32960EnergyStorageTemperatures();
                    //可充电储能子系统号,有效值范围：1～250
                    Integer energyStorageSubSystemIndex1 = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2));
                    energyStorageTemperatures.setEnergyStorageSubSystemIndex1(energyStorageSubSystemIndex1);
                    //可充电储能,温度探针个数,N个温度探针，有效值范围：1～65531，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效
                    Integer probeCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 4));
                    energyStorageTemperatures.setProbeCount(probeCount);
                    List<Integer> cellTemperatures = new ArrayList<>();
                    for (int k = 1; k <= probeCount; k++) {
                        //可充电储能子系统,各温度探针,检测到的温度值,有效值范围：0～250 （数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
                        cellTemperatures.add(HexUtils.hexStringToDecimal(SubStringUtil.subStr(gb32960MessageData.getData(), i, i += 2)) - 40);
                    }
                    energyStorageTemperatures.setCellTemperatures(cellTemperatures);
                    energyStorageTemperaturesList.add(energyStorageTemperatures);
                }
                //可充电储能子系统温度信息列表,按可充电储能子系统代号依次排列，每个可充电储能子系统温度分布数据格
                energyStorageTemperatureStatus.setEnergyStorageTemperatures(energyStorageTemperaturesList);
                gb32960BaseDTO.setEnergyStorageTemperatureStatus(energyStorageTemperatureStatus);
                gb32960MessageData.setData(SubStringUtil.subStr(gb32960MessageData.getData(), i, gb32960MessageData.getData().length()));
            }
            //TODO 数据解析完成推送至mq
            log.info("数据解析完成推送至mq{}", JSONUtil.toJsonStr(gb32960BaseDTO));
        }
        //响应标识
        gb32960MessageData.setMsgResponse("01");
        //数据单元长度
        gb32960MessageData.setDataCellLength("0006");
        //数据单元-时间获取
        StringBuffer timestamp = new StringBuffer().append(SubStringUtil.subStrStart(primaryData, 12));
        //校验码计算
        StringBuffer checkCodeData = new StringBuffer().append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(timestamp);
        gb32960MessageData.setCheckCode(HexUtils.getBCC(String.valueOf(checkCodeData)));
        StringBuffer pushData = new StringBuffer().append(gb32960MessageData.getMsgHead()).append(gb32960MessageData.getMsgCommand()).append(gb32960MessageData.getMsgResponse())
                .append(gb32960MessageData.getUniqueIdentifier()).append(gb32960MessageData.getEncryption()).append(gb32960MessageData.getDataCellLength()).append(timestamp).append(gb32960MessageData.getCheckCode());
        return String.valueOf(pushData);
    }


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

        // 车辆唯一标识
        String uniqueIdentifier = msg.getUniqueIdentifier();

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

        parseRealTimeOrSupplementaryData(msg);
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

        parseRealTimeOrSupplementaryData(msg);

        return generateResponseWithTimestamp(msg);
    }


    // 解析实时数据或补发数据
    private void parseRealTimeOrSupplementaryData(GB32960MessageData msg) {
        String data = msg.getData();
        // 实时数据处理逻辑
        if ("02".equals(msg.getMsgCommand())) {
            // 处理实时数据
            handleRealtimeDataParsing(data, msg);
        }

        // 补发数据处理逻辑
        if ("03".equals(msg.getMsgCommand())) {
            // 处理补发数据
            handleSupplementaryDataParsing(data, msg);
        }
    }


    private void handleRealtimeDataParsing(String data, GB32960MessageData msg) {
        log.info("处理实时数据: {}", data);

        GB32960BaseDTO gb32960BaseDTO = new GB32960BaseDTO();
        gb32960BaseDTO.setCommand("TERMINAL_VEHICLE_UPLOAD_REALTIME");
        gb32960BaseDTO.setVin(msg.getUniqueIdentifier());

        // 解析采集时间
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String acquisitionTime = year + " " +
                HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 6, 8)) + ":" +
                HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 8, 10)) + ":" +
                HexUtils.hexStringToDecimal(SubStringUtil.subStr(data, 10, 12));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(acquisitionTime);
            long ts = date.getTime();
            gb32960BaseDTO.setDataTime(ts);
        } catch (Exception e) {
            log.error("时间解析错误: {}", acquisitionTime, e);
        }

        // 整车数据处理
        if ("01".equals(SubStringUtil.subStr(data, 12, 14))) {
            GB32960VehicleStatus vehicleStatus = new GB32960VehicleStatus();
            vehicleStatus.setEngineStatus(parseEngineStatus(SubStringUtil.subStr(data, 14, 16)));
            vehicleStatus.setChargingStatus(parseChargingStatus(SubStringUtil.subStr(data, 16, 18)));
            vehicleStatus.setRunningModel(parseRunningModel(SubStringUtil.subStr(data, 18, 20)));
            vehicleStatus.setSpeed(parseSpeed(SubStringUtil.subStr(data, 20, 24)));
            vehicleStatus.setMileage(parseMileage(SubStringUtil.subStr(data, 24, 32)));
            vehicleStatus.setVoltage(parseVoltage(SubStringUtil.subStr(data, 32, 36)));
            vehicleStatus.setCurrent(parseCurrent(SubStringUtil.subStr(data, 36, 40)));
            vehicleStatus.setSoc(parseSoc(SubStringUtil.subStr(data, 40, 42)));
            vehicleStatus.setDcStatus(parseDcStatus(SubStringUtil.subStr(data, 42, 44)));
            vehicleStatus.setTransmissionStatus(parseTransmissionStatus(SubStringUtil.subStr(data, 44, 46)));
            vehicleStatus.setInsulationResistance(parseInsulationResistance(SubStringUtil.subStr(data, 46, 50)));
            gb32960BaseDTO.setVehicleStatus(vehicleStatus);

            // 解析驱动电机数据
            if ("02".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
                gb32960BaseDTO.setDriveMotorStatus(parseDriveMotorStatus(SubStringUtil.subStr(msg.getData(), 2, -1)));
            }

            // 解析车辆位置数据
            if ("05".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
                gb32960BaseDTO.setLocationStatus(parseLocationStatus(SubStringUtil.subStr(msg.getData(), 2, -1)));
            }

            // 解析极致数据
            if ("06".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
                gb32960BaseDTO.setExtremeStatus(parseExtremeStatus(SubStringUtil.subStr(msg.getData(), 2, -1)));
            }

            // 解析报警数据
            if ("07".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
                gb32960BaseDTO.setAlertStatus(parseAlertStatus(SubStringUtil.subStr(msg.getData(), 2, -1)));
            }

            // 解析可充电储能装置电压数据
            if ("08".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
                gb32960BaseDTO.setEnergyStorageVoltageStatus(parseEnergyStorageVoltageStatus(SubStringUtil.subStr(msg.getData(), 2, -1)));
            }

            // 解析可充电储能装置温度数据
            if ("09".equals(SubStringUtil.subStrStart(msg.getData(), 2))) {
                gb32960BaseDTO.setEnergyStorageTemperatureStatus(parseEnergyStorageTemperatureStatus(SubStringUtil.subStr(msg.getData(), 2, -1)));
            }
        }

        // TODO: 将 gb32960BaseDTO 推送到 Kafka 或其他 API
        processFinalData(gb32960BaseDTO);
    }

    private void handleSupplementaryDataParsing(String data, GB32960MessageData msg) {
        log.info("处理补发数据: {}", data);

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

    private String parseEngineStatus(String status) {
        switch (status) {
            case "01":
                return "STARTED";
            case "02":
                return "STOPPED";
            case "03":
                return "OTHER";
            case "FE":
                return "ERROR";
            case "FF":
                return "INVALID";
            default:
                return "UNKNOWN";
        }
    }

    private String parseChargingStatus(String status) {
        switch (status) {
            case "01":
                return "CHARGING_STOPPED";
            case "02":
                return "CHARGING_DRIVING";
            case "03":
                return "NO_CHARGING";
            case "04":
                return "NO_CHARGING";
            case "FE":
                return "ERROR";
            case "FF":
                return "INVALID";
            default:
                return "UNKNOWN";
        }
    }

    private String parseRunningModel(String model) {
        switch (model) {
            case "01":
                return "EV";
            case "02":
                return "PHEV";
            case "03":
                return "FV";
            case "FE":
                return "ERROR";
            case "FF":
                return "INVALID";
            default:
                return "UNKNOWN";
        }
    }

    private Double parseSpeed(String speedHex) {
        Integer speed = HexUtils.hexStringToDecimal(speedHex);
        return new BigDecimal(speed).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
    }

    private Double parseMileage(String mileageHex) {
        Integer mileage = HexUtils.hexStringToDecimal(mileageHex);
        return new BigDecimal(mileage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
    }

    private Double parseVoltage(String voltageHex) {
        Integer voltage = HexUtils.hexStringToDecimal(voltageHex);
        return new BigDecimal(voltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
    }

    private Double parseCurrent(String currentHex) {
        Integer current = HexUtils.hexStringToDecimal(currentHex);
        return new BigDecimal(current).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
    }

    private Integer parseSoc(String socHex) {
        return HexUtils.hexStringToDecimal(socHex);
    }

    private String parseDcStatus(String dcStatus) {
        switch (dcStatus) {
            case "01":
                return "NORMAL";
            case "02":
                return "OFF";
            case "FE":
                return "ERROR";
            case "FF":
                return "INVALID";
            default:
                return "UNKNOWN";
        }
    }

    private GB32960TransmissionStatus parseTransmissionStatus(String transmissionStatusHex) {
        GB32960TransmissionStatus status = new GB32960TransmissionStatus();
        Integer gear = HexUtils.hexStringToDecimal(transmissionStatusHex);
        status.setGear(gear);
        return status;
    }

    private Integer parseInsulationResistance(String resistanceHex) {
        return HexUtils.hexStringToDecimal(resistanceHex);
    }

    private GB32960DriveMotorStatus parseDriveMotorStatus(String driveMotorData) {
        log.info("解析驱动电机数据: {}", driveMotorData);

        GB32960DriveMotorStatus driveMotorStatus = new GB32960DriveMotorStatus();

        // 驱动机个数
        Integer driveMotorCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorData, 0, 2));
        driveMotorStatus.setDriveMotorCount(driveMotorCount);

        List<GB32960DriveMotors> driveMotorsList = new ArrayList<>();

        int offset = 2; // 初始化偏移量
        for (int i = 0; i < driveMotorCount; i++) {
            GB32960DriveMotors driveMotors = new GB32960DriveMotors();

            // 每个驱动电机总成信息长度
            String driveMotorInfo = SubStringUtil.subStr(driveMotorData, offset, 28);
            offset += 28;

            // 驱动电机序号
            Integer driveMotorSn = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorInfo, 0, 2));
            driveMotors.setSn(driveMotorSn);

            // 驱动电机功率状态
            String driveMotorPower = SubStringUtil.subStr(driveMotorInfo, 2, 4);
            switch (driveMotorPower) {
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
                    break;
            }

            // 驱动电机控制器温度
            Integer controllerTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorInfo, 4, 6));
            driveMotors.setControllerTemperature(controllerTemperature - 40);

            // 驱动电机转速
            Integer driveMotorSpeed = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorInfo, 6, 10));
            driveMotors.setDriveMotorSpeed(driveMotorSpeed - 20000);

            // 驱动电机转矩
            Integer driveMotorTorque = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorInfo, 10, 14));
            Double motorTorque = new BigDecimal(driveMotorTorque).subtract(new BigDecimal(20000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
            driveMotors.setDriveMotorTorque(motorTorque);

            // 驱动电机温度
            Integer driveMotorTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorInfo, 14, 16));
            driveMotors.setDriveMotorTemperature(driveMotorTemperature - 40);

            // 电机控制器输入电压
            Integer controllerInputVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorInfo, 16, 20));
            Double inputVoltage = new BigDecimal(controllerInputVoltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
            driveMotors.setControllerInputVoltage(inputVoltage);

            // 电机控制器直流母线电流
            Integer dcBusCurrent = HexUtils.hexStringToDecimal(SubStringUtil.subStr(driveMotorInfo, 20, 24));
            Double busCurrent = new BigDecimal(dcBusCurrent).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue();
            driveMotors.setDcBusCurrentOfController(busCurrent);

            driveMotorsList.add(driveMotors);
        }

        driveMotorStatus.setDriveMotors(driveMotorsList);
        return driveMotorStatus;
    }

    private GB32960LocationStatus parseLocationStatus(String locationData) {
        log.info("解析位置数据: {}", locationData);

        GB32960LocationStatus locationStatus = new GB32960LocationStatus();

        // 定位状态
        GB32960LocateStatus locateStatus = new GB32960LocateStatus();
        locateStatus.setValidation("VALID");

        Integer latitudeType = HexUtils.hexStringToDecimal(SubStringUtil.subStr(locationData, 0, 1));
        locateStatus.setLatitudeType(latitudeType == 0 ? "NORTH" : "SOUTH");

        Integer longitudeType = HexUtils.hexStringToDecimal(SubStringUtil.subStr(locationData, 1, 2));
        locateStatus.setLongitudeType(longitudeType == 0 ? "EAST" : "WEST");

        locationStatus.setLocateStatus(locateStatus);

        // 精度
        Integer longitude = HexUtils.hexStringToDecimal(SubStringUtil.subStr(locationData, 2, 10));
        locationStatus.setLongitude(new BigDecimal(longitude).movePointLeft(6).doubleValue());

        Integer latitude = HexUtils.hexStringToDecimal(SubStringUtil.subStr(locationData, 10, 18));
        locationStatus.setLatitude(new BigDecimal(latitude).movePointLeft(6).doubleValue());

        return locationStatus;
    }

    private GB32960ExtremeStatus parseExtremeStatus(String extremeData) {
        log.info("解析极致数据: {}", extremeData);

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

        return extremeStatus;
    }

    private GB32960AlertStatus parseAlertStatus(String alertData) {
        log.info("解析报警数据: {}", alertData);

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

        if (energyStorageAlertCount > 0) {
            List<Integer> energyStorageAlertList = new ArrayList<>();
            for (int i = 0; i < energyStorageAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 12 + 4 * i, 12 + 4 * i + 4));
                energyStorageAlertList.add(alertCode);
            }
            alertStatus.setEnergyStorageAlertList(energyStorageAlertList);
        }

        // 驱动电机故障总数
        Integer driveMotorAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 12 + 4 * energyStorageAlertCount, 14 + 4 * energyStorageAlertCount));
        alertStatus.setDriveMotorAlertCount(driveMotorAlertCount);

        if (driveMotorAlertCount > 0) {
            List<Integer> driveMotorAlertList = new ArrayList<>();
            for (int i = 0; i < driveMotorAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 14 + 4 * energyStorageAlertCount + 4 * i, 14 + 4 * energyStorageAlertCount + 4 * i + 4));
                driveMotorAlertList.add(alertCode);
            }
            alertStatus.setDriveMotorAlertList(driveMotorAlertList);
        }

        // 发动机故障总数
        Integer engineAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 14 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount, 16 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount));
        alertStatus.setEngineAlertCount(engineAlertCount);

        if (engineAlertCount > 0) {
            List<Integer> engineAlertList = new ArrayList<>();
            for (int i = 0; i < engineAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 16 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount + 4 * i, 16 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount + 4 * i + 4));
                engineAlertList.add(alertCode);
            }
            alertStatus.setEngineAlertList(engineAlertList);
        }

        // 其他故障总数
        Integer otherAlertCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 16 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount + 4 * engineAlertCount, 18 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount + 4 * engineAlertCount));
        alertStatus.setOtherAlertCount(otherAlertCount);

        if (otherAlertCount > 0) {
            List<Integer> otherAlertList = new ArrayList<>();
            for (int i = 0; i < otherAlertCount; i++) {
                Integer alertCode = HexUtils.hexStringToDecimal(SubStringUtil.subStr(alertData, 18 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount + 4 * engineAlertCount + 4 * i, 18 + 4 * energyStorageAlertCount + 4 * driveMotorAlertCount + 4 * engineAlertCount + 4 * i + 4));
                otherAlertList.add(alertCode);
            }
            alertStatus.setOtherAlertList(otherAlertList);
        }

        return alertStatus;
    }

    private GB32960EnergyStorageVoltageStatus parseEnergyStorageVoltageStatus(String voltageData) {
        log.info("解析电压数据: {}", voltageData);

        GB32960EnergyStorageVoltageStatus voltageStatus = new GB32960EnergyStorageVoltageStatus();

        Integer subSystemCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, 0, 2));
        voltageStatus.setSubSystemOfEnergyStorageCount(subSystemCount);

        List<GB32960EnergyStorageVoltages> voltagesList = new ArrayList<>();
        int offset = 2;

        for (int i = 0; i < subSystemCount; i++) {
            GB32960EnergyStorageVoltages voltages = new GB32960EnergyStorageVoltages();

            Integer subSystemIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 2));
            voltages.setEnergyStorageSubSystemIndex(subSystemIndex);
            offset += 2;

            Integer energyStorageVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setEnergyStorageVoltage(new BigDecimal(energyStorageVoltage).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue());
            offset += 4;

            Integer energyStorageCurrent = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setEnergyStorageCurrent(new BigDecimal(energyStorageCurrent).subtract(new BigDecimal(1000)).divide(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_UP).doubleValue());
            offset += 4;

            Integer cellCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setCellCount(cellCount);
            offset += 4;

            Integer frameCellStartIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
            voltages.setFrameCellStartIndex(frameCellStartIndex);
            offset += 4;

            Integer frameCellCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 2));
            voltages.setFrameCellCount(frameCellCount);
            offset += 2;

            List<Double> cellVoltages = new ArrayList<>();
            for (int j = 0; j < frameCellCount; j++) {
                Integer cellVoltage = HexUtils.hexStringToDecimal(SubStringUtil.subStr(voltageData, offset, offset + 4));
                cellVoltages.add(new BigDecimal(cellVoltage).divide(new BigDecimal("1000")).setScale(3, BigDecimal.ROUND_UP).doubleValue());
                offset += 4;
            }
            voltages.setCellVoltages(cellVoltages);

            voltagesList.add(voltages);
        }

        voltageStatus.setEnergyStorageVoltages(voltagesList);
        return voltageStatus;
    }

    private GB32960EnergyStorageTemperatureStatus parseEnergyStorageTemperatureStatus(String temperatureData) {
        log.info("解析温度数据: {}", temperatureData);

        GB32960EnergyStorageTemperatureStatus temperatureStatus = new GB32960EnergyStorageTemperatureStatus();

        Integer subSystemCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, 0, 2));
        temperatureStatus.setSubEnergyStorageSystemCount(subSystemCount);

        List<GB32960EnergyStorageTemperatures> temperaturesList = new ArrayList<>();
        int offset = 2;

        for (int i = 0; i < subSystemCount; i++) {
            GB32960EnergyStorageTemperatures temperatures = new GB32960EnergyStorageTemperatures();

            Integer subSystemIndex = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, offset, offset + 2));
            temperatures.setEnergyStorageSubSystemIndex1(subSystemIndex);
            offset += 2;

            Integer probeCount = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, offset, offset + 4));
            temperatures.setProbeCount(probeCount);
            offset += 4;

            List<Integer> cellTemperatures = new ArrayList<>();
            for (int j = 0; j < probeCount; j++) {
                Integer cellTemperature = HexUtils.hexStringToDecimal(SubStringUtil.subStr(temperatureData, offset, offset + 2)) - 40;
                cellTemperatures.add(cellTemperature);
                offset += 2;
            }
            temperatures.setCellTemperatures(cellTemperatures);

            temperaturesList.add(temperatures);
        }

        temperatureStatus.setEnergyStorageTemperatures(temperaturesList);
        return temperatureStatus;
    }


    // 处理最终的数据实体，推送到 Kafka 或调用 API
    private void processFinalData(GB32960BaseDTO data) {
        //TODO 将数据推送到 Kafka 或其他 API
        String jsonData = JSONUtil.toJsonStr(data);
        // 示例：推送到 Kafka
        // kafkaTemplate.send("topic_name", jsonData);
        log.info("数据推送到 Kafka 或 API: {}", jsonData);
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
        //数据单元-时间获取
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
