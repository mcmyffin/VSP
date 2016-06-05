package Games;
import Common.Abstract.MainAbstract;
import Common.Exceptions.*;
import Common.Util.DebugService;
import Common.Util.IPFinder;
import Games.GameManagerComponent.GameManager;
import YellowPage.RegistrationService;
import YellowPage.YellowPageService;

import static spark.Spark.*;

/**
 * Created by dima on 12.04.16.
 */

public class Main extends MainAbstract{

    public static int    port = 4567;
    public static String ip = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Games Manager";
    public static String service = "games";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/games";

    public Main(){
        super(port,ip,name,description,service,URLService);
    }

    public static void main(String[] args) {


        System.out.println("=== Games ===");

        port(port);
        Main main = new Main();
        GameManager gameManager = new GameManager();

        RegistrationService registrationService = new RegistrationService(main);
        registrationService.startRegistration();

        YellowPageService.startListening();
        DebugService.start();
        /**
         * returns all available games
         */
        get("/games",(req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String games = gameManager.getGames();
            return games;
        });

        /**
         * creates a new game. Values may optionaly be set within the body parameter.
         */
        post("/games", (req,res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.status(201);

            String gameJsonString = req.body();
            String gameID = gameManager.createGame(gameJsonString); // throws WrongFormatException
            res.header("Location",URL+gameID);
            return "OK";
        });


        /**
         * returns the current game
         */
        get("/games/:gameID", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String game   = gameManager.getGameById(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            return game;
        });


        /**
         * returns the status of the game (registration|running|finished)
         */
        get("/games/:gameID/status", (req,res) -> {
            res.status(200); // if found
            res.header("Content-Type","application/json");
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String status = gameManager.getGameStatusByGameId(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            return status;
        });

        /**
         * sets the status of the game. registration : the initial state after creation.
         * PlayerManager may join the game.
         * Set automatically running : may be set when all players are ready to begin the game finished :
         *              after an ending criteria is reached, the game is set into the state finished
         */
        put("/games/:gameID/status", (req,res) -> {

            res.status(200); // The change has been applied
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            gameManager.setGameStatusByGameId(gameID);

            return "The change has been applied";
        });


        /**
         * Gets a services
         */
        get("/games/:gameID/services" , (req,res) -> {
            res.header("Content-Type","application/json");
            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String services = gameManager.getServicesByGameId(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            return services;
        });

        /**
         * places a services
         */
        put("/games/:gameID/services" , (req,res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();

            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            gameManager.setServicesToGame(gameID, req.body()); // muss GameNotFoundException werfen wenn nicht gefunden
            return "OK";
        });

        /**
         * Gets a components
         */
        get("/games/:gameID/components", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String components = gameManager.getComponentsByGameId(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            return components;
        });

        /**
         * places a components
         */
        put("/games/:gameID/components", (req,res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();

            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            gameManager.setComponentsToGame(gameID,req.body());  // muss GameNotFoundException werfen wenn nicht gefunden

            return "OK";
        });

        /**
         * returns all joined players
         */
        get("/games/:gameID/players", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String players = gameManager.getPlayersByGameId(gameID); // muss GameNotFoundException werfen wenn nicht gefunden

            return players;
        });

        /**
         * creates a new player
         */
        post("/games/:gameID/players", (req,res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
            res.status(200);

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String playerID = gameManager.createPlayer(gameID,req.body()); // muss GameNotFoundException werfen wenn nicht gefunden
            res.header("Location",URL+playerID);

            return "Player created";
        });

        /**
         * gets the currently active player that shall take action
         */
        get("/games/:gameID/players/current", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            System.out.println("_____");

            String player = gameManager.getCurrentPlayer(gameID);   // muss GameNotFoundException werfen wenn nicht gefunden
            return player;
        });

        /**
         * gets the player holding the turn mutex
         */
        get("/games/:gameID/players/turn", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String player = gameManager.getPlayersTurn(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            // TODO -> was passiert wenn keiner den MUTEX hällt ? (momentan wird 404 Exception PlayerNotFoundException geworfen!)
            return player;
        });

        /**
         * tries to aquire the turn mutex (player is given either as query or body parameter)
         */
        put("/games/:gameID/players/turn", (req,res) -> {
            if(req.queryMap().toMap().isEmpty() || !req.queryMap().toMap().containsKey("player")) throw new QuerryParamsNotFoundException();

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String playerID = req.queryParams("player");

            boolean isAquired = gameManager.setPlayerTurn(gameID,playerID);

            if(isAquired){
                res.status(201); // Created
                return "aquired the mutex";
            }else{
                res.status(200); // Ok
                return "already holding the mutex";
            }
        });

        /**
         * releases the mutex
         */
        delete("/games/:gameID/players/turn",(req,res) -> {

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            gameManager.removePlayerTurn(gameID);

            return "OK removed";
        });

        /**
         * Gets a player
         */
        get("/games/:gameID/players/:playerID", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String playerID = req.params(":playerID");
            playerID = gameID+"/players/"+playerID;

            String player = gameManager.getPlayer(gameID,playerID); // muss GameNotFoundException werfen wenn nicht gefunden
                                                                    // muss PlayerNotFoundException werfen wenn nicht gefunden
            return player;
        });

