package Games.GameManagerComponent;

import Games.Exceptions.PlayerNotFoundException;
import Games.GameManagerComponent.DTO.PlayerDTO;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 12.04.16.
 */
public class PlayerManager {

    private final String id;
    private final int maxPlayer = 8;
    private final int minPlayer = 2;
    private final Map<String,Player> playerMap;
    private final Queue<Player> playerQueue;

    public PlayerManager(String id){
        checkNotNull(id);
        this.id = id;
        this.playerMap = new HashMap();
        this.playerQueue = new LinkedList();
    }

    public synchronized String addPlayer(PlayerDTO playerDTO){
        checkNotNull(playerDTO);
        if(playerMap.size() > maxPlayer) return "";

        playerDTO.setId(id+"/"+playerMap.size());
        playerDTO.setReady("false");
        Player player = Player.fromDTO(playerDTO);

        playerMap.put(player.getId(),player);
        playerQueue.offer(player);
        return player.getId();
    }

    public synchronized void removePlayer(String playerID) throws PlayerNotFoundException {
        checkNotNull(playerID);
        if(!playerMap.containsKey(playerID)) throw new PlayerNotFoundException();
        Player p = playerMap.remove(playerID);
        playerQueue.remove(p);
    }

    public Player getPlayerById(String playerId) throws PlayerNotFoundException {
        checkNotNull(playerId);
        if(!playerMap.containsKey(playerId)) throw new PlayerNotFoundException();
        return playerMap.get(playerId);
    }

    public Collection<Player> getPlayerCollection(){
        return playerMap.values();
    }

    public String getId(){return id;}

    public synchronized void updatePlayer(String playerID, PlayerDTO playerDTO) throws PlayerNotFoundException {
        checkNotNull(playerID);
        checkNotNull(playerDTO);

        Player p = getPlayerById(playerID);
        p.setAccount(playerDTO.getAccount());
        p.setPawn(playerDTO.getPawn());
        p.setUser(playerDTO.getUser());
    }

    /**** operations ****/
    synchronized boolean isPlayersReadyToStart(){
        for(Player p : playerMap.values()){
            if(!p.isReady()) return false;
        }
        return true;
    }

    synchronized Player getNextPlayer(){
        Player p = playerQueue.poll();
        playerQueue.offer(p);
        return playerQueue.peek();
    }

    synchronized Player getCurrentPlayer(){
        return playerQueue.peek();
    }

    synchronized void resetPlayersReady(){

        // queue reset
        playerQueue.clear();
        playerQueue.addAll(playerMap.values());

        // players readyStatus reset
        for (Player p : playerMap.values()) p.setReady(false);
    }
}

class Player{
    private final String id;
    private String user;
    private String pawn;
    private String account;
    private boolean ready;


    public static Player fromDTO(PlayerDTO playerDTO) {
        checkNotNull(playerDTO);

        Player p = new Player(
                playerDTO.getId(),
                playerDTO.getUser(),
                playerDTO.getPawn(),
                playerDTO.getAccount(),
                Boolean.parseBoolean(playerDTO.getReady())
        );
        return p;
    }

    public Player(String id, String user, String pawn, String account, boolean ready) {
        this.id = id;
        this.user = user;
        this.pawn = pawn;
        this.account = account;
        this.ready = ready;
    }

    void setUser(String user) {
        this.user = user;
    }

    void setPawn(String pawn) {
        this.pawn = pawn;
    }

    void setAccount(String account) {
        this.account = account;
    }

    void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getPawn() {
        return pawn;
    }

    public String getAccount() {
        return account;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public PlayerDTO toDTO() {

        PlayerDTO dto = new PlayerDTO(
                getId(),
                getUser(),
                getPawn(),
                getAccount(),
                getId()+"/ready"
        );
        return dto;
    }
}