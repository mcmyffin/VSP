package Games.GameManagerComponent;

import Games.Exceptions.PlayerNotFoundException;
import Games.Exceptions.PlayerSequenceWrongException;
import Games.GameManagerComponent.DTO.GameDTO;

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

    public static Game fromDTO(GameDTO dto){
        checkNotNull(dto);

        Game g = new Game(
                dto.getId(),
                dto.getName(),
                new PlayerManager(dto.getPlayers()),
                GameStatus.REGISTRATION,
                Services.fromDTO(dto.getServicesDTO()),
                Components.fromDTO(dto.getComponentsDTO())
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
                services.toDTO(),
                components.toDTO()
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

    public void signalPlayerState(Player p) {
        checkNotNull(p);

        if(getStatus().equals(GameStatus.REGISTRATION)){
            p.setReady(true);
            tryToStartGame();
        }else if(getStatus().equals(GameStatus.RUNNING)){
            playerManager.getNextPlayer();
        }else{
            // TODO wenn spiel im FINISHED status
        }
    }

    public synchronized Player getPlayerHoldingMutex() throws PlayerNotFoundException {
        String playerID = mutex.getPlayerID();
        return playerManager.getPlayerById(playerID);
    }

    public synchronized boolean isMutexReleased(){
        return mutex.isReleased();
    }

    public synchronized boolean setMutex(Player p) throws PlayerSequenceWrongException {
        checkNotNull(p);
        if(!mutex.getPlayerID().equals(p.getId())) throw new PlayerSequenceWrongException();
        if(!getPlayerManager().getCurrentPlayer().equals(p)) throw new PlayerSequenceWrongException();

        if(mutex.getPlayerID().equals(p.getId())) return false;
        return mutex.acquire(p.getId());
    }

    /**** operations ****/
    private void tryToStartGame(){
        if(getStatus().equals(GameStatus.REGISTRATION)){
            if(playerManager.isPlayersReadyToStart()){
                status = GameStatus.RUNNING;
                // TODO
            }
        }
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

    private String playerID;

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
