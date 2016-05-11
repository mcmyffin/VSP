package Brokers;

import Brokers.BrokerManagerComponent.BrokerManager;
import Common.Abstract.MainAbstract;
import Common.Exceptions.WrongContentTypeException;
import Common.Util.IPFinder;
import Events.EventManagerComponent.EventManager;
import YellowPage.RegistrationService;
import YellowPage.YellowPageService;
import static spark.Spark.*;
/**
 * Created by sasa on 11.05.16.
 */
public class Main extends MainAbstract{

    public static int port = 4567;
    public static String ip = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Manage estates";
    public static String service = "broker";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/broker";


    public Main(){
        super(port,ip,name,description,service,URLService);
    }

    public static void main(String[] args) {

        port(port);

        Main main = new Main();
        BrokerManager brokerManager = new BrokerManager();

        get("/broker", (req, res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String brokersJsonArray = brokerManager.getBrokers();
            return brokersJsonArray;
        });

        post("/broker", (req, res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.status(200);

            String brokerJsonString = req.body();
            String brokerID = brokerManager.createBroker(brokerJsonString); // throws WrongFormatException
            res.header("Location",URL+"/"+brokerID);
            return "OK";
        });

        /**
         * returns the current broker
         */
        get("/broker/:gameID", (req,res) -> {
            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "broker/"+gameID;

            String broker   = brokerManager.getbrokerById(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            return broker;
        });


        post("/broker/:gameID", (req, res) -> {
            //TODO
            return "TODO";
        });

        /**
         * returns List of available place
         */
        get("/broker/:gameID/places", (req,res) -> {
            res.status(200);

            String gameID = req.params(":gameID");
            gameID = "broker/"+gameID;

            String places = brokerManager.getBrokerPlacesByGameId(gameID);
            return places;
        });





        //TODO Exeptions hinzufügen

        /****** EXCEPTION BEHANDLUNG *******/
        exception(WrongContentTypeException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Wrong Content-Type");
            ex.printStackTrace();
        });

    }
}
