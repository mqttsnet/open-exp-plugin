package com.mqttsnet.thinglinks.open.exp.example.udptomqtt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author lin
 * @date 2024年08月30日 17:23
 */
@RestController
@RequestMapping("/UdpClient")
public class UdpClient {
    @PostMapping("/sendMQTT")
    public void sendMQTT(@RequestParam("message") String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // 将要发送的消息转换为字节数组
            byte[] buf = message.getBytes();
            // 创建一个DatagramPacket对象，用于封装要发送的数据
            InetAddress address = InetAddress.getByName("localhost");
            String udpPort = Boot.udpPort.getDefaultValue();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.valueOf(udpPort));
            // 通过DatagramSocket发送数据包
            socket.send(packet);
            System.out.println("Message sent to server: " + message);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
