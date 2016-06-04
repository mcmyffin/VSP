package Boards;

import Boards.BoardManagerComponent.BoardManager;
import Common.Abstract.MainAbstract;
import Common.Exceptions.*;
import Common.Util.DebugService;
import Common.Util.IPFinder;
import YellowPage.RegistrationService;
import YellowPage.YellowPageService;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.net.URISyntaxException;

import static spark.Spark.*;

/**
 * Created by dima on 21.04.16.
 */
public class Main extends MainAbstract{

    public static int    port = 4567;
    public static String ip = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Board Manager";
    public static String service = "boards";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/boards";

    public Main(){
        super(port,ip,name,description,service,URLService);
    }

    public static void main(String[] args) {

        System.out.println("=== Boards ===");
        port(port);
        Main main = new Main();
        BoardManager boardManager = new BoardManager();

        RegistrationService registrationService = new RegistrationService(main);
        registrationService.startRegistration();

        YellowPageService.startListening();
        DebugService.start();

        /**
         * returns all active games (both running and joinable)
         */
        get("/boards", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String boardsAsJson = boardManager.getBoards();
            return boardsAsJson;
        });

        /**
         * creates a new board. A game url must be given in the body.
         */
        post("/boards", (req,res) -> {
            checkContentTypeJson(req);

            res.status(201);

            String jsonString = req.body();
            String gameID = boardManager.createBoard(jsonString);
            setLocationHeader(res,URL+gameID);

            return boardManager.getBoard(gameID);
        });

        /**
         * gets the board belonging to the game
         */
        get("/boards/:gameID", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");

            if(req.queryMap().toMap().containsKey("expanded")){
                String boaedExpandedJson = boardManager.getExpandedBoard(gameID);
                return boaedExpandedJson;
            }else{
                String boardJson = boardManager.getBoard(gameID);
                return boardJson;
            }
        });

        /**
         * places a boards
         */
        put("/boards/:gameID", (req,res) -> {
            checkContentTypeJson(req);
            throw new UnsupportedOperationException();
        } );

        /**
         * deletes the board to the game, effectivly ending the game
         */
        delete("/boards/:gameID", (req,res) -> {
            res.status(200);

            String gameID = "/boards/"+req.params(":gameID");
            boardManager.deleteBoard(gameID);

            return "OK";
        } );

        /**
         * returns a list of all player positions
         */
        get("/boards/:gameID/pawns", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");
            String pawnList = boardManager.getPawnListByGameID(gameID);

            return pawnList;
        });

        /**
         * creates a new pawn
         */
        post("/boards/:gameID/pawns", (req,res) -> {
            checkContentTypeJson(req);
            res.status(201);

            String jsonBody = req.body();
            String gameID = "/boards/"+req.params(":gameID");

            String pawnID = boardManager.createPawn(gameID,jsonBody);

            setLocationHeader(res, URL+pawnID);
            return "OK";
        });

        /**
         * Gets a pawn by ID
         */
        get("/boards/:gameID/pawns/:pawnID", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");
            String pawnID = gameID+"/pawns/"+req.params("pawnID");

            String pawnJsonString = boardManager.getPawnByGameID(gameID,pawnID);
            return pawnJsonString;
        });

        /**
         * places a pawns
         */
        put("/boards/:gameID/pawns/:pawnID", (req,res) -> {
            res.status(200);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");
            String pawnID = gameID+"/pawns/"+req.params("pawnID");
            String jsonPawnString = req.body();

            String pawnJsonString = boardManager.updatePawnByGameID(gameID,pawnID, jsonPawnString);
            return pawnJsonString;
        });

        /**
         * removes a pawn from the board
         */
        delete("/boards/:gameID/pawns/:pawnID", (req,res) -> {
            res.status(200);
            String gameID = "/boards/"+req.params(":gameID");
            String pawnID = gameID+"/pawns/"+req.params("pawnID");

            boardManager.removePawnByGameID(gameID,pawnID);
            return "OK";
        });

        /**
         * moves a pawn relative to its current position
         */
        post("/boards/:gameID/pawns/:pawnID/move", (req,res) -> {
            res.status(200);
            checkContentTypeJson(req);

            String gameID = "/boards/"+req.params(":gameID");
            String pawnID = gameID+"/pawns/"+req.params("pawnID");

            JSONObject jsonObject = new JSONObject(req.body());
            int number = Integer.parseInt(jsonObject.get("number").toString());

            boardManager.movePawn(gameID,pawnID,number);

           return "OK";
        });

        /**
         * returns all rolls done for the paw
         */
        get("/boards/:gameID}/pawns/:pawnID/roll", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");
            String pawnID = gameID+"/pawns/"+req.params("pawnID");

            String pawnRollsJsonString = boardManager.getPawnRollsByGameID(gameID,pawnID);
            return pawnRollsJsonString;
        });

        /**
         * rolls on the board and executes associated actions
         */
        post("/boards/:gameID/pawns/:pawnID/roll", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");
            String pawnID = gameID+"/pawns/"+req.params("pawnID");

            String eventListJsonString =  boardManager.pawnRoll(gameID,pawnID);
            return eventListJsonString;
        });

        /**
         * List of available place
         */
        get("/boards/:gameID/places", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");
            String placesJsonStringList = boardManager.getPlaces(gameID);

            return placesJsonStringList;
        });

        /**
         * Gets a places
         */
        get("/boards/:gameID/places/:placeID", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String gameID = "/boards/"+req.params(":gameID");
            String placeID = gameID+"/places/"+req.params(":placeID");

            String placeJsonString = boardManager.getPlace(gameID,placeID);

            return placeJsonString;
        });

        /**
         * places a place
         */
        put("/boards/:gameID/places/:placeID", (req,res) -> {
            checkContentTypeJson(req);
            throw new UnsupportedOperationException();
        });


        /**** Exceptions ****/

        exception(UnsupportedOperationException.class, (ex,req,res) -> {
            res.status(501); // not implemented
            res.body("Not implemented Method");
            ex.printStackTrace();
        });


        exception(BoardNotFoundException.class, (ex, req, res) -> {
            res.status(404);// not Found
            res.body("Board not found");
            ex.printStackTrace();
        });

        exception(BoardAlreadyExistsException.class, (ex, req, res) -> {
            res.status(409);// conflict
            res.body("Board already exist");
            ex.printStackTrace();
        });

        exception(PlaceNotFoundException.class, (ex, req, res) -> {
            res.status(404);// not Found
            res.body("Place not found");
            ex.printStackTrace();
        });

        exception(PawnNotFoundException.class, (ex, req, res) -> {
            res.status(404);// not Found
            res.body("Place not found");
            ex.printStackTrace();
        });

        exception(WrongFormatException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(WrongContentTypeException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(ServiceNotAvaibleException.class, (ex, req, res) -> {
            res.status(409);// conflict
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(NumberFormatException.class, (ex, req, res) -> {
            res.status(409);// conflict
            res.body("Number type not int");
            ex.printStackTrace();
        });

        exception(URISyntaxException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("URI Syntax wrong");
            ex.printStackTrace();
        });

        exception(ServiceNotAvaibleException.class, (ex, req, res) -> {
            res.status(500);// Internal Error
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(MutexNotReleasedException.class, (ex, req, res) -> {
            res.status(409);// conflict
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(Exception.class, (ex, req, res) -> {
            res.status(409);// conflict
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        /********************/

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
