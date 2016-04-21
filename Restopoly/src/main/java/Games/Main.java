package Games;
import Games.Exceptions.*;
import Games.GameManagerComponent.GameManager;
import org.json.JSONObject;

import static spark.Spark.*;

/**
 * Created by dima on 12.04.16.
 */

public class Main {

    public static void main(String[] args) {
        GameManager gameManager = new GameManager();


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
            gameManager.createGame(gameJsonString); // throws WrongFormatException

            return "OK";
        });


        /**
         * returns the current game
         */
        get("/games/:gameID", (req,res) -> {
            res.status(200);
            String gameID = req.params(":gameID");
            String game   = gameManager.getGameById(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            return game;
        });


        /**
         * returns the status of the game (registration|running|finished)
         */
        get("/games/:gameID/status", (req,res) -> {
            res.status(200); // if found

            String gameID = req.params(":gameID");
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
            res.status(409); // Conflicting situation, such as at least one player is not ready or ending criteria not reached
            String gameID = req.params(":gameID");
            gameManager.setGameStatusByGameId(gameID);
            // TODO keine Ahnung
            return "TODO";
        });


        /**
         * Gets a services
         */
        get("/games/:gameID/services" , (req,res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();

            res.status(200);
            String gameID = req.params(":gameID");

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
            String players = gameManager.getPlayersByGameId(gameID); // muss GameNotFoundException werfen wenn nicht gefunden

            return players;
        });

        /**
         * creates a new player
         */
        post("/games/:gameID/players", (req,res) -> {
            if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();

            String gameID = req.params(":gameID");
            String player = gameManager.createPlayer(gameID,req.body()); // muss GameNotFoundException werfen wenn nicht gefunden

            if(!player.isEmpty()){
                res.status(200);
                return player;
            }else{
                res.status(409); // Conflict (Game is full)
                return "Game full";
            }
        });

        /**
         * Gets a player
         */
        get("/games/:gameID/players/:playerID", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
            String playerID = req.params(":playerID");

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
            String playerID = req.params(":playerID");

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
            String playerID = req.params(":playerID");
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
            String playerID = req.params(":playerID");

            boolean isReady = gameManager.isPlayerReady(gameID,playerID);   // muss GameNotFoundException werfen wenn nicht gefunden             // muss PlayerNotFoundException werfen wenn nicht gefunden
            return isReady;
        });

        /**
         * signals that the player is ready to start the game / is finished with his turn
         */
        put("/games/:gameID/players/:playerID/ready" , (req,res) -> {
            res.status(200);
            String gameID = req.params(":gameID");
            String playerID = req.params(":playerID");
            gameManager.signalPlayerState(gameID,playerID); // muss GameNotFoundException werfen wenn nicht gefunden
                                                            // muss PlayerNotFoundException werfen wenn nicht gefunden
            return "OK";
        });

        /**
         * gets the currently active player that shall take action
         */
        get("/games/:gameID/players/current", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");

            String gameID = req.params(":gameID");
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
            String player = gameManager.getPlayersTurn(gameID); // muss GameNotFoundException werfen wenn nicht gefunden
            // TODO -> was passiert wenn keiner den MUTEX hällt ? (momentan wird Exception PlayerNotFoundException geworfen!)
            return player;
        });

        /**
         * tries to aquire the turn mutex (player is given either as query or body parameter)
         */
        put("/games/:gameID/players/turn", (req,res) -> {
            if(req.queryMap().toMap().isEmpty() || !req.queryMap().toMap().containsKey("player")) throw new QuerryParamsNotFound();

            String gameID = req.params(":gameID");
            String playerID = req.queryParams("player");

            boolean isMutexReleased = gameManager.isMutexReleased(gameID);
            if(!isMutexReleased){
                res.status(409); // Conflict
                return "already aquired by an other player";
            }

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

            // TODO
            boolean releaseMutex = gameManager.removePlayerTurn(gameID);

            return "OK removed";
        });

        /****** EXCEPTION BEHANDLUNG *******/
        exception(GameNotFoundException.class, (ex,req,res) -> {
            res.status(404);
            res.body("Game not found");
        });

        exception(PlayerNotFoundException.class, (ex, req, res) -> {
            res.status(404);
            res.body("Player not found");
        });

        exception(QuerryParamsNotFound.class, (ex, req, res) -> {
            res.status(400);
            res.body("QuerryParameter required");
        });

        exception(WrongContentTypeException.class, (ex, req, res) -> {
            res.status(400);
            res.body("Wrong Content-Type");
        });

        exception(WrongFormatException.class, (ex, req, res) -> {
            res.status(400);
            res.body(ex.getMessage());
        });

        exception(UnsupportedOperationException.class, (ex,req,res) -> {
            res.status(400); // not implemented
            res.body("Wrong player turn");
        });


        exception(UnsupportedOperationException.class, (ex,req,res) -> {
            res.status(501); // not implemented
            res.body("Not implemented Method");
        });



    }
}
