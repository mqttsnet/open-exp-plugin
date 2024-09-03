package cn.think.in.java.open.exp.example.udptomqtt;

/**
 * @author lin
 * @date 2024年08月30日 17:00
 */
public interface BootNettyUdpBootstrap {
    void startup(int port);

    void shutdown();
}
