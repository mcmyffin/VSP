package userservice.User.service;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

import Common.Abstract.MainAbstract;
import Common.Util.IPFinder;
import YellowPage.RegistrationService;
import diceservice.Dice.util.IpFinder;
import org.json.JSONObject;

import com.google.gson.Gson;
import userservice.User.entities.User;


/**
 * created by Christian Zen christian.zen@outlook.de Date of creation:
 * 26.04.2016
 */
public class UserService extends MainAbstract {


	public static int port = 4567;
	public static String ip = IpFinder.getIP();
    private static String name = "group_42";
    private static String description = "Manage Users";
    private static String service = "users";
	public static String URL = "http://" + ip + ":" + port;
	public static String URLService = URL + "/users";

    private static int counter = 0;

    public UserService(int port, String ip, String name, String description, String service, String urlService) {
        super(port, ip, name, description, service, urlService);
    }

    private static Map<String,User> userMap = new HashMap();
	/*
	 * The users service registers users of the system
	 */

    private static synchronized String getNextUserID(){
        int i = counter;
        String id = "/users/"+i;
        counter++;
        return id;
    }

    private static synchronized User getUserByID(String userID) throws Exception {
        if(!userMap.containsKey(userID)) throw new Exception("User not found");
        return userMap.get(userID);
    }

	public static void main(String[] args) {
		port(port);

        Gson gson = new Gson();
        UserService main = new UserService(port,ip,name,description,service,URLService);
        RegistrationService service = new RegistrationService(main);
        service.startRegistration();


		/*
		 * Returns list of URIs of player resources bsp:
		 * http://141.22.34.15/cnt/172.18.0.38/4567/users
		 */
		get("/users", (req, res) -> {
			res.status(200);
            res.header("Content-Type", "application/json");
			return "{\"users\" : " + gson.toJson(userMap.keySet()) + "}";
		});

		/*
		 * Registers a new player with the system
		 */
		post("/users", (req, res) -> {
			if(!req.contentType().equals("application/json")){
                res.status(400);
                return "Wrong Content-Type";
            }

			JSONObject jsonObject = new JSONObject(req.body());
			String name = jsonObject.getString("name");
			String uri = jsonObject.getString("uri");
			String id = getNextUserID();

			User newUser = new User(id, name, uri);
			userMap.put(id,newUser);
			res.status(201); // created

			 res.header("Location", URL+id);

			return "User created";
		});

		/*
		 * Returns the state of the player resource
		 * todo
		 */
		get(("/users/:userId"), (req, res) -> {
			try{

                String userId = "/users/"+req.params("userId");
                User u = getUserByID(userId);

                res.status(200);
                return gson.toJson(u);

            }catch (Exception e){
                res.status(404);
                e.printStackTrace();
                return e.getMessage();
            }
		});

		put(("/users/:userId"), (req, res) -> {
            try{

                String userId = "/users/"+req.params("userId");
                User u = getUserByID(userId);

                res.status(500);
                return "Not implemented";

            }catch (Exception e){
                res.status(404);
                e.printStackTrace();
                return e.getMessage();
            }
		});

        exception(Exception.class, (ex,req,res) -> {
            res.status(500);
            ex.printStackTrace();
            res.body("Unerwarteter FEHLER");
        });


	}

}
