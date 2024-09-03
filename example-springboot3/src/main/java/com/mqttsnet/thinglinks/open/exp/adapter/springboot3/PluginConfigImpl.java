package com.mqttsnet.thinglinks.open.exp.adapter.springboot3;

import com.mqttsnet.thinglinks.open.exp.client.PluginConfig;

/**
 * @version 1.0
 * @Author cxs
 * @Description
 * @date 2023/8/12
 **/
public class PluginConfigImpl implements PluginConfig {
    @Override
    public String getProperty(String pluginId, String key, String defaultValue) {
        return pluginId + " --->>> " + key;
    }

}
