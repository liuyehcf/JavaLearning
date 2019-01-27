package org.liuyehcf.akka.remote.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author chenlu
 * @date 2019/1/25
 */
public class IPUtils {
    public static String getLocalIp() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
