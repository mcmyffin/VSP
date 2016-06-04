package MockupClient;

import Common.Abstract.MainAbstract;
import Common.Exceptions.WrongContentTypeException;
import Common.Exceptions.WrongFormatException;
import Common.Util.IPFinder;
import Events.EventManagerComponent.DTO.EventDTO;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

/**
 * Created by dima on 02.06.16.
 */
public class Main extends MainAbstract{

    public static int    port = 6000;
    public static String ip = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Games Manager";
    public static String service = "games";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/games";

    private static List<EventDTO> eventList = new ArrayList();
    private static Gson gson = new Gson();

    public Main(){
        super(port,ip,name,description,service,URLService);
    }

    public static void main(String[] args) {
        port(port);

        Main main = new Main();

        /**
         * Informs the player, that it is his turn
         */
        post("/client/turn",(req,res) -> {
            checkContentTypeJson(req);
            res.status(200);

            String jsonBodyString = req.body();
            JSONObject jsonObject = new JSONObject(jsonBodyString);

            if(!jsonObject.has("player")){
                throw new WrongFormatException("Body param \"player\" not found in Json-Object");
            }

            printIncommingTurn(jsonObject.getString("player"));

            return "Mokup Client: Thanks";
        });


        get("/client/event", (req,res) -> {
            res.header("Content-Type","application/json");
            return getTable();
        });


        /**
         * inform a player about a new event
         */
        post("/client/event", (req,res) -> {
            checkContentTypeJson(req);
            res.status(200);

            String jsonBodyString = req.body();
            JSONArray jsonObject = new JSONArray(jsonBodyString);

            for(Object o : jsonObject){
                EventDTO event = gson.fromJson(o.toString(),EventDTO.class);
                eventList.add(event);
            }

            printIncommingEvent(jsonObject.length());
            printTable();

            return "Mokup Client: Thanks";
        });

    }

    private static synchronized void printIncommingTurn(String player){
        System.out.println("=== ( /client/turn ) ===");
        System.out.println("\tplayer: "+player);
        System.out.println("========================");
    }

    private static synchronized void printIncommingEvent(int number){
        System.out.println("========================= (NEW EVENTS REIVED) ==================================");
        System.out.println("\tNUMBER OF EVENTS:\t"+number);
        System.out.println("================================================================================");
    }

    private static synchronized void printTable(){

        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for(int i = 0; i < eventList.size(); i++){
            EventDTO e = eventList.get(i);
            System.out.println("["+i+"]\t"+gson.toJson(e));
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    private static synchronized String getTable(){
        String txt = "<h1>EVENTS <u>["+eventList.size()+"]</u> </h1>";
        txt += "<p>----------------------------------------------------------------------------------------------------------------------------------------------------------------</p>";
        for(int i = 0; i < eventList.size(); i++){
            EventDTO e = eventList.get(i);
            txt += "<p>["+i+"]\t"+gson.toJson(e)+"</p>";
            txt += "<p>----------------------------------------------------------------------------------------------------------------------------------------------------------------</p>";
        }
        return txt;
    }

    private static void checkContentTypeJson(Request req) throws WrongContentTypeException {
        if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
    }

    private static void setResponseContentTypeJson(Response res){
        res.header("Content-Type","application/json");
    }

    private static void setLocationHeader(Response res, String URL){
        res.header("Location",URL);
    }

}
