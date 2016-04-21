package Games.GameManagerComponent;

import Games.Exceptions.GameNotFoundException;
import Games.Exceptions.PlayerNotFoundException;
import Games.Exceptions.WrongFormatException;
import Games.GameManagerComponent.DTO.ComponentsDTO;
import Games.GameManagerComponent.DTO.GameDTO;
import Games.GameManagerComponent.DTO.PlayerDTO;
import Games.GameManagerComponent.DTO.ServicesDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.istack.internal.NotNull;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 12.04.16.
 */
public class GameManager {

    private final Gson gson;
    private final Map<String,Game> gamesMap;
    private String gameStatusByGameId;

    public GameManager(){
        this.gson = new Gson();
        this.gamesMap = new HashMap();
    }


    private Collection<Game> getGameCollection(){
        return gamesMap.values();
    }

    private Game getGameObjectById(String id) throws GameNotFoundException {
        checkNotNull(id);
        Game g = gamesMap.get(id);
        if(g == null) throw new GameNotFoundException();
        return g;
    }

    private GameStatus getGameStatusObjectById(String id) throws GameNotFoundException {
        checkNotNull(id);
        Game g = getGameObjectById(id);
        return g.getStatus();
    }

    private Services getGameServicesObjectById(String id) throws GameNotFoundException {
        checkNotNull(id);

        Game g = getGameObjectById(id);
        return g.getServices();
    }

    private Components getGameComponentsObjectById(String id) throws GameNotFoundException {
        checkNotNull(id);
        Game g = getGameObjectById(id);
        return g.getComponents();
    }

    private Collection<Player> getPlayerCollection(String id) throws GameNotFoundException {
        checkNotNull(id);

        Game g = getGameObjectById(id);
        return g.getPlayerManager().getPlayerCollection();
    }

    private Player getPlayerObject(String gameId, String playerId) throws PlayerNotFoundException, GameNotFoundException {
        checkNotNull(gameId);
        checkNotNull(playerId);


        Game g = getGameObjectById(gameId);
        Player p = g.getPlayerManager().getPlayerById(playerId);

        return p;
    }



    private void createGame(@NotNull GameDTO gameDTO){

        String gamesId = "games/"+gamesMap.size();
        String playersId = gamesId+"/players";
        String servicesId = gamesId+"/services";
        String componentsId = gamesId+"/components";

        gameDTO.setId(gamesId);
        gameDTO.setPlayers(playersId);

        gameDTO.getComponentsDTO().setId(componentsId);
        gameDTO.getServicesDTO().setId(servicesId);

        Game g = Game.fromDTO(gameDTO);

        gamesMap.put(gamesId,g);
    }



    /*********************************/

    public String getGames(){

        Collection<GameDTO> gamesCollection = new ArrayList();
        for(Game g : getGameCollection()){
            GameDTO gameDTO = g.toDTO();
            gamesCollection.add(gameDTO);
        }

        return gson.toJson(gamesCollection);
    }


    public void createGame(String gameJsonString) throws WrongFormatException {

        try{
            GameDTO gameDTO = gson.fromJson(gameJsonString, GameDTO.class);
            createGame(gameDTO);

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException("Game format wrong");
        }
    }

    public String getGameById(String gameID) throws GameNotFoundException {
        Game g =  getGameObjectById(gameID);
        GameDTO gameDTO = g.toDTO();
        return gson.toJson(gameDTO);
    }

    public String getGameStatusByGameId(String gameID) throws GameNotFoundException {
        Game g = getGameObjectById(gameID);
        return g.getStatus().toString();
    }

    public void setGameStatusByGameId(String gameStatusByGameId) {
        throw new UnsupportedOperationException();
    }


    public String getServicesByGameId(String gameID) throws GameNotFoundException {

        Game g = getGameObjectById(gameID);
        Services s = g.getServices();
        ServicesDTO servicesDTO = s.toDTO();

        return gson.toJson(servicesDTO);
    }

    public void setServicesToGame(String gameID, String servicesStringJson) throws GameNotFoundException, WrongFormatException {

        Game g = getGameObjectById(gameID);

        try{
            String servicesID = g.getServices().getId();

            ServicesDTO servicesDTO = gson.fromJson(servicesStringJson, ServicesDTO.class);
            servicesDTO.setId(servicesID);

            Services services = Services.fromDTO(servicesDTO);
            g.setServices(services);

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    public String getComponentsByGameId(String gameID) throws GameNotFoundException {

        Game g = getGameObjectById(gameID);
        Components components = g.getComponents();
        ComponentsDTO componentsDTO = components.toDTO();

        return gson.toJson(componentsDTO);
    }

    public void setComponentsToGame(String gameID, String componentsJsonString) throws GameNotFoundException, WrongFormatException {

        Game g = getGameObjectById(gameID);

        try{
            String componentsID = g.getComponents().getId();

            ComponentsDTO componentsDTO = gson.fromJson(componentsJsonString,ComponentsDTO.class);
            componentsDTO.setId(componentsID);

            Components components = Components.fromDTO(componentsDTO);
            g.setComponents(components);

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    public String getPlayersByGameId(String gameID) throws GameNotFoundException {

        Collection<PlayerDTO> playerDTOCollection = new ArrayList();
        Collection<Player> playerCollection = getPlayerCollection(gameID);
        for(Player p : playerCollection){
            PlayerDTO playerDTO = p.toDTO();
            playerDTOCollection.add(playerDTO);
        }

        return gson.toJson(playerDTOCollection);
    }

    public String createPlayer(String gameID, String playerJsonString) throws GameNotFoundException, WrongFormatException {

        try{
            PlayerDTO playerDTO = gson.fromJson(playerJsonString,PlayerDTO.class);

            Game g =  getGameObjectById(gameID);
            PlayerManager playerManager = g.getPlayerManager();
            String playerID = playerManager.addPlayer(playerDTO);

            try{
                Player p = playerManager.getPlayerById(playerID);
                return gson.toJson(p.toDTO());
            }catch (PlayerNotFoundException ex){
                return "";
            }

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    public String getPlayer(String gameID, String playerID) throws PlayerNotFoundException, GameNotFoundException {

        Game g = getGameObjectById(gameID);
        PlayerManager playerManager = g.getPlayerManager();
        Player p = playerManager.getPlayerById(playerID);

        return gson.toJson(p.toDTO());
    }

    public void updatePlayer(String gameID, String playerID, String playerJsonString) throws PlayerNotFoundException, GameNotFoundException, WrongFormatException {

        try{
            PlayerDTO playerDTO = gson.fromJson(playerJsonString, PlayerDTO.class);
            Game g = getGameObjectById(gameID);
            PlayerManager playerManager = g.getPlayerManager();
            playerManager.updatePlayer(playerID,playerDTO);

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    public void removePlayer(String gameID, String playerID) throws GameNotFoundException, PlayerNotFoundException {

        Game g = getGameObjectById(gameID);
        PlayerManager playerManager = g.getPlayerManager();
        playerManager.removePlayer(playerID);
    }

    public boolean isPlayerReady(String gameID, String playerID) throws GameNotFoundException, PlayerNotFoundException {

        Game g = getGameObjectById(gameID);
        PlayerManager playerManager = g.getPlayerManager();
        Player p = playerManager.getPlayerById(playerID);
        return p.isReady();
    }
}
