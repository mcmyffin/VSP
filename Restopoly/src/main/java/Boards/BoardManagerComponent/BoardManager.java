package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.DTOs.BoardDTO;
import Boards.BoardManagerComponent.DTOs.PawnDTO;
import Boards.BoardManagerComponent.Util.InitializeService;
import Common.Exceptions.*;
import Common.Util.URIObject;
import Common.Util.URIParser;
import Decks.DeckManagerComponent.DTOs.CardDTO;
import Events.EventManagerComponent.DTO.EventDTO;
import Games.GameManagerComponent.DTO.ComponentsDTO;
import Games.GameManagerComponent.DTO.PlayerDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 21.04.16.
 */
public class BoardManager {

    private final Gson gson;
    private final InitializeService initializeService;
    private final Map<String,Board> boardGameIDMap; // Map<GameID,Board>

    public BoardManager() {
        this.gson = new Gson();
        this.initializeService = new InitializeService();
        this.boardGameIDMap = new HashMap();
    }


    private synchronized Board getBoardObjectByGameId(String gameID) throws BoardNotFoundException {
        checkNotNull(gameID);

        if(!boardGameIDMap.containsKey(gameID)) throw new BoardNotFoundException();
        return boardGameIDMap.get(gameID);
    }

    private synchronized void deleteBoardByGameId(String gameID) throws BoardNotFoundException {
        checkNotNull(gameID);

        Board board = getBoardObjectByGameId(gameID);

        boardGameIDMap.remove(board.getId());
    }

    private void setPlayerMutex(Pawn pawn,Board board) throws ServiceNotAvaibleException, MutexNotReleasedException {
        checkNotNull(pawn);
        checkNotNull(board);


        try {
            // get PlayerID
            String playerURI = pawn.getPlayer();
            HttpResponse<String> playerResponse = Unirest.get(playerURI).asString();

            // Wenn player nicht gefunden oder Games Service einen Fehler macht
            if(playerResponse.getStatus() != 200){
                throw new ServiceNotAvaibleException("Game Service unerwarteter Response-Code\n" +
                                                     "get("+playerURI+")\n" +
                                                     "status: "+playerResponse.getStatus()+"\n" +
                                                     "body: "+playerResponse.getBody());
            }

            PlayerDTO playerDTO = gson.fromJson(playerResponse.getBody(),PlayerDTO.class);
            String playerID = playerDTO.getId();

            // try to aquire mutex
            HttpResponse<String> gamesAquireMutexResponse   = Unirest.put(board.getGameURI().getAbsoluteURI()+"/players/turn")
                    .queryString("player",playerID).asString();

            if(gamesAquireMutexResponse.getStatus() == 200){
                // player already holding mutex -> OK
            }else if(gamesAquireMutexResponse.getStatus() == 201){
                // player aquire mutex success
            }else if(gamesAquireMutexResponse.getStatus() == 409){
                // another player holding mutex
                throw new MutexNotReleasedException("Mutex already aquired by an other player");
            }else{
                   throw new ServiceNotAvaibleException("Game Service unerwarteter Response-Code\n" +
                                                        "get("+board.getGameURI().getAbsoluteURI()+"/players/turn)\n" +
                                                        "status: "+gamesAquireMutexResponse.getStatus()+"\n" +
                                                        "body:  "+gamesAquireMutexResponse.getBody());
            }

        } catch (UnirestException e) {
            throw new ServiceNotAvaibleException("Game Service ist nicht erreichbar",e);
        }
    }

