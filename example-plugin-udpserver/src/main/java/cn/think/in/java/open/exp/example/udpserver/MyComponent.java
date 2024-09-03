package cn.think.in.java.open.exp.example.udpserver;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MyComponent {
    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    BootNettyUdpBootstrapThread bootNettyUdpBootstrapThread = null;
    @PostConstruct
    public void init() {
        String udpPort = Boot.udpPort.getDefaultValue();
        log.info("start UdpServer plugin...");
        if (bootNettyUdpBootstrapThread == null){
            bootNettyUdpBootstrapThread = new BootNettyUdpBootstrapThread(Integer.valueOf(udpPort));
        }
        bootNettyUdpBootstrapThread.start();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void aVoid() {
        log.info("插件关闭了");
        if (bootNettyUdpBootstrapThread != null){
            bootNettyUdpBootstrapThread.shutdown();
        }
        scheduledExecutorService.shutdown();
    }
}
