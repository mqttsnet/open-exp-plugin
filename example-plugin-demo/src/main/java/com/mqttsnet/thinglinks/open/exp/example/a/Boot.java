package com.mqttsnet.thinglinks.open.exp.example.a;

import com.mqttsnet.thinglinks.open.exp.client.ConfigSupport;
import com.mqttsnet.thinglinks.open.exp.plugin.depend.AbstractBoot;

public class Boot extends AbstractBoot {

    public static ConfigSupport configSupport = new ConfigSupport("aaa", "111");
}