    private boolean isPlayersTurn(Pawn pawn, Board board) throws ServiceNotAvaibleException, WrongFormatException {
        checkNotNull(pawn);
        checkNotNull(board);

        String playerURI = pawn.getPlayer();

        try{
            // player information holen
            HttpResponse<String> gamesPlayerResponse = Unirest.get(playerURI).asString();

            // hole den Player der aktuell am Zug ist
            HttpResponse<String> gamesCurrentResponse   = Unirest.get(board.getGameURI().getAbsoluteURI()+"/players/current").asString();

            if(gamesPlayerResponse.getStatus() == 200 && gamesCurrentResponse.getStatus() == 200){

                PlayerDTO playerPawnDTO = gson.fromJson(gamesPlayerResponse.getBody(),PlayerDTO.class);
                PlayerDTO playerTurnDTO = gson.fromJson(gamesCurrentResponse.getBody(),PlayerDTO.class);

                // Wenn der Player gleich dem im Current, dann ist dieser am Zug
                return  ( playerPawnDTO.getId().equals(playerTurnDTO.getId()) );

            } else {
                throw new ServiceNotAvaibleException("Game Response unerwarteter Response-Code\n" +
                                            "get("+playerURI+")\n" +
                                            "status: "+gamesPlayerResponse.getStatus()+"\n" +
                                            "body: "+gamesPlayerResponse.getBody()+"\n" +
                                            "get("+board.getGameURI().getAbsoluteURI()+"/players/turn"+")\n" +
                                            "status: "+gamesCurrentResponse.getStatus()+"\n" +
                                            "body: "+gamesCurrentResponse.getBody());
            }

        } catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Game Service nicht erreichbar und/oder funktioniert nicht richtig",ex);
        }catch (JsonSyntaxException ex){
            throw new WrongFormatException("Game Service response json Syntax fehler", ex);
        }
    }

    private int getDiceNumber(Board board) throws ServiceNotAvaibleException {

        try{
            HttpResponse<String> response = Unirest.get(board.getGameURI().getAbsoluteURI()+"/components").asString();

            if(response.getStatus() == 200){

                ComponentsDTO componentsDTO = gson.fromJson(response.getBody(),ComponentsDTO.class);
                String diceURI = componentsDTO.getDice();

                if(diceURI == null) throw new ServiceNotAvaibleException("Dice Service not found");

                HttpResponse<JsonNode> diceResponse = Unirest.get(diceURI).asJson();
                if(diceResponse.getStatus() == 200){

                    JSONObject jsonObject = diceResponse.getBody().getObject();
                    String stringNumber = jsonObject.get("number").toString();

                    return Integer.parseInt(stringNumber);

                }else throw new ServiceNotAvaibleException("Dice Service-Response != 200");
            }else throw new ServiceNotAvaibleException("Game Service-Response != 200");

        } catch (UnirestException | JsonSyntaxException ex){
            throw new ServiceNotAvaibleException(ex.getMessage());
        }
    }

    private EventDTO createEvent(Board board, Pawn pawn,String type, String name, String reason, String resource) throws ServiceNotAvaibleException, WrongFormatException {
        String game = board.getGameURI().getAbsoluteURI();
        String time = Long.toString(System.currentTimeMillis());
        String player = pawn.getPlayer();

        EventDTO eventPostDTO = new EventDTO(

                                    game,
                                    type,
                                    name,
                                    reason,
                                    resource,
                                    player,
                                    time
                                );
        try{
            HttpResponse<String> componentsResponse = Unirest.get(game+"/components").asString();
            if(componentsResponse.getStatus() != 200) throw new ServiceNotAvaibleException("Game Service GET - Wrong response code");

            String eventManagerURI = gson.fromJson(componentsResponse.getBody(),ComponentsDTO.class).getEvent();

            HttpResponse<String> eventPostResponse = Unirest
                                                        .post(eventManagerURI)
                                                        .header("Content-Type","application/json")
                                                        .body(gson.toJson(eventPostDTO)).asString();
            if(eventPostResponse.getStatus() != 201) throw new ServiceNotAvaibleException("Event Service POST -  Wrong response code");

            HttpResponse<String> eventGetResponse = Unirest.get(eventPostResponse.getHeaders().getFirst("Location")).asString();
            if(eventGetResponse.getStatus() != 200) throw new ServiceNotAvaibleException("Event Service GET - Wrong response code");

            EventDTO eventGetDTO = gson.fromJson(eventGetResponse.getBody(),EventDTO.class);
            return eventGetDTO;

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException(ex.getMessage());
        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    private List<EventDTO> notifyBankUeberLos(Board board,Pawn pawn) throws ServiceNotAvaibleException {
        checkNotNull(pawn);
        checkNotNull(board);

        String bankURI = getBankURI(board.getGameURI().getAbsoluteURI());
        String toAccountID = getPlayerAccountID(pawn.getPlayer());
        int amount = 200;
        String reason = "Player walks over \"LOS\"";
        List<EventDTO> eventDTOList = createtransferTo(bankURI,toAccountID,amount,reason);
        return eventDTOList;
    }

    private List<EventDTO> createtransferTo(String bankURI,String toAccount,int amount, String reason) throws ServiceNotAvaibleException {
        checkNotNull(bankURI);
        checkNotNull(toAccount);
        checkNotNull(reason);

        try{
            String bankTransferURI = bankURI+"/transfer/to"+toAccount+"/"+amount;
            HttpResponse<JsonNode> bankResponse = Unirest.post(bankTransferURI)
                                                        .header("Content-Type","application/json")
                                                        .body(reason).asJson();
            if(bankResponse.getStatus() == 201){

                List<EventDTO> eventDTOList = new ArrayList();

                for(Object o : bankResponse.getBody().getArray()){
                    EventDTO eventDTO = gson.fromJson(o.toString(),EventDTO.class);
                    eventDTOList.add(eventDTO);
                }

                return eventDTOList;
            }else{
                throw new ServiceNotAvaibleException("Bank Service unerwarteter Response-Code\n" +
                                                    "post("+bankTransferURI+")\n" +
                                                    "req_body: "+reason+"\n" +
                                                    "res_status: "+bankResponse.getStatus()+"\n" +
                                                    "res_body: "+bankResponse.getBody().toString());
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Bank Service nicht erreichbar");
        }
    }

    private String getPlayerAccountID(String playerURI) throws ServiceNotAvaibleException {
        checkNotNull(playerURI);

        try {
            // get player Information
            HttpResponse<String> playerResponse = Unirest.get(playerURI).asString();
            if(playerResponse.getStatus() != 200){
                throw new ServiceNotAvaibleException("Game Service unerwarteter Response-Code\n" +
                                                     "get("+playerURI+")\n" +
                                                     "status: "+playerResponse.getStatus()+"\n" +
                                                     "body: "+playerResponse.getBody());
            }else{
                PlayerDTO playerDTO = gson.fromJson(playerResponse.getBody(),PlayerDTO.class);
                URIObject uriObject = URIParser.createURIObject(playerDTO.getAccount());
                return uriObject.getId();
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Game Service nicht erreichbar");
        } catch (URISyntaxException e) {
            throw new ServiceNotAvaibleException("Game Service Player account invalid URI",e);
        }
    }

    private String getBankURI(String gameURI) throws ServiceNotAvaibleException {
        checkNotNull(gameURI);
        try{
            // get game Components
            HttpResponse<String> gameResponse = Unirest.get(gameURI+"/components").asString();
            if(gameResponse.getStatus() != 200){
                throw new ServiceNotAvaibleException("Game Service unerwarteter Response-Code\n" +
                                                    "get("+gameURI+"/components"+")\n"+
                                                    "status: "+gameResponse.getStatus()+"\n" +
                                                    "body: "+gameResponse.getBody());
            }else{
                ComponentsDTO componentsDTO =  gson.fromJson(gameResponse.getBody(),ComponentsDTO.class);
                return componentsDTO.getBank();
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Game Service nicht erreichbar",ex);
        }
    }

    private CardDTO getCommunityCard(){
        throw new UnsupportedOperationException();
    }

    private CardDTO getChanceCard(){
        throw new UnsupportedOperationException();
    }

    private List<EventDTO> notifyBrokerByVisitPlace(Place place, Pawn pawn) throws WrongFormatException, ServiceNotAvaibleException {
        checkNotNull(place);
        checkNotNull(pawn);

        try{
            String brokerPlaceURI = place.getBroker()+"/visit";
            String playerGameURI  = pawn.getPlayer();
            String jsonBody = "{\"player\" : \""+playerGameURI+"\" }";

            List<EventDTO> eventDTOList = new ArrayList();

            // broker benachrichtigen, Grundstück wurde besucht
            HttpResponse<JsonNode> brokerResponse = Unirest.post(brokerPlaceURI)
                                                                .header("Content-Type","application/json")
                                                                .body(jsonBody)
                                                                .asJson();

            if(brokerResponse.getStatus() == 200){
                // parse body zum JsonArray
                JSONArray jsonArray = brokerResponse.getBody().getArray();

                // parse jsonObj zu EventDTO und füge diesen in die Liste
                for(Object o : jsonArray){
                    eventDTOList.add(gson.fromJson(o.toString(),EventDTO.class));
                }

                return eventDTOList;
            }else {
                // Wenn unerwarteter Response Code
                throw new ServiceNotAvaibleException("Broker Service unerwarteter Response-Code\n" +
                                                    "post("+brokerPlaceURI+")\n" +
                                                    "req_header(\"Content-Type\",\"application/json\")\n" +
                                                    "req_body("+jsonBody+")\n" +
                                                    "res_status: "+brokerResponse.getStatus()+"\n" +
                                                    "res_body: "+brokerResponse.getBody());
            }



        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Broker Service nicht erreichbar",ex);
        }
    }

    /***************************/

    public synchronized String createBoard(String jsonString) throws BoardAlreadyExistsException,
            WrongFormatException, URISyntaxException, ServiceNotAvaibleException {
        checkNotNull(jsonString);


        JSONObject jsonObject = new JSONObject(jsonString);
        if(!jsonObject.has("game"))  throw new WrongFormatException("can't find \"game\" param");

        String gameURI = jsonObject.getString("game");
        Board board = new Board(gameURI);

        if(boardGameIDMap.containsKey(board.getId())) throw new BoardAlreadyExistsException();

        boardGameIDMap.put(board.getId(),board);
        initializeService.addBoard(board);

        return board.getId();
    }


    public String getBoards() {
        Collection<String> boards = boardGameIDMap.keySet();

        JSONObject j = new JSONObject();
        j.put("boards",boards);
        return j.toString();
    }

    public String createPawn(String gameID,String jsonBody) throws WrongFormatException, BoardNotFoundException {
        checkNotNull(gameID);
        checkNotNull(jsonBody);

        try{

            Board board = getBoardObjectByGameId(gameID);
            PawnDTO pawnDTO = gson.fromJson(jsonBody, PawnDTO.class);

            String id = board.addPawn(pawnDTO);
            return id;

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    public String getBoard(String gameID) throws BoardNotFoundException {
        checkNotNull(gameID);

        Board board = getBoardObjectByGameId(gameID);
        BoardDTO boardDTO = board.toDTO();

        return gson.toJson(boardDTO);
    }

    public void deleteBoard(String gameID) throws BoardNotFoundException {
        checkNotNull(gameID);

        deleteBoardByGameId(gameID);
    }

    public String getPawnListByGameID(String gameID) throws BoardNotFoundException {
        checkNotNull(gameID);

        Board board = getBoardObjectByGameId(gameID);
        Collection<String> pawns = board.getPawnsMap().keySet();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pawns",pawns);

        return jsonObject.toString();
    }

    public String getPawnByGameID(String gameID, String pawnID) throws BoardNotFoundException, PawnNotFoundException {
        checkNotNull(gameID);
        checkNotNull(pawnID);

        Board board = getBoardObjectByGameId(gameID);
        Pawn pawn = board.getPawnById(pawnID);

        return gson.toJson(pawn.toDTO());
    }

    public String updatePawnByGameID(String gameID, String pawnID, String jsonPawnString) throws BoardNotFoundException, PawnNotFoundException, WrongFormatException {
        checkNotNull(gameID);
        checkNotNull(pawnID);
        checkNotNull(jsonPawnString);

        try{

            PawnDTO pawnDTO = gson.fromJson(jsonPawnString,PawnDTO.class);
            Board board     = getBoardObjectByGameId(gameID);

            board.updatePawn(pawnID,pawnDTO);
            Pawn pawn = board.getPawnById(pawnID);

            return gson.toJson(pawn.toDTO());

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    public void removePawnByGameID(String gameID, String pawnID) throws BoardNotFoundException, PawnNotFoundException {
        checkNotNull(gameID);
        checkNotNull(pawnID);

        Board board = getBoardObjectByGameId(gameID);
        board.removePawn(pawnID);
    }

    public String getPawnRollsByGameID(String gameID, String pawnID) throws BoardNotFoundException, PawnNotFoundException {
        checkNotNull(gameID);
        checkNotNull(pawnID);

        Board board = getBoardObjectByGameId(gameID);
        RollPersistence rollPersistence = board.getRollPersistence();

        Rolls rolls = rollPersistence.getPawnRolls(pawnID);

        return "{ \"throws\" : "+gson.toJson(rolls.getRollList())+"}";
    }

    public synchronized String pawnRoll(String gameID, String pawnID) throws BoardNotFoundException, PawnNotFoundException,
            PlayersWrongTurnException, ServiceNotAvaibleException, WrongFormatException, UnirestException, MutexNotReleasedException {
        checkNotNull(gameID);
        checkNotNull(pawnID);


        List<EventDTO> eventList = new ArrayList();
        Board board = getBoardObjectByGameId(gameID);
        Pawn pawn = board.getPawnById(pawnID);

        // !!! prüfe ob der Spieler am Zug ist !!!
        if(!isPlayersTurn(pawn,board)) throw new PlayersWrongTurnException();

        // setze mutex wenn nicht bereits geschehen
        setPlayerMutex(pawn,board);

        // 2.1 würfeln
        int number = getDiceNumber(board);

        // 2.2 erstelle Event (Spieler hat die Nummber XY gewürfelt)
        String type = "dice roll";
        String name = "Dice Event";
        String reason = "Player wants to roll Dice";
        String resource = "Number: "+number;

        EventDTO eventPlayerDiceDTO = createEvent(board,pawn,type,name,reason,resource);
        eventList.add(eventPlayerDiceDTO);

        // pawn bewegen
        List<EventDTO> subEventList = movePawn(gameID,pawnID,number);
        eventList.addAll(subEventList);

        return gson.toJson(eventList);
    }

    public String getPlaces(String gameID) throws BoardNotFoundException {
        checkNotNull(gameID);

        Board board = getBoardObjectByGameId(gameID);
        Collection<String> places = board.getPlacesIDList();

        return gson.toJson(places);
    }

    public String getPlace(String gameID, String placeID) throws BoardNotFoundException, PlaceNotFoundException {
        checkNotNull(gameID);
        checkNotNull(placeID);

        Board board = getBoardObjectByGameId(gameID);
        Place place = board.getPlaceByID(placeID);

        return gson.toJson(place.toDTO());
    }

    public List<EventDTO> movePawn(String gameID, String pawnID, int number) throws BoardNotFoundException, PawnNotFoundException,
            ServiceNotAvaibleException, WrongFormatException, UnirestException {

        Board board = getBoardObjectByGameId(gameID);
        Pawn pawn   = board.getPawnById(pawnID);

        List<EventDTO> eventList = new ArrayList();

        // 1 gewürfelte Zahl der RollPersistence übergeben
        board.getRollPersistence().addPawnRoll(pawnID,number);
        // 2. Pawn bewegen
        PawnMove move = board.pawnRoll(pawnID,number);

        // 4. erstelle Event (Spieler wurde bewegt)
        String type = "pawn move";
        String name = "Pawn Move Event";
        String reason = "Player wants to move";
        String resource = pawn.getMove();
        EventDTO eventPlayerMoveDTO = createEvent(board,pawn,type,name,reason,resource);

        eventList.add(eventPlayerMoveDTO);


        // Wenn kein Beoker für diesen Place eingetragen, dann nicht kaufbares Feld
        if(move.isUeberLos()){
            List<EventDTO> events = notifyBankUeberLos(board,pawn);
            eventList.addAll(eventList);
        }
        if(move.getPlace().getBroker().isEmpty()){
            // Gemeinschaftskarte?
            if(move.getPlace().getName().equals("Gemeinschaftsfeld")){
                CardDTO card = getCommunityCard();
                // TODO erstelle Event und weitere Aktionen

            // Ereigniskarte?
            }else if(move.getPlace().getName().equals("Ereignisfeld")){
                CardDTO card = getChanceCard();
                // TODO erstelle Event und weitere Aktionen

            // Gefängnis
            }else if(move.getPlace().getName().equals("Gehen ins Gefängnis")){
                board.setPawnToJail(pawnID);
                // TODO erstelle Event
            }

        }else{
            // Benachrichte Broker (weil Feld besucht)
            List<EventDTO> list = notifyBrokerByVisitPlace(move.getPlace(),pawn);
            eventList.addAll(list);
        }

        return eventList;
    }
}