        /**
         * places a player
         */
        put("/games/:gameID/players/:playerID", (req,res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();

            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String playerID = req.params(":playerID");
            playerID = gameID+"/players/"+playerID;

            gameManager.updatePlayer(gameID,playerID,req.body());   // muss GameNotFoundException werfen wenn nicht gefunden
                                                                    // muss PlayerNotFoundException werfen wenn nicht gefunden
            return "OK";
        });

        /**
         * Removes the player from the game
         */
        delete("/games/:gameID/players/:playerID", (req,res) -> {

            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String playerID = req.params(":playerID");
            playerID = gameID+"/players/"+playerID;

            gameManager.removePlayer(gameID,playerID);  // muss GameNotFoundException werfen wenn nicht gefunden
                                                        // muss PlayerNotFoundException werfen wenn nicht gefunden
            return "OK";
        });

        /**
         * tells if the player is ready to start the game
         */
        get("/games/:gameID/players/:playerID/ready" , (req,res) -> {
            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String playerID = req.params(":playerID");
            playerID = gameID+"/players/"+playerID;

            boolean isReady = gameManager.isPlayerReady(gameID,playerID);   // muss GameNotFoundException werfen wenn nicht gefunden             // muss PlayerNotFoundException werfen wenn nicht gefunden
            return isReady;
        });

        /**
         * signals that the player is ready to start the game / is finished with his turn
         */
        put("/games/:gameID/players/:playerID/ready" , (req,res) -> {
            res.status(200);
            String gameID = req.params(":gameID");
            gameID = "/games/"+gameID;

            String playerID = req.params(":playerID");
            playerID = gameID+"/players/"+playerID;

            gameManager.signalPlayerState(gameID,playerID); // muss GameNotFoundException werfen wenn nicht gefunden
                                                            // muss PlayerNotFoundException werfen wenn nicht gefunden
            return "OK";
        });


        /****** EXCEPTION BEHANDLUNG *******/
        exception(GameNotFoundException.class, (ex, req, res) -> {
            res.status(404);
            res.body("Game not found");
            ex.printStackTrace();
        });

        exception(PlayerNotFoundException.class, (ex, req, res) -> {
            res.status(404);
            res.body("Player not found");
            ex.printStackTrace();
        });

        exception(QuerryParamsNotFoundException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("QuerryParameter required");
            ex.printStackTrace();
        });

        exception(WrongContentTypeException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Wrong Content-Type");
            ex.printStackTrace();
        });

        exception(WrongFormatException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(PlayersWrongTurnException.class, (ex, req, res) -> {
            res.status(409); // Conflict
            res.body("Wrong player turn");
            ex.printStackTrace();
        });

        exception(MutexNotReleasedException.class, (ex, req, res) -> {
            res.status(409); // Conflict
            res.body("already aquired by an other player");
            ex.printStackTrace();
        });

        exception(GameStateException.class, (ex, req, res) -> {
            res.status(409); // Conflict
            res.body(ex.getMessage());
            ex.printStackTrace();
        });


        exception(GameFullException.class, (ex,req,res) -> {
            res.status(409); // Conflict (Game is full)
            res.body("Maximum player count reached!");
            ex.printStackTrace();
        });

        exception(ServiceNotAvaibleException.class, (ex, req, res) -> {
            res.status(500);// not found
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(Exception.class, (ex, req, res) -> {
            res.status(500);//
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

    }
}
