package gew.logging.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Jason/Ge Wu
 */
public class NetworkInfo {

    private static InetAddress ADDRESS;

    public static String getLocalIP() {
        String localIP = null;
        try {
            if (ADDRESS == null) {
                ADDRESS = InetAddress.getLocalHost();
            }
            localIP = ADDRESS.getHostAddress();

        } catch (UnknownHostException err) {
            System.err.println("Unable to get local IP: " + err.getMessage());
        }
        return localIP;
    }

    public static String getMacAddress() {
        String mac = null;
        try {
            if (ADDRESS == null) {
                ADDRESS = InetAddress.getLocalHost();
            }
            NetworkInterface network = NetworkInterface.getByInetAddress(ADDRESS);
            byte[] macAddress = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < macAddress.length; i++) {
                sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
            }
            mac = sb.toString();

        } catch (UnknownHostException err) {
            System.err.println("Unable to get Mac: " + err.getMessage());
        } catch (SocketException e) {
            System.err.println("Unable to get Mac ADDRESS: " + e.getMessage());
        }
        return mac;
    }

}
