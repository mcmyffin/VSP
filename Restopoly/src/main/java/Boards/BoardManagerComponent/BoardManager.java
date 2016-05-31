package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.DTOs.BoardDTO;
import Boards.BoardManagerComponent.DTOs.PawnDTO;
import Boards.BoardManagerComponent.Util.InitializeService;
import Common.Exceptions.*;
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

    private boolean isPlayersTurn(Pawn pawn, Board board) throws ServiceNotAvaibleException {
        checkNotNull(pawn);

        String playerURI = pawn.getPlayer();

        try{
            HttpResponse<String> gamesPlayerResponse = Unirest.get(playerURI).asString();
            HttpResponse<String> gamesTurnResponse   = Unirest.get(board.getGameURI().getAbsoluteURI()+"/players/turn").asString();

            if(gamesPlayerResponse.getStatus() == 200 && gamesTurnResponse.getStatus() == 200){

                PlayerDTO playerPawnDTO = gson.fromJson(gamesPlayerResponse.getBody(),PlayerDTO.class);
                PlayerDTO playerTurnDTO = gson.fromJson(gamesTurnResponse.getBody(),PlayerDTO.class);

                // Wenn der Player gleich dem im Turn, dann ist dieser am Zug
                return  ( playerPawnDTO.getId().equals(playerTurnDTO.getId()) );

            } else throw new UnirestException("Response Status-Code != 200 !!!");

        } catch (UnirestException | JsonSyntaxException ex){
            throw new ServiceNotAvaibleException(ex.getMessage());
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

            HttpResponse<String> eventPostResponse = Unirest.post(eventManagerURI).header("Content-Type","application/json").body(eventPostDTO).asString();
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

    private void notifyBankUeberLos(Pawn pawn){
        throw new UnsupportedOperationException();
    }

    private CardDTO getCommunityCard(){
        throw new UnsupportedOperationException();
    }

    private CardDTO getChanceCard(){
        throw new UnsupportedOperationException();
    }

    private List<EventDTO> notifyBrokerByVisitPlace(Place place, Pawn pawn) throws UnirestException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("player",pawn.getPlayer());

        List<EventDTO> eventDTOList = new ArrayList();
        HttpResponse<JsonNode> brokerResponse = Unirest.post(place.getBroker()+"/visit").header("Content-Type","application/json").body(jsonObject.toString()).asJson();
        JSONArray jsonArray = brokerResponse.getBody().getArray();

        for(Object o : jsonArray){
            eventDTOList.add(gson.fromJson(o.toString(),EventDTO.class));
        }

        return eventDTOList;
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
            PlayersWrongTurnException, ServiceNotAvaibleException, WrongFormatException, UnirestException {
        checkNotNull(gameID);
        checkNotNull(pawnID);


        List<EventDTO> eventList = new ArrayList();
        Board board = getBoardObjectByGameId(gameID);
        Pawn pawn = board.getPawnById(pawnID);

        // !!! prüfe ob der Spieler am Zug ist !!!
        if(!isPlayersTurn(pawn,board)) throw new PlayersWrongTurnException();

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
        if(move.isUeberLos()) notifyBankUeberLos(pawn);// TODO sage der Bank bescheid (Spieler erhällt Geld)
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
