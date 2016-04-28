package Boards;

import Boards.BoardManagerComponent.BoardManager;
import Common.Abstract.MainAbstract;
import Common.Exceptions.WrongContentTypeException;
import Common.Exceptions.WrongFormatException;
import Common.Util.IPFinder;
import YellowPage.RegistrationService;
import YellowPage.YellowPageService;
import spark.Request;

import static spark.Spark.*;

/**
 * Created by dima on 21.04.16.
 */
public class Main extends MainAbstract{

    public static int port = 4567;
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

        port(port);
        Main main = new Main();
        BoardManager boardManager = new BoardManager();
        RegistrationService registrationService = new RegistrationService(main);

        registrationService.startRegistration();
        YellowPageService.startListening();

        /**
         * returns all active games (both running and joinable)
         */
        get("/boards", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String boardsAsJson = boardManager.getBoards();
            return boardsAsJson;
        });

        /**
         * creates a new board. A game url must be given in the body.
         */
        post("/boards", (req,res) -> {
            if(!isContentTypeApplicationJson(req)) throw new WrongContentTypeException();

            res.status(201);

            String jsonString = req.body();
            String boardID = boardManager.createBoard(jsonString);
            res.header("Location",URL+"/"+boardID);
            return "OK";
        });

        /**
         * gets the board belonging to the game
         */
        get("/boards/:gameID", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            throw new UnsupportedOperationException();
        });

        /**
         * places a boards
         */
        put("/boards/:gameID", (req,res) -> {
            if(!isContentTypeApplicationJson(req)) throw new WrongContentTypeException();
            throw new UnsupportedOperationException();
        } );

        /**
         * deletes the board to the game, effectivly ending the game
         */
        delete("/boards/:gameID", (req,res) -> {
            // TODO RESPONSE WELCHEN STATUSCODE ??
            throw new UnsupportedOperationException();
        } );

        /**
         * returns a list of all player positions
         */
        get("/boards/:boardID/pawns", (req,res) -> {
            throw new UnsupportedOperationException();
        });

        /**
         * creates a new pawn
         */
        post("/boards/:boardID/pawns", (req,res) -> {
            if(!isContentTypeApplicationJson(req)) throw new WrongContentTypeException();
            res.status(201);

            String jsonBody = req.body();
            String pawnID = boardManager.createPawn(jsonBody);

            res.header("Location",URL+"/"+pawnID);
            return "OK";
        });

        /**
         * Gets a pawns
         */
        get("/boards/:gameID/pawns/:pawnID", (req,res) -> {
            res.status(200);
            throw new UnsupportedOperationException();
        });

        /**
         * places a pawns
         */
        put("/boards/:gameID/pawns/:pawnID", (req,res) -> {
            // TODO RESPONSE WELCHEN STATUSCODE ??
            throw new UnsupportedOperationException();
        });

        /**
         * removes a pawn from the board
         */
        delete("/boards/:gameID/pawns/:pawnID", (req,res) -> {
            // TODO RESPONSE WELCHEN STATUSCODE ??
            throw new UnsupportedOperationException();
        });

        /**
         * moves a pawn relative to its current position
         */
        post("/boards/:gameID/pawns/:pawnID/move", (req,res) -> {
            // TODO RESPONSE WELCHEN STATUSCODE ??
           throw new UnsupportedOperationException();
        });

        /**
         * returns all rolls done for the paw
         */
        get("/boards/:gameID}/pawns/:pawnID/roll", (req,res) -> {
            res.status(200);
            throw new UnsupportedOperationException();
        });

        /**
         * rolls on the board and executes associated actions
         */
        post("/boards/:gameID}/pawns/:pawnID/roll", (req,res) -> {
            // TODO RESPONSE WELCHEN STATUSCODE ??
            throw new UnsupportedOperationException();
        });

        /**
         * List of available place
         */
        get("/boards/:gameID/places", (req,res) -> {
            res.status(200);
            throw new UnsupportedOperationException();
        });

        /**
         * Gets a places
         */
        get("/boards/:gameID/places/:placeID", (req,res) -> {
            res.status(200);
            throw new UnsupportedOperationException();
        });

        /**
         * places a places
         */
        put("/boards/:gameID/places/:placeID", (req,res) -> {
            if(!isContentTypeApplicationJson(req)) throw new WrongContentTypeException();
            throw new UnsupportedOperationException();
        });





        exception(UnsupportedOperationException.class, (ex,req,res) -> {
            res.status(501); // not implemented
            res.body("Not implemented Method");
            ex.printStackTrace();
        });

        exception(WrongFormatException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

    }

    private static boolean isContentTypeApplicationJson(Request req){
        return req.headers("Content-Type").equals("application/json");
    }
}
