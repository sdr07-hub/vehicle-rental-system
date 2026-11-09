package com.vehiclerental.portal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

/**
 * After the app starts, prints the address to type on a phone that is on the same Wi-Fi,
 * for example:  Open on your phone: http://192.168.1.5:8081
 */
@Component
public class LanAddressPrinter implements ApplicationRunner {

    @Value("${server.port:8080}")
    private int port;

    @Override
    public void run(ApplicationArguments args) {
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address && address.isSiteLocalAddress()) {
                        System.out.println(">>> Open on your phone: http://" + address.getHostAddress() + ":" + port
                                + "   (" + ni.getDisplayName() + ")");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(">>> Could not read the network address. Run 'ipconfig' to find it.");
        }
    }
}
