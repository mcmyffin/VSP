package diceservice.Dice.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class IpFinder {
	public static String getIP(){
        try {
            return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
            return "UNKNOWN-ADRESS";
        }
    }
}
