import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * @author  Jason Ge Wu
 * @since   2019-09-06
 * @version 1.0.2
 */
public final class NetworkUtil {


    private static final Logger log = Logger.getGlobal();

    private static final String DEFAULT_IP = "127.0.0.1";

    private static final String DEFAULT_MAC = "00-00-00-00-00-00";



    public static String getLocalHostName() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostName();

        } catch (UnknownHostException err) {
            log.info(String.format("Get Localhost Name Failed: %s", err.getMessage()));
            return "";
        }
    }


    /**
     * Common   Method to Get Localhost IP Address
     * @return  Localhost IP Address
     */
    public static String getLocalHostIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();

        } catch (UnknownHostException err) {
            log.info(String.format("Get Localhost IP Failed: %s, System Return Default [%s]",
                    err.getMessage(), DEFAULT_IP));
            return DEFAULT_IP;
        }
    }

    public static String getIpByHost(String hostname) throws UnknownHostException {
        if (hostname == null || hostname.trim().isEmpty()) {
            return getLocalHostName();
        }
        InetAddress address = InetAddress.getByName(hostname);
        return address.getHostAddress();
    }


    /**
     * Common   Method to Get Localhost Mac Address
     * @return  Localhost Mac Address
     */
    public static String getMacAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(address);
            byte[] macAddress = network.getHardwareAddress();
            if (macAddress == null || macAddress.length == 0) {
                return DEFAULT_MAC;
            }
            return convertMacAddressToString(macAddress);

        } catch (UnknownHostException | SocketException err) {
            log.info(String.format("Get Mac Address Failed: %s, System Return Default [%s]",
                    err.getMessage(), DEFAULT_MAC));
            return DEFAULT_MAC;
        }
    }


    public static String getMacAddress(String hostname) throws UnknownHostException, SocketException {
        if (hostname == null || hostname.trim().isEmpty()) {
            return getMacAddress();
        }
        InetAddress address = InetAddress.getByName(hostname);
        if (address == null) {
            throw new UnknownHostException("InetAddress is Null");
        }
        NetworkInterface network = NetworkInterface.getByInetAddress(address);
        byte[] macAddress = network.getHardwareAddress();
        if (macAddress == null || macAddress.length == 0) {
            throw new IllegalStateException("Mac Address is Invalid");
        }
        return convertMacAddressToString(macAddress);
    }


    private static String convertMacAddressToString(byte[] macAddress) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < macAddress.length; i++) {
            sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }

}
