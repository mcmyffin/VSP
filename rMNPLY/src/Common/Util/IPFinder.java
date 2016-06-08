package Common.Util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Created by dima on 27.04.16.
 */
public class IPFinder {
    public static String getIP(){
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "UNKNOWN-ADRESS";
        }
    }
}