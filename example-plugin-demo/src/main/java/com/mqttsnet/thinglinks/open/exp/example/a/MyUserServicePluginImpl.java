package com.mqttsnet.thinglinks.open.exp.example.a;

import com.mqttsnet.thinglinks.open.exp.adapter.springboot3.example.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author cxs
 */
@Slf4j
@Component
public class MyUserServicePluginImpl implements UserService {

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("example-plugin-demo plugin is starting for MyUserServicePluginImpl...");
    }

    @Override
    public void createUserExt() {
        log.info("create user ext");
    }

    @Override
    public String getName() {
        return MyUserServicePluginImpl.class.getName();
    }
}
