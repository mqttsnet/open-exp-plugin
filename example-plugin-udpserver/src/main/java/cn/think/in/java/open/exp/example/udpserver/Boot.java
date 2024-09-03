package cn.think.in.java.open.exp.example.udpserver;

import com.mqttsnet.thinglinks.open.exp.client.ConfigSupport;
import com.mqttsnet.thinglinks.open.exp.plugin.depend.AbstractBoot;

public class Boot extends AbstractBoot {
    /**
     * udpServer启动的端口号
     */
    public static ConfigSupport udpPort = new ConfigSupport("udp.port", "9999");
}
