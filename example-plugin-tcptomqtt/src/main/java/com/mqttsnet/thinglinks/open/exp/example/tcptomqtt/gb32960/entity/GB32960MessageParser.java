package com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity;

import com.mqttsnet.thinglinks.open.exp.example.tcptomqtt.gb32960.entity.dao.GB32960MessageData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * -----------------------------------------------------------------------------
 * File Name: GB32960MessageParser
 * -----------------------------------------------------------------------------
 * Description:
 * <p>
 * GB32960MessageParser 负责将 ByteBuf 中的 GB32960 消息解析为 GB32960MessageData 对象。
 * -----------------------------------------------------------------------------
 *
 * @author xiaonannet
 * @version 1.0
 * -----------------------------------------------------------------------------
 * Revision History:
 * Date         Author          Version     Description
 * --------      --------     -------   --------------------
 * 2024/9/9       xiaonannet        1.0        Initial creation
 * -----------------------------------------------------------------------------
 * @email
 * @date 2024/9/9 14:38
 */
@Slf4j
public class GB32960MessageParser {

    /**
     * 解析 GB32960 消息
     *
     * @param frame 包含完整 GB32960 消息的 ByteBuf
     * @return 解析后的 GB32960MessageData 对象
     */
    public static GB32960MessageData parse(ByteBuf frame) {
        GB32960MessageData messageData = new GB32960MessageData();

        // 读取起始符
        messageData.setMsgHead(ByteBufUtil.hexDump(frame.readBytes(2)));

        // 读取命令标识
        messageData.setMsgCommand(ByteBufUtil.hexDump(frame.readBytes(1)));

        // 读取应答标识
        messageData.setMsgResponse(ByteBufUtil.hexDump(frame.readBytes(1)));

        // 读取唯一识别码
        messageData.setUniqueIdentifier(ByteBufUtil.hexDump(frame.readBytes(17)));

        // 读取加密方式
        messageData.setEncryption(ByteBufUtil.hexDump(frame.readBytes(1)));

        // 读取数据单元长度
        int dataCellLength = frame.readUnsignedShort();
        messageData.setDataCellLength(String.valueOf(dataCellLength));

        // 读取数据单元
        byte[] dataUnit = new byte[dataCellLength];
        frame.readBytes(dataUnit);
        messageData.setData(ByteBufUtil.hexDump(dataUnit));

        // 读取校验码
        messageData.setCheckCode(ByteBufUtil.hexDump(frame.readBytes(1)));

        return messageData;
    }
}
