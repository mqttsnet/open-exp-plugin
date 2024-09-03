package cn.think.in.java.open.exp.example.udpserver;

import lombok.Data;

/**
 * @author lin
 * @date 2024年08月30日 17:02
 */
@Data
public class BootNettyUdpData {
    private String address;

    private String content;

    private long time_stamp;
}
