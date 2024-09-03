package com.mqttsnet.thinglinks.open.exp.example.a;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        System.out.println("11111");
    }

    @PostConstruct
    public void init() {
        System.out.println("--->>>>");
        log.info("start plugin...");

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void aVoid() {
        scheduledExecutorService.shutdown();
    }

}
