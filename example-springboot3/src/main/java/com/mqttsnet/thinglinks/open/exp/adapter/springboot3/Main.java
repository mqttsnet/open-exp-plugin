package com.mqttsnet.thinglinks.open.exp.adapter.springboot3;

import com.mqttsnet.thinglinks.open.exp.adapter.springboot3.example.UserService;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContext;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContextSpiFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * spring boot 3 需要jdk的版本至少为17
 * VM --add-opens java.base/java.lang=ALL-UNNAMED
 **/
@SpringBootApplication
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
                // todo
            }
            return new User();
        }
    }
}
