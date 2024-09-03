package com.mqttsnet.thinglinks.open.exp.adapter.springboot3;

import com.mqttsnet.thinglinks.open.exp.adapter.springboot3.example.UserService;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContext;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContextSpiFactory;
import com.mqttsnet.thinglinks.open.exp.client.Plugin;
import com.mqttsnet.thinglinks.open.exp.client.PluginFilter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.*;

/**
 * @version 1.0
 * @Author cxs
 * @Description
 * @date 2023/8/9
 **/
@RestController
@RequestMapping("/base")
@Slf4j
public class BaseController {
    static ThreadLocal<String> context = new InheritableThreadLocal<>();
    ExpAppContext expAppContext = ExpAppContextSpiFactory.getFirst();
    Map<String, Integer> sortMap = new HashMap<>();
    Map<String, String> pluginIdTenantIdMap = new HashMap<>();

    PluginFilter callback;

    public BaseController() {
        sortMap.put("example-plugin1_1.0.0", 1);
        sortMap.put("example-plugin2_2.0.0", 2);
        pluginIdTenantIdMap.put("example-plugin2_2.0.0", "12345");
        pluginIdTenantIdMap.put("example-plugin1_1.0.0", "12345");

        callback = new PluginFilter() {

            @Override
            public <T> List<FModel<T>> filter(List<FModel<T>> list) {
                return list;
            }
        };

    }

    @PostConstruct
    public void init() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("22");
                    List<Object> objects = ExpAppContextSpiFactory.getFirst().get(UserService.class.getName());
                    for (Object object : objects) {
                        System.out.println(object);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @RequestMapping("/hello")
    public ResModel hello() {
        return new ResModel();
    }


    @RequestMapping("/run")
    public String run(String tenantId) {
        // 上下文设置租户 id
        context.set(tenantId);
        try {
            List<UserService> userServices = expAppContext.streamOne(UserService.class);
            // first 第一个就是这个租户优先级最高的.
            Optional<UserService> optional = userServices.stream().findFirst();
            if (optional.isPresent()) {
                optional.get().createUserExt();
            } else {
                return "not found";
            }
            return "success";
        } finally {
            // 上下文删除租户 id
            context.remove();
        }
    }

    @RequestMapping("/preload")
    public Plugin preload(String path) throws Throwable {
        if (path.startsWith("http")) {
            File tempFile = File.createTempFile("exp-" + UUID.randomUUID(), ".jar");
            HttpFileDownloader.download(path, tempFile.getAbsolutePath());
            path = tempFile.getAbsolutePath();
        }

        return expAppContext.preLoad(new File(path));
    }


    @RequestMapping("/install")
    public String install(String path, String tenantId) throws Throwable {
        if (path.startsWith("http")) {
            File tempFile = File.createTempFile("exp-" + UUID.randomUUID(), ".jar");
            HttpFileDownloader.download(path, tempFile.getAbsolutePath());
            path = tempFile.getAbsolutePath();
        }

        Plugin plugin = expAppContext.load(new File(path));

        sortMap.put(plugin.getPluginId(), Math.abs(new Random().nextInt(100)));
        pluginIdTenantIdMap.put(plugin.getPluginId(), tenantId);

        return plugin.getPluginId();
    }

    @RequestMapping("/unInstall")
    public String unInstall(String pluginId) throws Exception {
        log.info("plugin id {}", pluginId);
        expAppContext.unload(pluginId);
        pluginIdTenantIdMap.remove(pluginId);
        sortMap.remove(pluginId);
        return "ok";
    }
}
