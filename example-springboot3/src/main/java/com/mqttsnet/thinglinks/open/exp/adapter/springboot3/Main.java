package com.mqttsnet.thinglinks.open.exp.adapter.springboot3;

import java.util.List;
import java.util.Optional;

import com.mqttsnet.thinglinks.open.exp.adapter.springboot3.example.UserService;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContext;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContextSpiFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * spring boot 3 需要jdk的版本至少为17
 * VM --add-opens java.base/java.lang=ALL-UNNAMED
 **/
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.mqttsnet.thinglinks"})
@EnableAsync
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @RestController
    @RequestMapping("/hello")
    class Cont {

        ExpAppContext expAppContext = ExpAppContextSpiFactory.getFirst();

        @RequestMapping("/hi")
        public User s() {
            List<UserService> userServices = expAppContext.streamOne(UserService.class);
            Optional<UserService> first = userServices.stream().findFirst();
            if (first.isPresent()) {
                first.get().createUserExt();
            } else {
                log.info("no user service found");
            }
            return new User();
        }
    }
}
