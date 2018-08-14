package cn.panda.ronda.spring.util;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.util.Assert;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author yongkang.zhang
 * created at 14/08/2018
 */
@Slf4j
public class HostIdentifierHelper {

    public static String getIpAddress() {
        String ipAddress = "";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp())
                    continue;

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while(inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet6Address)
                        continue;

                    ipAddress = inetAddress.getHostAddress();
                    log.info("获取ip地址是: {}", ipAddress);
                    if (!Strings.isNullOrEmpty(ipAddress)) break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("获取ip地址失败", e);
        }
        Assert.isTrue(!Strings.isNullOrEmpty(ipAddress), "获取ipAddress失败");

        return ipAddress;
    }
}
