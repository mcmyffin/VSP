package userservice.User.util;

public class ConstantsUser {
	public static final String HOST = "//localhost/";
//	public static final String RMI_ID = "dice";
//	public static final int RMI_PORT = 7764;
//  public static final int JETTY_1_PORT = 5000;
//  public static final int JETTY_2_PORT = 5001;

	
	public static String DICESERVICE = "http://172.18.0.16:4567/dice";
	public static String GAMESERVICE = "https://vs-docker.informatik.haw-hamburg.de/ports/19711";
	public static String BANKSERVICE = "http://172.18.0.52:4567/banks";
	public static String BANKSERVICE2 = "https://vs-docker.informatik.haw-hamburg.de/ports/19713";
	public static String BOARDSERVICE = "https://vs-docker.informatik.haw-hamburg.de/ports/19714";
	public static String STORE = "https://vs-docker.informatik.haw-hamburg.de/ports/19715";
	
	public static String DICEURI = "https://vs-docker.informatik.haw-hamburg.de/ports/19710/dice";
	public static String GAMEURI = "https://vs-docker.informatik.haw-hamburg.de/ports/19711/game";
	public static String BANKURI = "https://vs-docker.informatik.haw-hamburg.de/ports/19712/bank";
	public static String BANK2URI = "https://vs-docker.informatik.haw-hamburg.de/ports/19713/";
	public static String BOARDURI = "https://vs-docker.informatik.haw-hamburg.de/ports/19714/board";
	
	


	public static final String RESPONSE_TYPE_JSON = "application/json";
}
