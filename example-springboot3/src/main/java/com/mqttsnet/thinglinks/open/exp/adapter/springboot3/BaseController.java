package com.mqttsnet.thinglinks.open.exp.adapter.springboot3;

import com.mqttsnet.thinglinks.open.exp.adapter.springboot3.example.UserService;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContext;
import com.mqttsnet.thinglinks.open.exp.client.ExpAppContextSpiFactory;
import com.mqttsnet.thinglinks.open.exp.client.Plugin;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BaseController负责处理插件的加载、卸载和用户服务的相关请求。
 *
 * @author mqttsnet
 */
@Slf4j
@RestController
@RequestMapping("/base")
public class BaseController {

    // 线程安全的上下文存储租户ID
    private static final ThreadLocal<String> context = new InheritableThreadLocal<>();

    // ExpAppContext对象，用于插件加载和服务操作
    private final ExpAppContext expAppContext = ExpAppContextSpiFactory.getFirst();


    /**
     * 初始化方法，启动一个新线程进行一些延迟的后台操作。
     */
    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                // 模拟一些延迟操作
                Thread.sleep(1000);

                // 从ExpAppContext中获取UserService服务列表
                List<Object> userServiceList = expAppContext.get(UserService.class.getName());
                for (Object userService : userServiceList) {
                    log.info("发现UserService实例: {}", userService);
                }
            } catch (InterruptedException e) {
                log.error("初始化过程中发生中断错误", e);
                Thread.currentThread().interrupt();  // 恢复线程中断状态
            } catch (Exception e) {
                log.error("初始化时发生异常", e);
            }
        }).start();
    }

    /**
     * 简单的Hello接口，返回一个ResModel对象。
     *
     * @return ResModel对象
     */
    @RequestMapping("/hello")
    public ResModel hello() {
        log.info("hello接口被调用");
        return new ResModel();
    }

    /**
     * 根据租户ID运行相关操作，并创建用户扩展。
     *
     * @param tenantId 租户ID
     * @return 操作结果
     */
    @RequestMapping("/run")
    public String run(String tenantId) {
        log.info("run接口被调用，租户ID: {}", tenantId);
        context.set(tenantId);  // 设置租户ID到线程上下文

        try {
            // 获取租户的UserService列表并执行createUserExt
            List<UserService> userServices = expAppContext.streamOne(UserService.class);
            Optional<UserService> optionalUserService = userServices.stream().findFirst();

            if (optionalUserService.isPresent()) {
                optionalUserService.get().createUserExt();
                log.info("成功为租户 {} 执行 createUserExt", tenantId);
                return "success";
            } else {
                log.warn("未找到UserService实例，租户ID: {}", tenantId);
                return "not found";
            }
        } finally {
            // 确保租户ID被清除
            context.remove();
        }
    }

    /**
     * 预加载插件，支持从远程URL下载插件并加载。
     *
     * @param path 插件路径或URL
     * @return 加载的插件对象
     * @throws Throwable 加载过程中可能抛出的异常
     */
    @RequestMapping("/preload")
    public Plugin preload(String path) throws Throwable {
        log.info("开始预加载插件，路径: {}", path);

        if (path.startsWith("http")) {
            // 如果是URL，下载插件到临时文件
            File tempFile = File.createTempFile("exp-" + UUID.randomUUID(), ".jar");
            HttpFileDownloader.download(path, tempFile.getAbsolutePath());
            path = tempFile.getAbsolutePath();
            log.info("插件从URL下载到临时文件: {}", path);
        }

        Plugin plugin = expAppContext.preLoad(new File(path));
        log.info("插件预加载成功，插件ID: {}", plugin.getPluginId());
        return plugin;
    }

    /**
     * 安装插件，支持从远程URL下载并安装插件。
     *
     * @param path     插件路径或URL
     * @param tenantId 租户ID
     * @return 安装的插件ID
     * @throws Throwable 安装过程中可能抛出的异常
     */
    @RequestMapping("/install")
    public String install(String path, String tenantId) throws Throwable {
        log.info("开始安装插件，路径: {}, 租户ID: {}", path, tenantId);

        if (path.startsWith("http")) {
            // 如果是URL，下载插件到临时文件
            File tempFile = File.createTempFile("exp-" + UUID.randomUUID(), ".jar");
            HttpFileDownloader.download(path, tempFile.getAbsolutePath());
            path = tempFile.getAbsolutePath();
            log.info("插件从URL下载到临时文件: {}", path);
        }

        Plugin plugin = expAppContext.load(new File(path));

        log.info("插件安装成功，插件ID: {}", plugin.getPluginId());
        return plugin.getPluginId();
    }

    /**
     * 卸载插件，并清理相关的映射。
     *
     * @param pluginId 要卸载的插件ID
     * @return 卸载结果
     * @throws Exception 卸载过程中可能抛出的异常
     */
    @RequestMapping("/unInstall")
    public String unInstall(String pluginId) throws Exception {
        log.info("开始卸载插件，插件ID: {}", pluginId);

        expAppContext.unload(pluginId);
        log.info("插件卸载成功，插件ID: {}", pluginId);
        return "ok";
    }
}
