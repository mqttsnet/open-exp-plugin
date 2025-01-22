package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拦截器注解
 * @author mqttsnet
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercepts {

    /**
     * 拦截的类
     *
     * @return 拦截的类
     */
    Class<?>[] type();
}
