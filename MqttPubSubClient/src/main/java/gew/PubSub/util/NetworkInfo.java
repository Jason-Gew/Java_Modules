package gew.pubsub.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author Jason/GeW
 */
public class NetworkInfo {

    private static InetAddress Address;

    public static String getLocalIP() {
        String localIP = null;
        try {
            Address = InetAddress.getLocalHost();
            localIP = Address.getHostAddress();

        } catch (UnknownHostException err) {
            System.err.println("Unable to get local ip: " + err.getMessage());
        }
        return localIP;
    }

    public static String getMacAddress() {
        String mac = null;
        try {
            Address = InetAddress.getLocalHost();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = networkInterfaces.nextElement();
                    byte[] macAddress = network.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < macAddress.length; i++) {
                        sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
                    }
                    mac = sb.toString();
                } catch (NullPointerException e) {

                }
            }
        } catch (UnknownHostException err) {
            System.err.println("Unable to get Mac: " + err.getMessage());
        } catch (SocketException e) {
            System.err.println("Unable to get Mac Address: " + e.getMessage());
        }
        return mac;
    }

}
