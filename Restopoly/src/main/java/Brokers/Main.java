package Brokers;

import Brokers.BrokerManagerComponent.BrokerManager;
import Common.Abstract.MainAbstract;
import Common.Exceptions.WrongContentTypeException;
import Common.Util.IPFinder;

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


        post("/broker", (req, res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.status(200);

            String brokerJsonString = req.body();
            String brokerID = brokerManager.createBroker(brokerJsonString); // throws WrongFormatException
            res.header("Location",URL+brokerID);
            return "OK";
        });

        get("/broker", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String brokersJsonArray = brokerManager.getBrokers();
            return brokersJsonArray;
        });

        /**
         * returns the current broker
         */
        get("/broker/:gameID", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String gameID = req.params(":gameID");
            gameID = "/broker/"+gameID;

            String broker   = brokerManager.getbrokerById(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            return broker;
        });


        put("/broker/:gameID", (req, res) -> {
            //TODO
            return "TODO";
        });

        /**
         * returns List of available place
         */
        get("/broker/:gameID/places", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String gameID = req.params(":gameID");
            gameID = "/broker/"+gameID;

            String places = brokerManager.getBrokerPlacesByGameId(gameID);
            return places;
        });


        get("/broker/:gameID/places/:placeID", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            gameID = "/broker/"+gameID;

            String placeID = gameID+"/places/"+req.params(":placeID");

            String placeJsonSring = brokerManager.getPlaceJsonStringByID(gameID, placeID);
            return placeJsonSring;

        });

        put("/broker/:gameID/places/:placeID", (req,res) -> {
            res.status(200);
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            gameID = "/broker/"+gameID;
            String placeID = gameID+"/places/"+req.params(":placeID");

            brokerManager.updatePlaceByID(gameID, placeID, req.body());
            res.header("Location",URL+placeID);
            return brokerManager.getPlaceJsonStringByID(gameID, placeID);
        });

        //:placeID zusätzlich hinzgefügt
        get("/broker/:gameID/places/:placeID/owner", (req,res) -> {
            //owner unmöglich zu ermitteln auf welche place -> /broker/:gameID/places/:placeID/owner
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = "/broker/"+req.params(":gameID");
            String placeID = req.params(":placeID");
            placeID = "/broker/"+gameID+"/places/"+placeID;

            String owner = brokerManager.getBrokerOwnerByGameID(gameID, placeID);
            return owner;
        });

        //:placeID zusätzlich hinzgefügt
        put("/broker/:gameID/places/:placeID/owner", (req,res) -> {
            //siehe oben
            res.status(200);
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.header("Content-Type","application/json");

            String gameID = "/broker/"+req.params(":gameID");
            String placeID = gameID+"/places/"+req.params(":placeID");

            String jsonOwnerString = brokerManager.updateOwnrByGameID(gameID,placeID,req.body());
            res.header("Location",URL+placeID+"/owner");
            //TODO  -> Impl2
            return jsonOwnerString;
        });

        //:placeID zusätzlich hinzgefügt
        delete("/broker/:gameID/places/:placeID/owner", (req,res) -> {
            //siehe oben
            res.status(200);

            String gameID = "/broker/"+req.params(":gameID");
            String placeID = gameID+"/places"+req.params(":placeID");

            brokerManager.removeOwnerByGameID(gameID,placeID);
            return "OK";
            //TODO  -> Impl3

        });
        //:placeID zusätzlich hinzgefügt
        put("/broker/:gameID/places/:placeID/hypothecarycredit", (req,res) -> {
            //hypothecarycredit unmöglich zu ermitteln auf welche place -> /broker/:gameID/places/:placeID/hypothecarycredit
            res.status(200);
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.header("Content-Type","application/json");

            String gameID = "/broker/"+req.params(":gameID");
            String placeID = gameID+"/places"+req.params(":placeID");

            String jsonOwnerString = brokerManager.updateHypothecaryByGameID(gameID,placeID,req.body());
            res.header("Location",URL+placeID+"/owner");
            //TODO  -> Impl4
            return jsonOwnerString;

        });

        //:placeID zusätzlich hinzgefügt
        delete("/broker/:gameID/places/:placeID/hypothecarycredit", (req,res) -> {
            //siehe oben
            res.status(200);
            String gameID = "/broker/"+req.params(":gameID");
            String placeID = gameID+"/places"+req.params(":placeID");

            brokerManager.removeHypothecaryByGameID(gameID,placeID);
            return "OK";
            //TODO  -> Impl5
        });

        //:placeID zusätzlich hinzgefügt
        post("/broker/:gameID/places/:placeID/visit", (req,res) -> {
            //visit unmöglich zu ermitteln auf welche place -> /broker/:gameID/places/:placeID/visit
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.header("Content-Type","application/json");
            res.status(200);

            String gameID = "/broker/"+req.params(":gameID");
            String placeID = gameID+"/places/"+req.params(":placeID");
            String playerJsonString = req.body();

            String eventListJson = brokerManager.playervisitetInJson(gameID, placeID, playerJsonString); // throws WrongFormatException
            return eventListJson;
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
