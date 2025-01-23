package com.mqttsnet.thinglinks.open.exp.example.a;

import cn.hutool.core.net.NetUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MyComponent {


    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);


    @Resource
    MyUserServicePluginImpl myUserServicePlugin;

    public MyComponent() {
        System.out.println("example-plugin-demo plugin is starting for MyComponent...");
    }

    @PostConstruct
    public void init() {
        System.out.println("--->>>>");
        log.info("start example-plugin-demo plugin...");

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("example-plugin-demo Heartbeat sent successfully at at {} with IP: {}", LocalDateTime.now(), NetUtil.getLocalhostStr());
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void aVoid() {
        scheduledExecutorService.shutdown();
    }

}
