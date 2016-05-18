package Decks;

import Common.Abstract.MainAbstract;
import Common.Exceptions.*;
import Common.Util.IPFinder;
import Decks.DeckManagerComponent.DeckManager;
import YellowPage.RegistrationService;
import org.json.JSONException;
import spark.Request;
import spark.Response;

import java.net.URISyntaxException;

import static spark.Spark.*;

/**
 * Created by dima on 13.05.16.
 */
public class Main extends MainAbstract{

    public static int    port = 4567;
    public static String ip   = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Deck Manager";
    public static String service = "decks";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/decks";

    public Main(){
        super(port,ip,name,description,service,URLService);
    }

    public static void main(String[] args) {

        port(port);
        Main main = new Main();

        DeckManager deckManager = new DeckManager();
        RegistrationService registrationService = new RegistrationService(main);


        registrationService.startRegistration();

        /**
         * List of available deck
         */
        get("/decks", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String deckCollection = deckManager.getDecks();
            return deckCollection;
        });

        get("/decks/:gameID", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/decks/"+req.params(":gameID");
            String deckJsonString = deckManager.getDeckById(gameID);
            return deckJsonString;
        });

        /**
         * creates a new deck
         */
        post("/decks", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);
            checkContentTypeJson(req);

            String deckID = deckManager.createDeck(req.body());

            setLocationHeader(res,URL+deckID);
            return deckManager.getDeckById(deckID);
        });

        /**
         * Gets a chance card
         */
        get("/decks/:gameID/chance", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/decks/"+req.params(":gameID");
            String chanceCardJsonString = deckManager.getNextChanceCard(gameID);

            return chanceCardJsonString;
        });

        /**
         * Gets a community card
         */
        get("/decks/:gameID/community", (req, res) -> {
            setResponseContentTypeJson(res);

            String gameID = "/decks/"+req.params(":gameID");
            String communityCardJsonString = deckManager.getNextCommunityCard(gameID);

            return communityCardJsonString;
        });


        /****** EXCEPTION BEHANDLUNG *******/
        exception(WrongContentTypeException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Wrong Content-Type");
            ex.printStackTrace();
        });

        exception(JSONException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Json-Format not valid");
            ex.printStackTrace();
        });

        exception(WrongFormatException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(URISyntaxException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Bad URI Syntax");
            ex.printStackTrace();
        });

        exception(GameDecksAlreadyExistException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("GameID already exists in /decks");
            ex.printStackTrace();
        });

        exception(GameDeckNotFoundException.class, (ex, req, res) -> {
            res.status(404);// bad request
            res.body("GameID not found");
            ex.printStackTrace();
        });

        exception(DeckException.class, (ex, req, res) -> {
            res.status(404);// bad request
            res.body("Deck is empty!\nCards not found");
            ex.printStackTrace();
        });

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
