package com.mqttsnet.thinglinks.open.exp.example.tcpserver.utils.bytes;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 字节工具类
 * @Author: mqttsnet
 * @E-mail: mqttsnet@163.com
 * @Website: https://www.mqttsnet.com
 * @CreateDate: 2021/11/23$ 11:37$
 * @UpdateUser: mqttsnet
 * @UpdateDate: 2021/11/23$ 11:37$
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class ByteUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static List<String> datePattern = new ArrayList<String>();

    static {
        datePattern.add("yyyy-MM-dd hh/mm/ss");
        datePattern.add("yyyy-MM-dd hh:mm:ss");
        datePattern.add("yyyy/MM/dd hh:mm:dd");
    }

    static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        DeserializationConfig config = objectMapper.getDeserializationConfig();
        for (String s : datePattern) {
            objectMapper.setDateFormat(new SimpleDateFormat(s));
        }
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    public static ObjectMapper InstanceObjectMapper() {
        return objectMapper;
    }

    public List<String> getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(List<String> datePattern) {
        this.datePattern = datePattern;
    }

    /**
     * 向串口发送数据转为字节数组
     */
    public static byte[] hex2byte(String hex) {
        String digital = "0123456789ABCDEF";
        String hex1 = hex.replace(" ", "");
        char[] hex2char = hex1.toCharArray();
        byte[] bytes = new byte[hex1.length() / 2];
        byte temp;
        for (int p = 0; p < bytes.length; p++) {
            temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);
            temp += digital.indexOf(hex2char[2 * p + 1]);
            bytes[p] = (byte) (temp & 0xff);
        }
        return bytes;
    }

    /**
     * 接收到的字节数组转换16进制字符串
     */
    public static String byteToStr(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 计算CRC16校验码
     * 逐个求和
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC_16(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        if (Integer.toHexString(CRC).toUpperCase().length() == 2) {
            return byteToStr(bytes, bytes.length) + "00" + Integer.toHexString(CRC).toUpperCase();
        } else if (Integer.toHexString(CRC).toUpperCase().length() == 3) {
            return byteToStr(bytes, bytes.length) + "0" + Integer.toHexString(CRC).toUpperCase();
        }
        return byteToStr(bytes, bytes.length) + Integer.toHexString(CRC).toUpperCase();
    }

    /**
     * 指令校验和,并取出后两位字节
     */
    public static String getSum16(byte[] msg, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */
        return byteToStr(msg, length) + byteToStr(mByte, mByte.length).substring(byteToStr(mByte, mByte.length).length() - 4, byteToStr(mByte, mByte.length).length());
    }

    /**
     * 计算校验值
     */
    public static String getCRC_16CheckSum(String hexdata) {
        if (hexdata == null || hexdata.equals("")) {
            return "00";
        }
        hexdata = hexdata.replaceAll(" ", "");
        int total = 0;
        int len = hexdata.length();
        if (len % 2 != 0) {
            return "00";
        }
        int num = 0;
        while (num < len) {
            String s = hexdata.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        String data = hexInt(total);
        return data.substring(data.length() - 2, data.length());
    }

    public static String hexInt(int total) {
        int a = total / 256;
        int b = total % 256;
        if (a > 255) {
            return hexInt(a) + format(b);
        }
        return format(a) + format(b);
    }

    public static String format(int hex) {
        String hexa = Integer.toHexString(hex);
        int len = hexa.length();
        if (len < 2) {
            hexa = "0" + hexa;
        }
        return hexa;
    }

    public static String convertByteBufToString(ByteBuf buf) {
        String str;
        if (buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String bytesToHexColon(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (i != 0) {
                sb.append(":");
            }
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);

        }
        return sb.toString();
    }


    public static String hext10To16(int value) {
        return String.format("%08X", value);
    }

    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }


    public static String dayFomat(Date date) {
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss SSs");
        String d = bartDateFormat.format(date);
        return d;
    }


    public static byte[] hexToByteArray(String inHex) {
        if (inHex.contains(" ")) {
            inHex = inHex.replace(" ", "");
        }
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static Calendar bytes2Calendar(byte[] bytes) {
        int time = (bytes[0] << 24) & 0xFF000000 |
                (bytes[1] << 16) & 0xFF0000 |
                (bytes[2] << 8) & 0xFF00 |
                (bytes[3]) & 0xFF;
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(time * 1000L);
        return calendar;
    }

    public static byte[] calendar2Bytes(Calendar calendar) {
        int time = (int) (calendar.getTimeInMillis() / 1000);
        long time1 = calendar.getTimeInMillis();
        byte[] bytes = new byte[6];
        for (int i = bytes.length - 1; i >= 0; i--) {
            bytes[i] = (byte) (time & 0xFF);
            time >>= 8;
        }
        return bytes;
    }

    public static byte[] longToBytes(long x) {
//        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//
//        buffer.putLong(x);
//
//        return buffer.array();
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((x >> offset) & 0xff);
        }
        return byteNum;

    }

    public static int bytes2Long(byte[] byteNum) {
        int num = 0;
        for (int ix = 0; ix < 4; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    public static long getUnsignedByte(byte[] bytes) {     //将data字节型数据转换为0~255 (0xFF 即BYTE)。

        int mask = 0xff;
        int temp = 0;
        long res = 0;
        int byteslen = bytes.length;
        if (byteslen > 8) {
            return Long.valueOf(0L);
        }
        for (int i = 0; i < byteslen; i++) {
            res <<= 8;
            temp = bytes[i] & mask;
            res |= temp;
        }
        return res;

    }

    public static int byteToInt(byte[] bytes) {
        if (bytes.length < 4) {
            if (bytes.length != 0) {
                byte[] bytes1 = new byte[4];
                if (bytes.length == 3) {
                    bytes1[0] = 0x00;
                    bytes1[1] = bytes[0];
                    bytes1[2] = bytes[1];
                    bytes1[3] = bytes[2];
                } else if (bytes.length == 2) {
                    bytes1[0] = 0x00;
                    bytes1[1] = 0x00;
                    bytes1[2] = bytes[0];
                    bytes1[3] = bytes[1];
                } else if (bytes.length == 1) {
                    bytes1[0] = 0x00;
                    bytes1[1] = 0x00;
                    bytes1[2] = 0x00;
                    bytes1[3] = bytes[0];
                }
                return (bytes1[0] & 0xff) << 24
                        | (bytes1[1] & 0xff) << 16
                        | (bytes1[2] & 0xff) << 8
                        | (bytes1[3] & 0xff);
            } else {
                return 0;
            }
        } else if (bytes.length >= 4) {
            int a = (bytes[0] & 0xff) << 24;//说明二
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            return a | b | c | d;
        }
        return 0;

    }

    public static long bytesToSignedlong(byte[] bs) {
        int bytes = bs.length;

        switch (bytes) {
            case 0:
                return 0;
            case 1:
                return (long) ((bs[0]));
            case 2:
                return (long) ((bs[0]) << 8 | (bs[1] & 0xff));
            case 3:
                return (long) ((bs[0]) << 16 | (bs[1] & 0xff) << 8 | (bs[2] & 0xff));
            case 4:
                return (long) ((bs[0]) << 24 | (bs[1] & 0xffL) << 16 | (bs[2] & 0xffL) << 8 | (bs[3] & 0xffL));
            case 5:
                return (long) ((bs[0]) << 32 | (bs[1] & 0xffL) << 24 | (bs[2] & 0xffL) << 16 | (bs[3] & 0xffL) << 8 | (bs[4] & 0xffL));
            case 6:
                return (long) ((bs[0]) << 40 | (bs[1] & 0xffL) << 32 | (bs[2] & 0xffL) << 24 | (bs[3] & 0xffL) << 16 | (bs[4] & 0xffL) << 8 | (bs[5] & 0xffL));
            case 7:
                return (long) ((bs[0]) << 48 | (bs[1] & 0xffL) << 40 | (bs[2] & 0xffL) << 32 | (bs[3] & 0xffL) << 24 | (bs[4] & 0xffL) << 16 | (bs[5] & 0xffL) << 8 | (bs[6] & 0xffL));
            case 8:
                return (long) ((bs[0]) << 56 | (bs[1] & 0xffL) << 48 | (bs[2] & 0xffL) << 40 | (bs[3] & 0xffL) << 32 |
                        (bs[4] & 0xffL) << 24 | (bs[5] & 0xffL) << 16 | (bs[6] & 0xffL) << 8 | (bs[7] & 0xffL));
            default:
                return 0;
        }
    }

    public static int byteToSignedInt(byte[] bytes) {
        if (bytes.length < 4) {
            if (bytes.length != 0) {
                byte[] bytes1 = new byte[4];
                if (bytes.length == 3) {
                    bytes1[0] = 0x00;
                    bytes1[1] = bytes[0];
                    bytes1[2] = bytes[1];
                    bytes1[3] = bytes[2];
                } else if (bytes.length == 2) {
                    bytes1[0] = 0x00;
                    bytes1[1] = 0x00;
                    bytes1[2] = bytes[0];
                    bytes1[3] = bytes[1];
                } else if (bytes.length == 1) {
                    bytes1[0] = 0x00;
                    bytes1[1] = 0x00;
                    bytes1[2] = 0x00;
                    bytes1[3] = bytes[0];
                }
                return (bytes1[0]) << 24
                        | (bytes1[1]) << 16
                        | (bytes1[2]) << 8
                        | (bytes1[3] & 0xFF);
            } else {
                return 0;
            }
        } else if (bytes.length == 4) {
            return (bytes[0]) << 24
                    | (bytes[1] & 0xFF) << 16
                    | (bytes[2] & 0xFF) << 8
                    | (bytes[3] & 0xFF);
        }
        return 0;

    }


    // 将int类型装换为byte[]数组
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }


    public static byte[] readByteFromInputstream() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\bh\\data_202110180616_reversed.raw");
        ImageReader reader = ImageIO.getImageReaders(file).next();
        reader.setInput(ImageIO.createImageInputStream(file));
        IIOMetadata metadata = reader.getImageMetadata(0);


        InputStream is = new FileInputStream(file);

        BufferedInputStream bis = new BufferedInputStream(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int BYTE_SIZE = 2 * 1024;
        //创建字节数组作为缓存
        byte[] b = new byte[BYTE_SIZE];

        int c;
        //c = bis.read(b)将字节流读出后放入字节数组b中,baos.write(b, 0, c);读取多少就写入多少
        while ((c = bis.read(b)) != -1) {
            baos.write(b, 0, c);
            baos.flush();
        }
        //将字节数组流转换成字节数组
        byte[] date = baos.toByteArray();

        is.close();
        baos.close();

        return date;

    }

    /**
     * date 2021:05:15:21:36:45
     *
     * @return
     * @throws IOException
     */
    public static byte[] bytes7ByDate(String date) {
        String[] strings = date.split(":");

        byte[] bytes0 = int2byte(Integer.parseInt(strings[0]));
        byte[] bytes2 = int2byte(Integer.parseInt(strings[1]));
        byte[] bytes3 = int2byte(Integer.parseInt(strings[2]));
        byte[] bytes4 = int2byte(Integer.parseInt(strings[3]));
        byte[] bytes5 = int2byte(Integer.parseInt(strings[4]));
        byte[] bytes6 = int2byte(Integer.parseInt(strings[5]));

        byte[] bytes = new byte[7];
        System.arraycopy(bytes0, 0, bytes, 0, 2);
        System.arraycopy(bytes2, 0, bytes, 2, 1);
        System.arraycopy(bytes3, 0, bytes, 3, 1);
        System.arraycopy(bytes4, 0, bytes, 4, 1);
        System.arraycopy(bytes5, 0, bytes, 5, 1);
        System.arraycopy(bytes6, 0, bytes, 6, 1);
        return bytes;
    }

    public static String dateByBytes7(byte[] bytes) {
        String date = "";
        if (bytes.length != 7) {
            return date;
        } else {
            byte[] bytesy = new byte[2];
            byte[] bytes1 = new byte[1];
            System.arraycopy(bytes, 0, bytesy, 0, 2);
            int y1 = ByteUtil.byteToInt(bytesy);
            System.arraycopy(bytes, 2, bytes1, 0, 1);
            int m = ByteUtil.byteToInt(bytes1);
            String mstr = "";
            if (m < 10) {
                mstr = "0" + m;
            } else {
                mstr = m + "";
            }
            System.arraycopy(bytes, 3, bytes1, 0, 1);
            int d = ByteUtil.byteToInt(bytes1);
            String dstr = "";
            if (d < 10) {
                dstr = "0" + d;
            } else {
                dstr = d + "";
            }
            System.arraycopy(bytes, 4, bytes1, 0, 1);
            int h = ByteUtil.byteToInt(bytes1);
            String hstr = "";
            if (h < 10) {
                hstr = "0" + h;
            } else {
                hstr = h + "";
            }
            System.arraycopy(bytes, 5, bytes1, 0, 1);
            int mi = ByteUtil.byteToInt(bytes1);
            String mistr = "";
            if (mi < 10) {
                mistr = "0" + mi;
            } else {
                mistr = mi + "";
            }
            System.arraycopy(bytes, 6, bytes1, 0, 1);
            int s = ByteUtil.byteToInt(bytes1);
            String sstr = "";
            if (s < 10) {
                sstr = "0" + s;
            } else {
                sstr = s + "";
            }
            date = y1 + ":" + mstr + ":" + dstr + ":" + hstr + ":" + mistr + ":" + sstr;
            return date;
        }
    }

    /**
     * 数组转换成十六进制字符串
     *
     * @param bArray
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase()).append(" ");
        }
        return sb.toString();
    }

    /**
     * 将byte数组转换为整数
     *
     * @param bs
     * @return
     */
    public static int bytesToInt(byte[] bs) {
        int a = 0;
        for (int i = bs.length - 1; i >= 0; i--) {
            a += bs[i] * Math.pow(255, bs.length - i - 1);
        }
        return a;
    }

    // float转换为byte[4]数组
    public static byte[] getByteArray(float f) {
        int intbits = Float.floatToIntBits(f);//将float里面的二进制串解释为int整数
        return getByteArray(intbits);
    }


    public static byte[] bytesToLittle(byte[] bs) {
        byte[] bytes = new byte[bs.length];

        int i = 0;
        for (int j = bs.length - 1; j >= 0; j--) {
            bytes[j] = bs[i];
            i++;
        }
        return bytes;
    }


    /**
     * 将byte数组转换为浮点数
     *
     * @param offset 从哪位开始读起
     * @return
     */
    public static float readFlowRate(int offset, byte[] data) {
        byte[] newData = new byte[4];
        float flowRate = 0.0f;
        newData[0] = data[offset];
        newData[1] = data[offset + 1];
        newData[2] = data[offset + 2];
        newData[3] = data[offset + 3];
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.put(newData);
        buf.rewind();
        flowRate = buf.getFloat();
        return flowRate;
    }

    // int转换为byte[4]数组
    public static byte[] getByteArray(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) ((i & 0xff000000) >> 24);
        b[1] = (byte) ((i & 0x00ff0000) >> 16);
        b[2] = (byte) ((i & 0x0000ff00) >> 8);
        b[3] = (byte) (i & 0x000000ff);
        return b;
    }

    // int转换为byte[4]数组
    public static byte[] getByteArray(Long i) {
        byte[] b = new byte[4];
        b[0] = (byte) ((i & 0xff000000) >> 24);
        b[1] = (byte) ((i & 0x00ff0000) >> 16);
        b[2] = (byte) ((i & 0x0000ff00) >> 8);
        b[3] = (byte) (i & 0x000000ff);
        return b;
    }

    // 从byte数组的index处的连续4个字节获得一个int
    public static int getInt(byte[] arr, int index) {
        return (0xff000000 & (arr[index + 0] << 24)) |
                (0x00ff0000 & (arr[index + 1] << 16)) |
                (0x0000ff00 & (arr[index + 2] << 8)) |
                (0x000000ff & arr[index + 3]);
    }

    public static byte[] getByteArray(double data) {
        long intBits = Double.doubleToLongBits(data);

        return getBytes(intBits);

    }


    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + "," +
                (byte) ((b >> 6) & 0x1) + "," +
                (byte) ((b >> 5) & 0x1) + "," +
                (byte) ((b >> 4) & 0x1) + "," +
                (byte) ((b >> 3) & 0x1) + "," +
                (byte) ((b >> 2) & 0x1) + "," +
                (byte) ((b >> 1) & 0x1) + "," +
                (byte) ((b >> 0) & 0x1) + ",";
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];

        bytes[0] = (byte) (data & 0xff);

        bytes[1] = (byte) ((data >> 8) & 0xff);

        bytes[2] = (byte) ((data >> 16) & 0xff);

        bytes[3] = (byte) ((data >> 24) & 0xff);

        bytes[4] = (byte) ((data >> 32) & 0xff);

        bytes[5] = (byte) ((data >> 40) & 0xff);

        bytes[6] = (byte) ((data >> 48) & 0xff);

        bytes[7] = (byte) ((data >> 56) & 0xff);

        return bytes;

    }

    public static String convertString(String str) {
        String upStr = str.toUpperCase();
        String lowStr = str.toLowerCase();
        StringBuffer buf = new StringBuffer(str.length());
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == upStr.charAt(i)) {
                buf.append(lowStr.charAt(i));
            } else {
                buf.append(upStr.charAt(i));
            }
        }
        return buf.toString();
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"–> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF,
     * 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static String scrambleTable(byte[] bytes) {
        System.out.println(byteToHex(bytes));
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (0xFF - bytes[i]);//取反
        }
        String scrambleTable = "00000000FF480EC09A0D70BC8E2C93ADA7B746CE5A977DCC32A2BF3E0A10F18894CDEAB1FE901D81341AE1791C" +
                "59275B4F6E8D9CB52EFB9865457E7C1421E311299BD563FD203B026835C2F238B24EB69EDD1B396A5DF730CA8AFCF82843C6225337AA" +
                "C7FA407604D06B85E471649D6D3DBA3672D4BBEE619515F9F050878C44A66F558FF480EC09A0D70BC8E2C93ADA7B746CE5A977DCC32A2" +
                "BF3E0A10F18894CDEAB1FE901D81341AE1791C59275B4F6E8D9CB52EFB9865457E7C1421E311299BD563FD203B026835C2F238B24EB69E" +
                "DD1B396A5DF730CA8AFCF82843C6225337AAC7FA407604D06B85E471649D6D3DBA3672D4BBEE619515F9F050878C44A66F558FF480EC09" +
                "A0D70BC8E2C93ADA7B746CE5A977DCC32A2BF3E0A10F18894CDEAB1FE901D81341AE1791C59275B4F6E8D9CB52EFB9865457E7C1421E311" +
                "299BD563FD203B026835C2F238B24EB69EDD1B396A5DF730CA8AFCF82843C6225337AAC7FA407604D06B85E471649D6D3DBA3672D4BBEE6" +
                "19515F9F050878C44A66F558FF480EC09A0D70BC8E2C93ADA7B746CE5A977DCC32A2BF3E0A10F18894CDEAB1FE901D81341AE1791C59275" +
                "B4F6E8D9CB52EFB9865457E7C1421E311299BD563FD203B026835C2F238B24EB69EDD1B396A5DF730CA8AFCF82843C6225337AAC7FA4076" +
                "04D06B85E471649D6D3DBA3672D4BBEE619515F9F050878C44A66F558FF480EC09A0D70BC8E2C93ADA7B746CE5A977DCC32A2BF3E0A10F1" +
                "8894CDEAB1FE901D81341AE1791C59275B4F6E8D9CB52EFB9865457E7C1421E311299BD563FD203B026835C2F238B24EB69EDD1B396A5DF" +
                "730CA8AFCF82843C6225337AAC7FA407604D06B85E471649D6D3DBA3672D4BBEE619515F9F050878C44A66F558FF480EC09A0D70BC8E2C9" +
                "3ADA7B746CE5A977DCC32A2BF3E0A10F18894CDEAB1FE901D81341AE1791C59275B4F6E8D9CB52EFB9865457E7C1421E311299BD563FD20" +
                "3B026835C2F238B24EB69EDD1B396A5DF730CA8AFCF82843C6225337AAC7FA407604D06B85E471649D6D3DBA3672D4BBEE619515F9F050878C" +
                "44A66F558FF480EC09A0D70BC8E2C93ADA7B746CE5A977DCC32A2BF3E0A10F18894CDEAB1FE901D81341AE1791C59275B4F6E8D9CB52EFB9865457" +
                "E7C1421E311299BD563FD203B026835C2F238B24EB69EDD1B396A5DF730CA8AFCF82843C6225337AAC7FA407604D06B85E471649D6D3DBA3672D4BBEE" +
                "619515F9F050878C44A66F558FF480EC09A0D70BC8E2C93ADA7B746CE5A977DCC32A2BF3E0A10F18894CDEAB1FE901D81341AE1791C59275B4F6E8D9CB52" +
                "EFB9865457E7C1421E311299BD563FD203B026835C2F238B24EB69EDD1B396A5DF730CA8AFCF82843C6225337AAC7FA407604D06B85E471649D6D3DBA3672D4" +
                "BBEE619515F9F050878C44A66F558";
        byte[] byteScramble = hexToByteArray(scrambleTable);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= byteScramble[i % 896];//加解扰
        }
        return bytesToHex(bytes);
    }


    //将0~15的十进制数转换成0~F的十六进制数
    public static char toHexChar(int hexValue) {
        if (hexValue <= 9 && hexValue >= 0)
            return (char) (hexValue + '0');
        else
            return (char) (hexValue - 10 + 'A');
    }

    public static String decimalToHex(int decimal) {
        String hex = "";
        while (decimal != 0) {
            int hexValue = decimal % 16;
            hex = toHexChar(hexValue) + hex;
            decimal = decimal / 16;
        }
        return getFileAddSpace(hex);
    }

    public static String getFileAddSpace(String replace) {
        String regex = "(.{2})";
        replace = replace.replaceAll(regex, "$1 ");
        return replace;
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hex
     * @return
     */
    public static String hexString2binaryString(String hex) {
        //16进制转10进制
        StringBuffer stringBuffer = new StringBuffer(6);
        int digit;
        try {
            digit = Integer.parseInt(hex);
        } catch (Exception e) {
            digit = hex.charAt(0) - 'A' + 10;
        }

        while (digit != 0) {    //转换为二进制
            stringBuffer.append(digit % 2);
            digit /= 2;
        }

        //二进制长度小于4时补0
        while (stringBuffer.length() < 4) stringBuffer.append(0);
        return stringBuffer.reverse().toString();    //返回逆转的字符串
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit1(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    public static byte bitToByte(String byteStr) {

        int re, len;

        if (null == byteStr) {
            return 0;
        }

        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }

        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }

        return (byte) re;

    }


    public static void main(String[] args) throws IOException {
        File srcFile = new File("");

        FileChannel fileChannelSrc = new RandomAccessFile(srcFile, "r").getChannel();

        ByteBuffer rBufferSrc = ByteBuffer.allocate(32);

        while ((fileChannelSrc.read(rBufferSrc)) != -1) {
            byte[] bytes = new byte[32];
            rBufferSrc.rewind();
            rBufferSrc.get(bytes);
            rBufferSrc.clear();
            System.out.println(ByteUtil.bytesToHexString(bytes));
        }


    }

}
