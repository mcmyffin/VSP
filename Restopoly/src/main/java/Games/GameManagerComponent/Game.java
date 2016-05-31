package Games.GameManagerComponent;

import Common.Exceptions.*;
import Games.GameManagerComponent.DTO.GameCreateDTO;
import Games.GameManagerComponent.DTO.GameDTO;
import Games.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 12.04.16.
 */
public class Game {

    private final String id;
    private String name;
    private PlayerManager playerManager;
    private GameStatus status;
    private Mutex mutex;
    private Services services;
    private Components components;

    public static Game fromDTO(GameCreateDTO dto){
        checkNotNull(dto);

        Game g = new Game(
                dto.getId(),
                dto.getName(),
                new PlayerManager(dto.getPlayers()),
                GameStatus.REGISTRATION,
                null,
                null
        );
        return g;
    }

    public Game(String id, String name, PlayerManager playerManager, GameStatus status, Services services, Components components) {
        checkNotNull(id);
        this.id = id;
        this.name = name;
        this.playerManager = playerManager;
        this.status = status;
        this.services = services;
        this.components = components;

        this.mutex = new Mutex();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Services getServices() {
        return services;
    }

    public Components getComponents() {
        return components;
    }

    public GameDTO toDTO(){
        return new GameDTO(
                id,
                name,
                playerManager.getId(),
                services.getId(),
                components.getId()
        );
    }

    public void setServices(Services services) {
        checkNotNull(services);
        this.services = services;
    }

    public void setComponents(Components components) {
        checkNotNull(components);
        this.components = components;
    }

    public void signalPlayerState(Player p) throws PlayersWrongTurnException {
        checkNotNull(p);

        Player currentPlayer = getPlayerManager().getCurrentPlayer();
        if(getStatus().equals(GameStatus.REGISTRATION)){

            p.setReady(true);
            tryToStartGame();

        }else if(getStatus().equals(GameStatus.RUNNING)){

            if(!currentPlayer.equals(p)) throw new PlayersWrongTurnException();
            mutex.release();

            Player nextPlayer = playerManager.getNextPlayer();
            notifyPlayersTurn(nextPlayer);

        }else{
            // TODO wenn spiel im FINISHED status
            throw new UnsupportedOperationException();
        }
    }

    public synchronized Player getPlayerHoldingMutex() throws PlayerNotFoundException {
        String playerID = mutex.getPlayerID();
        return playerManager.getPlayerById(playerID);
    }

    public synchronized boolean isMutexReleased(){
        return mutex.isReleased();
    }

    public synchronized boolean setMutex(Player p) throws PlayersWrongTurnException, MutexNotReleasedException {
        checkNotNull(p);
        if(!mutex.isReleased()){
            if(mutex.getPlayerID().equals(p.getId())) return false; // bereits erworben
            else throw new MutexNotReleasedException(); //
        }

        if(!getPlayerManager().getCurrentPlayer().equals(p)) throw new PlayersWrongTurnException();
        return mutex.acquire(p.getId());
    }

    public synchronized void removeMutex(){
        mutex.release();
    }



    public void nextGameStatus() throws GameStateException {

        if(status.equals(GameStatus.REGISTRATION)){

            if(playerManager.isPlayersReadyToStart()) tryToStartGame();
            else throw new GameStateException();

        }else if(status.equals(GameStatus.RUNNING)){

            if(isGameEndCreteriaReached()) tryToStopGame();
            else throw new GameStateException();
        }else{

            playerManager.resetPlayersReady();
            mutex.release();
            status = GameStatus.REGISTRATION;
            // todo spielbrett service reseten ?
        }
    }

    /**** operations ****/
    private void tryToStartGame(){
        if(getStatus().equals(GameStatus.REGISTRATION)){
            if(playerManager.isPlayersReadyToStart()){
                status = GameStatus.RUNNING;

                // Der Spieler der am zug ist benachrichtigen
                Player p = playerManager.getCurrentPlayer();
                notifyPlayersTurn(p);

            }
        }
    }

    private void notifyPlayersTurn(Player p){
        try{
            if(p.getUser() == null || p.getUser().isEmpty()) throw new ServiceNotAvaibleException("UserURI not found in Player");

            String userURI = p.getUser();
            HttpResponse<JsonNode> userResponse = Unirest.get(userURI).asJson();

            // check response
            if(userResponse.getStatus() != 200) throw new WrongResponsCodeException("Statuscode: "+userResponse.getStatus()+"\nBody: "+userResponse.getBody());

            JSONObject jsonObject = userResponse.getBody().getObject();
            String clientURI = jsonObject.getString("uri");

            Unirest.post(clientURI).header("Content-Type","application/json")
                                    .body("{ \"player\" : \""+ Main.URL+p.getId()+"\"}");

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void tryToStopGame(){
        // TODO
        throw new UnsupportedOperationException();
    }

    private boolean isGameEndCreteriaReached(){
        // TODO
        throw new UnsupportedOperationException();
    }

}

enum GameStatus {
    REGISTRATION("registration"),
    RUNNING("running"),
    FINISHED("finished");

    private String val;

    GameStatus(String val) {
        this.val = val;
    }

    @Override
    public String toString(){
        return this.val;
    }
}

class Mutex{

    private String playerID = "";

    synchronized boolean acquire(String playerID){
        checkNotNull(playerID);

        if(!isReleased()) return false;
        this.playerID = playerID;

        return true;
    }

    synchronized boolean isReleased(){
        return playerID.isEmpty();
    }

    synchronized void release(){
        this.playerID = "";
    }

    synchronized String getPlayerID(){
        return playerID;
    }
}
