package Games.GameManagerComponent;

import Banks.BankManagerComponent.DTOs.AccountDTO;
import Boards.BoardManagerComponent.DTOs.PawnDTO;
import Common.Exceptions.*;
import Events.EventManagerComponent.DTO.EventDTO;
import Events.EventManagerComponent.DTO.SubscriberDTO;
import Games.GameManagerComponent.DTO.*;
import Games.Main;
import YellowPage.RegistrationService;
import YellowPage.YellowPageDTO;
import YellowPage.YellowPageService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.istack.internal.NotNull;
import org.json.JSONObject;

import java.net.URISyntaxException;
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



    private Game createGame(@NotNull GameCreateDTO gameCreateDTO){

        // create ID's
        String gamesId = "/games/"+gamesMap.size();
        String playersId = gamesId+"/players";
        String servicesId = gamesId+"/services";
        String componentsId = gamesId+"/components";

        // get Services and Components
        ComponentsDTO componentsDTO = gameCreateDTO.getComponents();
        ServicesDTO servicesDTO     = gameCreateDTO.getServices();

        // set IDS
        gameCreateDTO.setId(gamesId);
        gameCreateDTO.setPlayers(playersId);

        componentsDTO.setId(componentsId);
        servicesDTO.setId(servicesId);

        // build Game
        Game g = Game.fromDTO(gameCreateDTO);

        // add Components and Services
        g.setComponents(Components.fromDTO(componentsDTO));
        g.setServices(Services.fromDTO(servicesDTO));

        gamesMap.put(gamesId,g);
        return g;
    }


    private void createBankAccount(String gameID, String playerID) throws GameNotFoundException, ServiceNotAvaibleException {
        checkNotNull(gameID);
        checkNotNull(playerID);

        Game g = getGameObjectById(gameID);
        Components components = g.getComponents();
        if(components.getBank() == null) throw new ServiceNotAvaibleException("Bank Service not found");

        try {

            int defaultSaldo = 3000;
            Player p = g.getPlayerManager().getPlayerById(playerID);
            AccountDTO accountDTO = new AccountDTO(Main.URL+playerID,defaultSaldo);

            HttpResponse<String> response = Unirest.post(g.getComponents().getBank()+"/accounts")
                    .header("Content-Type","application/json")
                    .body(gson.toJson(accountDTO)).asString();

            if (response.getStatus() == 201 || response.getStatus() == 20) {
                String account = response.getHeaders().get("Location").get(0);
                p.setAccount(account);
            }else{
                throw new WrongResponsCodeException("Bank: post ("+g.getComponents().getBank()+"/accounts"+") responsecode != 201/200\n" +
                                                    "Body: "+response.getBody());
            }

        }catch (UnirestException|PlayerNotFoundException ex){
            ex.printStackTrace();
        } catch (WrongResponsCodeException e) {
            e.printStackTrace();
        }
    }

    private void createPawn(String gameID, String playerID) throws GameNotFoundException, ServiceNotAvaibleException {
        checkNotNull(gameID);
        checkNotNull(playerID);

        Game g = getGameObjectById(gameID);
        Components components = g.getComponents();

        if(components.getBoard() == null) throw new ServiceNotAvaibleException("Board Service not found");

        try {

            Player p = g.getPlayerManager().getPlayerById(playerID);

            HttpResponse<String> response = Unirest.post(g.getComponents().getBoard()+"/pawns")
                                                .header("Content-Type","application/json")
                                                .body("{\"player\":\""+Main.URL+playerID+"\"}")
                                                .asString();
            // if is created Response Status
            if(response.getStatus() == 201 || response.getStatus() == 200){
                String pawn = response.getHeaders().get("Location").get(0);
                p.setPawn(pawn);
            }else{
                throw new WrongResponsCodeException("Board: post ("+g.getComponents().getBoard()+"/pawns"+") responsecode != 201/200\n" +
                                                    "Body: "+response.getBody());
            }

        }catch (PlayerNotFoundException|UnirestException ex){
            ex.printStackTrace();
        } catch (WrongResponsCodeException e) {
            e.printStackTrace();
        }
    }

    private String getClientURI(String usersURI) throws ServiceNotAvaibleException {
        checkNotNull(usersURI);

        try{

            HttpResponse<String> usersResponse = Unirest.get(usersURI).asString();
            if(usersResponse.getStatus() == 200){
                // build JsonObject
                JSONObject jsonObject = new JSONObject(usersResponse.getBody());
                String clientURI = jsonObject.getString("uri");
                return clientURI;

            }else{
                throw new ServiceNotAvaibleException("Users Service unerwarteter Response-Code\n" +
                                                    "get("+usersURI+")\n" +
                                                    "status: "+usersResponse.getStatus()+"\n" +
                                                    "body: "+usersResponse.getBody());
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Users Service nicht erreichbar");
        }
    }

    private void createEventSubscriptions(String gameID,String playerID) throws PlayerNotFoundException, GameNotFoundException, ServiceNotAvaibleException {
        checkNotNull(gameID);
        checkNotNull(playerID);

        Game game = getGameObjectById(gameID);
        Player player = game.getPlayerManager().getPlayerById(playerID);
        String eventURI = game.getComponents().getEvents()+"/subscriptions";

        String clientURI = getClientURI(player.getUser())+"/event";

        EventDTO eventRegex = new EventDTO(
                game.getComponents().getGame(),
                "",
                "",
                "",
                "",
                "",
                ""
        );

        SubscriberDTO subscriber = new SubscriberDTO(null,game.getComponents().getGame(),clientURI,eventRegex);

        try{

            HttpResponse<String> response = Unirest.post(eventURI).header("Content-Type","application/json").body(gson.toJson(subscriber)).asString();
            if(response.getStatus() == 200){
                System.out.println("=== Client Subscriber success ===");
                System.out.println(response.getHeaders().getFirst("Location"));
                System.out.println("=================================");
            }else{
                throw new ServiceNotAvaibleException("Event Service unerwarteter Respon-Code\n" +
                                                    "post("+eventURI+")\n" +
                                                    "req.header(\"Content-Type\",\"application/json\")\n" +
                                                    "req.body: "+gson.toJson(subscriber)+"\n" +
                                                    "res.status: "+response.getStatus()+"\n" +
                                                    "res.body"+ response.getBody());
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Event Service nicht erreichbar");
        }
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


    public String createGame(String gameJsonString) throws WrongFormatException {

        try{
            GameCreateDTO gameDTO = gson.fromJson(gameJsonString, GameCreateDTO.class);

            Game game = createGame(gameDTO);
            Services services = game.getServices();
            Components components = game.getComponents();


            services.setGame(Main.URLService);
            components.setGame(Main.URL+game.getId());

            try{
                // get Services
                List<YellowPageDTO> groupServices = YellowPageService.getServicesByGroupName(Main.name);
                for(YellowPageDTO dto : groupServices){

                    System.out.println(dto.getUri());

                    // events
                    if(dto.getService().equals("events")) services.setEvent(dto.getUri());
                    // decks
                    if(dto.getService().equals("decks")) services.setDeck(dto.getUri());
                    // banks
                    if(dto.getService().equals("banks")) services.setBank(dto.getUri());
                    // boards
                    if(dto.getService().equals("boards")) services.setBoard(dto.getUri());
                    // broker
                    if(dto.getService().equals("broker")) services.setBroker(dto.getUri());
                    // dice
                    if(dto.getService().equals("dice")) services.setDice(dto.getUri());
                }

            }catch (UnirestException ex){
                ex.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("game",Main.URL+game.getId());
                String jsonRegistrationObject = jsonObject.toString();

                // create Components
                // dice
                if(services.getDice() != null && !services.getDice().isEmpty()){
                    components.setDice(services.getDice());
                }

                // bank
                if (services.getBank() != null && !services.getBank().isEmpty()){
                    String component = RegistrationService.sendPost(services.getBank(),jsonRegistrationObject);
                    components.setBank(component);
                }
                // board
                if(services.getBoard() != null && !services.getBoard().isEmpty()){
                    String component = RegistrationService.sendPost(services.getBoard(),jsonRegistrationObject);
                    components.setBoard(component);
                }
                // broker
                if(services.getBroker() != null && !services.getBroker().isEmpty()){
                    String component = RegistrationService.sendPost(services.getBroker(),jsonRegistrationObject);
                    components.setBroker(component);
                }
                // deck
                if(services.getDeck() != null && !services.getDeck().isEmpty()){
                    String component = RegistrationService.sendPost(services.getDeck(),jsonRegistrationObject);
                    components.setDeck(component);
                }
                if(services.getEvent() != null && !services.getEvent().isEmpty()){
                    // event
                    components.setEvent(services.getEvent());
                }
            }catch (UnirestException ex){
                ex.printStackTrace();
            }



            return game.getId();

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

    public void setGameStatusByGameId(String gameID) throws GameNotFoundException, GameStateException {

        Game g = getGameObjectById(gameID);
        g.nextGameStatus();
    }


    public String getServicesByGameId(String gameID) throws GameNotFoundException {

        Game g = getGameObjectById(gameID);
        Services s = g.getServices();
        ServicesDTO servicesDTO = s.toDTO();

        String txt = gson.toJson(servicesDTO);
        return txt;
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

    public String createPlayer(String gameID, String playerJsonString) throws GameNotFoundException, WrongFormatException, GameFullException, ServiceNotAvaibleException, PlayerAlreadyExistsException, GameStateException, PlayerNotFoundException {

        try{
            // parse player from Json to DTO
            PlayerDTO playerDTO = gson.fromJson(playerJsonString,PlayerDTO.class);

            // get Game by ID
            Game g =  getGameObjectById(gameID);

            // check game-status
            // if game status == running -> exception
            if(!g.getStatus().equals(GameStatus.REGISTRATION)){
                throw new GameStateException("Registrieren nicht erlaubt.\nGame-Status: "+g.getStatus().toString());
            }

            // get PlayerManager
            PlayerManager playerManager = g.getPlayerManager();
            // add new Player
            String playerID = playerManager.addPlayer(playerDTO);

            // Bankaccount für Player erstellen
            createBankAccount(gameID,playerID);

            // Pawn für Player erstellen
            createPawn(gameID,playerID);

            // create Event subscriptions
            createEventSubscriptions(gameID,playerID);

            return playerID;

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException("Json Syntax wrong",ex);
        } catch (URISyntaxException e) {
            throw new WrongFormatException("URI format wrong. URI must be absolute",e);
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

    public void signalPlayerState(String gameID, String playerID) throws GameNotFoundException, PlayerNotFoundException, PlayersWrongTurnException {

        Game g = getGameObjectById(gameID);
        Player p = g.getPlayerManager().getPlayerById(playerID);

        g.signalPlayerState(p);

    }

    public String getCurrentPlayer(String gameID) throws GameNotFoundException {

        Game g = getGameObjectById(gameID);
        Player player = g.getPlayerManager().getCurrentPlayer();
        PlayerDTO playerDTO = player.toDTO();

        return gson.toJson(playerDTO);
    }

    public String getPlayersTurn(String gameID) throws GameNotFoundException, PlayerNotFoundException {

        Game g = getGameObjectById(gameID);
        Player player = g.getPlayerHoldingMutex();
        PlayerDTO playerDTO = player.toDTO();

        return gson.toJson(playerDTO);
    }

    public boolean isMutexReleased(String gameID) throws GameNotFoundException {

        Game g = getGameObjectById(gameID);
        return g.isMutexReleased();
    }

    public boolean setPlayerTurn(String gameID, String playerID) throws GameNotFoundException, PlayerNotFoundException, PlayersWrongTurnException, MutexNotReleasedException {

        Game g = getGameObjectById(gameID);
        Player player = g.getPlayerManager().getPlayerById(playerID);
        return g.setMutex(player);
    }

    public void removePlayerTurn(String gameID) throws GameNotFoundException {

        // TODO neuen Player benachrichtigen, wenn er am Zug ist !!!
        Game g = getGameObjectById(gameID);
        g.removeMutex();
    }
}
