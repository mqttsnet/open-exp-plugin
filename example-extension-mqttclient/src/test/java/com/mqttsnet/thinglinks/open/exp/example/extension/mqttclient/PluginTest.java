package com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient;

import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.util.PropertiesUtils;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.connector.MqttConnector;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.constant.MqttVersion;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.handler.MqttDelegateHandler;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Interceptor;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Intercepts;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.plugin.Invocation;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.proxy.CglibProxyFactory;
import com.mqttsnet.thinglinks.open.exp.example.extension.mqttclient.support.util.LogUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 插件相关的测试用例
 * @author mqttsnet
 */
@RunWith(JUnit4.class)
public class PluginTest {

    private static final MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();

    private static final AtomicBoolean isExecuteMqttClientIntercept = new AtomicBoolean(false);

    private static final AtomicBoolean isExecuteMqttConnectorIntercept = new AtomicBoolean(false);

    private static final AtomicBoolean isExecuteMqttDelegateHandlerIntercept = new AtomicBoolean(false);


    @BeforeClass
    public static void beforeClass() throws IOException {
        PropertiesUtils.loadTestProperties();
    }

    @After
    public void reset() {
        isExecuteMqttClientIntercept.set(false);
        isExecuteMqttConnectorIntercept.set(false);
        isExecuteMqttDelegateHandlerIntercept.set(false);
    }

    @AfterClass
    public static void afterClass() {
        mqttClientFactory.close();
    }

    @Test
    public void testMqttClientPlugin() {
        mqttClientFactory.addInterceptor(new MqttClientInterceptor());
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        String username = PropertiesUtils.getUsername();
        String password = PropertiesUtils.getPassword();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setUsername(username);
        mqttConnectParameter.setPassword(password.toCharArray());
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connectFuture();
        mqttClient.disconnect();
        Assert.assertTrue(isExecuteMqttClientIntercept.get());
    }


    @Test
    public void testMqttConnectorPlugin() {
        mqttClientFactory.addInterceptor(new MqttConnectorInterceptor());
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        String username = PropertiesUtils.getUsername();
        String password = PropertiesUtils.getPassword();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setUsername(username);
        mqttConnectParameter.setPassword(password.toCharArray());
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connectFuture();
        mqttClient.disconnect();
        Assert.assertTrue(isExecuteMqttConnectorIntercept.get());
    }


    @Test
    public void testMqttDelegateHandlerPlugin() {
        mqttClientFactory.addInterceptor(new MqttDelegateHandlerInterceptor());
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        String username = PropertiesUtils.getUsername();
        String password = PropertiesUtils.getPassword();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setUsername(username);
        mqttConnectParameter.setPassword(password.toCharArray());
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connect();
        mqttClient.disconnect();
        Assert.assertTrue(isExecuteMqttDelegateHandlerIntercept.get());
    }

    @Test
    public void testMultiplePlugin() {
        mqttClientFactory.addInterceptor(new MqttClientInterceptor());
        mqttClientFactory.addInterceptor(new MqttConnectorInterceptor());
        mqttClientFactory.addInterceptor(new MqttDelegateHandlerInterceptor());
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        String username = PropertiesUtils.getUsername();
        String password = PropertiesUtils.getPassword();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setUsername(username);
        mqttConnectParameter.setPassword(password.toCharArray());
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connect();
        mqttClient.disconnect();
        Assert.assertTrue(isExecuteMqttClientIntercept.get() && isExecuteMqttConnectorIntercept.get() && isExecuteMqttDelegateHandlerIntercept.get());
    }

    @Test
    public void testCombinationPlugin() {
        mqttClientFactory.addInterceptor(new CombinationInterceptor());
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        String username = PropertiesUtils.getUsername();
        String password = PropertiesUtils.getPassword();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setUsername(username);
        mqttConnectParameter.setPassword(password.toCharArray());
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connect();
        mqttClient.disconnect();
        Assert.assertTrue(isExecuteMqttClientIntercept.get() && isExecuteMqttConnectorIntercept.get() && isExecuteMqttDelegateHandlerIntercept.get());
    }

    @Test
    public void testCglibPlugin() {
        mqttClientFactory.setProxyFactory(new CglibProxyFactory());
        mqttClientFactory.addInterceptor(new CombinationInterceptor());
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        String username = PropertiesUtils.getUsername();
        String password = PropertiesUtils.getPassword();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setUsername(username);
        mqttConnectParameter.setPassword(password.toCharArray());
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connect();
        mqttClient.disconnect();
        Assert.assertTrue(isExecuteMqttClientIntercept.get() && isExecuteMqttConnectorIntercept.get() && isExecuteMqttDelegateHandlerIntercept.get());
    }


    @Intercepts(type = {MqttClient.class})
    private static class MqttClientInterceptor implements Interceptor {
        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            Object[] args = invocation.getArgs();
            Method method = invocation.getMethod();
            Object result = invocation.proceed();
            LogUtils.info(MqttClientInterceptor.class, "方法：" + method.getName() + "被拦截，参数：" + Arrays.toString(args) + ",返回值：" + result);
            isExecuteMqttClientIntercept.set(true);
            return result;
        }
    }


    @Intercepts(type = {MqttConnector.class})
    private static class MqttConnectorInterceptor implements Interceptor {
        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            Object[] args = invocation.getArgs();
            Method method = invocation.getMethod();
            Object result = invocation.proceed();
            LogUtils.info(MqttConnectorInterceptor.class, "方法：" + method.getName() + "被拦截，参数：" + Arrays.toString(args) + ",返回值：" + result);
            isExecuteMqttConnectorIntercept.set(true);
            return result;
        }
    }


    @Intercepts(type = {MqttDelegateHandler.class})
    private static class MqttDelegateHandlerInterceptor implements Interceptor {
        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            Object[] args = invocation.getArgs();
            Method method = invocation.getMethod();
            Object result = invocation.proceed();
            LogUtils.info(MqttDelegateHandlerInterceptor.class, "方法：" + method.getName() + "被拦截，参数：" + Arrays.toString(args) + ",返回值：" + result);
            isExecuteMqttDelegateHandlerIntercept.set(true);
            return result;
        }
    }

    @Intercepts(type = {MqttClient.class, MqttConnector.class, MqttDelegateHandler.class})
    private static class CombinationInterceptor implements Interceptor {

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            Object[] args = invocation.getArgs();
            Method method = invocation.getMethod();
            Object result = invocation.proceed();
            Object target = invocation.getTarget();
            if (target instanceof MqttClient) {
                isExecuteMqttClientIntercept.set(true);
            } else if (target instanceof MqttConnector) {
                isExecuteMqttConnectorIntercept.set(true);
            } else if (target instanceof MqttDelegateHandler) {
                isExecuteMqttDelegateHandlerIntercept.set(true);
            }
            LogUtils.info(MqttDelegateHandlerInterceptor.class, "方法：" + method.getName() + "被拦截，参数：" + Arrays.toString(args) + ",返回值：" + result);
            return result;
        }
    }


}
