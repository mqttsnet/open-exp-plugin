package com.mqttsnet.thinglinks.open.exp.example.a;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lin
 * @date 2024年08月29日 16:33
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/doTest")
    public String doTest(String param){
        return "doTest2_" + param;
    }
}
