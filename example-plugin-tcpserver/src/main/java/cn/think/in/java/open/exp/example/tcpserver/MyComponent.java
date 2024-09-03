package cn.think.in.java.open.exp.example.tcpserver;

import com.mqttsnet.thinglinks.open.exp.client.ConfigSupport;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MyComponent {
    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    TcpServer tcpServer = null;

    @PostConstruct
    public void init() {
        String tcpPort = Boot.tcpPort.getDefaultValue();
        log.info("start TcpServer plugin...");
        new Thread(() -> {
            try {
                if (tcpServer == null){
                    tcpServer = new TcpServer();
                }
                tcpServer.start(Integer.valueOf(tcpPort));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
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
        if (tcpServer != null){
            tcpServer.shutdown();
        }
        scheduledExecutorService.shutdown();
    }
}
