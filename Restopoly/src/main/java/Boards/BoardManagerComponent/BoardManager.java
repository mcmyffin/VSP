package Boards.BoardManagerComponent;

import Banks.BankManagerComponent.TransactionState;
import Banks.BankManagerComponent.TransferAction;
import Boards.BoardManagerComponent.DTOs.BoardDTO;
import Boards.BoardManagerComponent.DTOs.PawnDTO;
import Boards.BoardManagerComponent.Util.InitializeService;
import Boards.Main;
import Common.Exceptions.*;
import Common.Util.URIObject;
import Common.Util.URIParser;
import Decks.DeckManagerComponent.Card;
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
            HttpResponse<String> gamesAquireMutexResponse   = Unirest.put(board.getGameURIObject().getAbsoluteURI()+"/players/turn")
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
                                                        "get("+board.getGameURIObject().getAbsoluteURI()+"/players/turn)\n" +
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
            HttpResponse<String> gamesCurrentResponse   = Unirest.get(board.getGameURIObject().getAbsoluteURI()+"/players/current").asString();

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
                                            "get("+board.getGameURIObject().getAbsoluteURI()+"/players/turn"+")\n" +
                                            "status: "+gamesCurrentResponse.getStatus()+"\n" +
                                            "body: "+gamesCurrentResponse.getBody());
            }

        } catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Game Service nicht erreichbar und/oder funktioniert nicht richtig",ex);
        }catch (JsonSyntaxException ex){
            throw new WrongFormatException("Game Service response json Syntax fehler", ex);
        }
    }

    private int getDiceNumber(Board board) throws ServiceNotAvaibleException, WrongFormatException {

        try{
            ComponentsDTO componentsDTO = board.getGameComponents();
            String diceURI = componentsDTO.getDice();

            if(diceURI == null) throw new ServiceNotAvaibleException("Dice Service not found");

            HttpResponse<JsonNode> diceResponse = Unirest.get(diceURI).asJson();
            if(diceResponse.getStatus() == 200){

                JSONObject jsonObject = diceResponse.getBody().getObject();
                int number = jsonObject.getInt("number");
                return number;

            }else throw new ServiceNotAvaibleException("Dice Service-Response != 200");


        } catch (UnirestException ex){
            throw new ServiceNotAvaibleException(ex.getMessage());
        }catch (JsonSyntaxException ex){
            throw new WrongFormatException("Dice Number type wrong",ex);
        }
    }

    private EventDTO createEvent(Board board, String player,String type, String name, String reason, String resource) throws ServiceNotAvaibleException, WrongFormatException {
        String game = board.getGameURIObject().getAbsoluteURI();
        String time = Long.toString(System.currentTimeMillis());

        EventDTO eventPostDTO = new EventDTO(

                                    game,
                                    type,
                                    name,
                                    reason,
                                    resource,
                                    player,
                                    time
                                );

        String eventJsonString = gson.toJson(eventPostDTO);
        try{
            ComponentsDTO componentsDTO = board.getGameComponents();
            String eventManagerURI = componentsDTO.getEvent();

            HttpResponse<String> eventPostResponse = Unirest
                                                        .post(eventManagerURI)
                                                        .header("Content-Type","application/json")
                                                        .body(eventJsonString).asString();

            if(eventPostResponse.getStatus() != 201){
                throw new ServiceNotAvaibleException("Event Service POST -  Wrong response code\n" +
                                                        "post("+eventManagerURI+")\n" +
                                                        "req_header(\"Content-Type\":\"application/json\")\n" +
                                                        "req_body: "+eventJsonString+"\n"+
                                                        "res_status: "+eventPostResponse.getStatus()+"\n" +
                                                        "res_body: "+eventPostResponse.getBody());
            }

            // get response Locationheader
            String eventResponseLocationURI =  eventPostResponse.getHeaders().getFirst("Location");

            HttpResponse<String> eventGetResponse = Unirest.get(eventResponseLocationURI).asString();
            if(eventGetResponse.getStatus() != 200){
                throw new ServiceNotAvaibleException("Event Service GET - Wrong response code\n" +
                                                        "get("+eventResponseLocationURI+")\n" +
                                                        "status: "+eventGetResponse.getStatus()+"\n" +
                                                        "body: "+eventGetResponse.getBody());
            }

            EventDTO eventGetDTO = gson.fromJson(eventGetResponse.getBody(),EventDTO.class);
            return eventGetDTO;

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Event Service unerreichbar",ex);
        }catch (JsonSyntaxException ex){
            throw new WrongFormatException("Json Syntax wrong format",ex);
        }
    }

    private List<EventDTO> notifyBankUeberLos(Board board,Pawn pawn) throws ServiceNotAvaibleException {
        checkNotNull(pawn);
        checkNotNull(board);

        String bankURI      = board.getGameComponents().getBank();
        String toAccountID  = getPlayerAccountID(pawn.getPlayer());
        int amount          = 200;

        String reason       = "Player walks over \"LOS\"";
        List<EventDTO> eventDTOList = createTransferTo(bankURI,toAccountID,amount,reason);

        return eventDTOList;
    }


    private List<EventDTO> createTransferEveryBodyTo(Board board, String bankURI, String toAccountID, int amount, String reason) throws ServiceNotAvaibleException {
        checkNotNull(board);
        checkNotNull(bankURI);
        checkNotNull(toAccountID);
        checkNotNull(reason);

        // create EventList
        List<EventDTO> eventList = new ArrayList();

        // get Bankaccounts
        List<String> fromAccountList = new ArrayList();
        for(Pawn p : board.getPawnsMap().values()){
            String account = getPlayerAccountID(p.getPlayer());
            fromAccountList.add(account);
        }

        // entferne Account, welcher bezahlt werden soll
        fromAccountList.remove(toAccountID);

        // erstelle eine Transaction bei der Bank
        String transactionID = createTransactionID(bankURI);

        // execute transfers
        for(String accountID : fromAccountList){

            List<EventDTO> events = createTransferFromToWithTransaction(bankURI,accountID,toAccountID,amount,reason,transactionID);
            eventList.addAll(events);
        }

        TransactionState state = getTransactionStateByID(bankURI,transactionID);

        if(state.equals(TransactionState.READY)){
            commitTransactionByID(bankURI,transactionID);
        }else{
            rollbackTransactionByID(bankURI,transactionID);
        }

        return eventList;

    }

    private void rollbackTransactionByID(String bankURI, String transactionID) throws ServiceNotAvaibleException {
        checkNotNull(bankURI);
        checkNotNull(transactionID);

        String transactionURI = bankURI+"/transaction"+transactionID;

        try{

            HttpResponse<String> rollbackResponse = Unirest.delete(transactionURI).asString();
            if(rollbackResponse.getStatus() != 200){
                throw new ServiceNotAvaibleException("Bank Service unerwarteter Response-Code\n" +
                                                    "delete("+transactionURI+")\n" +
                                                    "status: "+rollbackResponse.getStatus()+"\n" +
                                                    "body: "+rollbackResponse.getBody());
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Bank Service nicht erreichbar",ex);
        }
    }

    private void commitTransactionByID(String bankURI, String transactionID) throws ServiceNotAvaibleException {
        checkNotNull(bankURI);
        checkNotNull(transactionID);

        String transactionURI = bankURI+"/transaction"+transactionID;

        try{

            HttpResponse<String> transactionResponse = Unirest.put(transactionURI).queryString("state", TransferAction.COMMIT.getVal()).asString();
            if(transactionResponse.getStatus() != 200){
                throw new ServiceNotAvaibleException("Bank Service unerwarteter Response-Code\n" +
                                                    "put("+transactionURI+")\n"+
                                                    "query( state , "+TransferAction.COMMIT.getVal()+")\n"+
                                                    "status: "+transactionResponse.getStatus()+"\n" +
                                                    "body: "+transactionResponse.getBody());
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Bank Service nicht erreichbar",ex);
        }
    }

    private TransactionState getTransactionStateByID(String bankURI, String transactionID) throws ServiceNotAvaibleException {
        checkNotNull(bankURI);
        checkNotNull(transactionID);

        String transactionStateURI = bankURI+"/transaction"+transactionID;

        try{
            HttpResponse<String> transactionStateResponse = Unirest.get(transactionStateURI).asString();
            if(transactionStateResponse.getStatus() == 200){

                String stringState = transactionStateResponse.getBody();
                stringState = stringState.toUpperCase();

                TransactionState state = TransactionState.valueOf(stringState);
                return state;

            }else {
                throw new ServiceNotAvaibleException("Bank Service unerwarteter Response-Code\n" +
                                                        "get("+transactionStateURI+")\n" +
                                                        "status: "+transactionStateResponse.getStatus()+"\n" +
                                                        "body: "+transactionStateResponse.getBody());
            }
        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Bank Service nicht erreichbar",ex);
        }
    }

    private List<EventDTO> createTransferFromToWithTransaction(String bankURI, String fromAccountID, String toAccountID,
                                                                                    int amount, String reason, String transactionID) throws ServiceNotAvaibleException {
        checkNotNull(bankURI);
        checkNotNull(fromAccountID);
        checkNotNull(toAccountID);
        checkNotNull(reason);
        checkNotNull(transactionID);

        String transferURI = bankURI+"/transfer/from"+fromAccountID+"/to"+toAccountID+"/"+amount;
        List<EventDTO> eventList = new ArrayList();
        try{

            HttpResponse<JsonNode> transferResponse = Unirest.post(transactionID).queryString("transaction",transactionID)
                                                                                .header("Content-Type","application/json")
                                                                                .body(reason)
                                                                                .asJson();
            // wenn transaction erfolgreich
            if(transferResponse.getStatus() == 201){

                for(Object o : transferResponse.getBody().getArray()){
                    EventDTO event = gson.fromJson(o.toString(),EventDTO.class);
                    eventList.add(event);
                }

                return eventList;

            // wenn transaction gescheitert
            }else if(transferResponse.getStatus() == 403){
                // todo erstelle Event (transaction bei account xy ist gescheitert)
                // todo überprüfe ob account xy genug geld hat
                // todo setzte player des accounts xy auf pleite (spieler hat verloren)
                throw new UnsupportedOperationException();
            }else {
                throw new ServiceNotAvaibleException("Bank Service unerwarteter Response-Code\n" +
                                                    "post()\n" +
                                                    "status: "+transferResponse.getStatus()+"\n" +
                                                    "body: "+transferResponse.getBody());
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Bank Service nicht erreichbar",ex);
        }
    }

    private String createTransactionID(String bankURI) throws ServiceNotAvaibleException {
        checkNotNull(bankURI);

        // baue transactionURI
        String transactionURI = bankURI+"/transaction";
        try{
            // sende Post anfrage
            HttpResponse<String> transactionResponse = Unirest.post(transactionURI).asString();
            // Wenn status erwartet
            if(transactionResponse.getStatus() == 200){
                // hole transactionURI aus dem LOCATION-HEADER
                String transactionResponseURI = transactionResponse.getHeaders().getFirst("Location");
                URIObject uriObject = URIParser.createURIObject(transactionResponseURI);
                return uriObject.getId();
            }else{
                throw new ServiceNotAvaibleException("Bank Service unerwarteter Response-Code\n" +
                                                    "post("+transactionURI+")\n" +
                                                    "status: "+transactionResponse.getStatus()+"\n" +
                                                    "body: "+transactionResponse.getBody());
            }

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Bank Service nicht erreichbar",ex);
        } catch (URISyntaxException e) {
            throw new ServiceNotAvaibleException("Bank create Transaction - Location URI wrong syntax",e);
        }
    }


    private List<EventDTO> createTransferFrom(String bankURI, String fromAccountID, int amount, String reason) throws ServiceNotAvaibleException {
        checkNotNull(bankURI);
        checkNotNull(fromAccountID);
        checkNotNull(reason);

        try{
            String bankTransferURI = bankURI+"/transfer/from"+fromAccountID+"/"+amount;
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


    private List<EventDTO> createTransferTo(String bankURI, String toAccount, int amount, String reason) throws ServiceNotAvaibleException {
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

    private Card getCard(String deckURI) throws ServiceNotAvaibleException {
        checkNotNull(deckURI);

        try{
            HttpResponse<String> deckResponse = Unirest.get(deckURI).asString();
            if(deckResponse.getStatus() == 200){
                // get JsonObject from Body
                String body = deckResponse.getBody();
                // parse jsonObject to DTO
                CardDTO cardDTO =  gson.fromJson(body,CardDTO.class);
                Card card = Card.fromDTO(cardDTO);
                return card;
            }else{
                throw new ServiceNotAvaibleException("Deck Service Response-Code != 200\n" +
                                                    "get("+deckURI+")\n" +
                                                    "status: "+deckResponse.getStatus()+"\n" +
                                                    "body: "+deckResponse.getBody());
            }
        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException("Deck Service nicht erreichbar",ex);
        }
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

    private List<EventDTO> executeCardAction(Board board, Card card, String cardType, String fieldName, Pawn pawn) throws PawnNotFoundException, BoardNotFoundException, UnirestException, ServiceNotAvaibleException, WrongFormatException {
        checkNotNull(board);
        checkNotNull(card);
        checkNotNull(cardType);
        checkNotNull(pawn);

        switch (card.getCardAction()){
            case BEWEGE_DICH: {
                // execute movement
                List<EventDTO> events = movePawnByCard(board,pawn,card.getCardAction().getNumber(),cardType);
                return events;
            }
            case GEFAENGNIS: {
                // create Event
                EventDTO event = createEvent(board,
                        pawn.getPlayer(),
                        "Board execute Pawn jump",
                        "Player jump to Jail",
                        "Player stands on "+fieldName,
                        Main.URL+pawn.getId());

                // set Pawn to Jail
                board.setPawnToJail(pawn.getId());

                List<EventDTO> events = new ArrayList();
                events.add(event);

                return events;
            }
            case BEZAHLE_GELD_AN_BANK: {
                // nummerische Geldmenge
                String bankURI = board.getGameComponents().getBank();
                String fromAccountID = getPlayerAccountID(pawn.getPlayer());
                int amount = card.getCardAction().getNumber();

                return createTransferFrom(bankURI,fromAccountID,amount,card.getText());
            }
            case ERHALTE_GELD_VON_BANK: {
                // nummerische Geldmenge
                String bankURI = board.getGameComponents().getBank();
                String toAccountID = getPlayerAccountID(pawn.getPlayer());
                int amount = card.getCardAction().getNumber();

                return createTransferTo(bankURI,toAccountID,amount,card.getText());
            }
            case ERHALTE_GELD_VON_ALLEN: {
                // nummerische Geldmenge
                String bankURI = board.getGameComponents().getBank();
                String toAccountID = getPlayerAccountID(pawn.getPlayer());
                int amount = card.getCardAction().getNumber();

                return createTransferEveryBodyTo(board,bankURI,toAccountID,amount,card.getText());
            }
        }
        throw new UnsupportedOperationException("TODO");
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

        EventDTO eventPlayerDiceDTO = createEvent(board,pawn.getPlayer(),type,name,reason,resource);
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
        String name = "Board execute Pawn Move";
        String reason = "Player wants to move";
        String resource = Main.URL+pawn.getId();

        // create Event
        EventDTO eventPlayerMoveDTO = createEvent(board,pawn.getPlayer(),type,name,reason,resource);
        eventList.add(eventPlayerMoveDTO);

        // get Game Components
        ComponentsDTO componentsDTO = board.getGameComponents();

        // Wenn kein Beoker für diesen Place eingetragen, dann nicht kaufbares Feld
        if(move.isUeberLos()){
            List<EventDTO> events = notifyBankUeberLos(board,pawn);
            eventList.addAll(eventList);
        }
        if(move.getPlace().getBroker().isEmpty()){
            // Gemeinschaftskarte?
            if(move.getPlace().getName().equals("Gemeinschaftsfeld")){
                // get Card
                String deckURI = componentsDTO.getDecks()+"/community";
                Card card = getCard(deckURI);

                // create event
                EventDTO event = createEvent(board,pawn.getPlayer(),"take Card",
                                                    "Player takes Community-Card from Deck",
                                                    "Player stands on Community-Card-Place",card.getText());

                // execute Card Action and create Events
                List<EventDTO> events = executeCardAction(board,card,"Community-Card place","Community card",pawn);
                eventList.add(event);
                eventList.addAll(eventList);

            // Ereigniskarte?
            }else if(move.getPlace().getName().equals("Ereignisfeld")){
                // get Card
                String deckURI = componentsDTO.getDecks()+"/chance";
                Card card = getCard(deckURI);

                // create event
                EventDTO event = createEvent(board,pawn.getPlayer(),"take Card",
                        "Player takes Chance-Card from Deck",
                        "Player stands on Chance-Card-Place",card.getText());

                // execute Card Action and create Events
                List<EventDTO> events = executeCardAction(board,card,"Chance-Card place","Community card",pawn);
                eventList.add(event);
                eventList.addAll(eventList);

            // Gefängnis
            }else if(move.getPlace().getName().equals("Gehen ins Gefängnis")){
                board.setPawnToJail(pawnID);
            }

        }else{
            // Benachrichte Broker (weil Feld besucht)
            List<EventDTO> list = notifyBrokerByVisitPlace(move.getPlace(),pawn);
            eventList.addAll(list);
        }

        return eventList;
    }

    private List<EventDTO> movePawnByCard(Board board, Pawn pawn, int number, String cardType) throws BoardNotFoundException, PawnNotFoundException,
            ServiceNotAvaibleException, WrongFormatException, UnirestException {

        List<EventDTO> eventList = new ArrayList();

        // 2. Pawn bewegen
        PawnMove move = board.pawnRoll(pawn,number);

        // 4. erstelle Event (Spieler wurde bewegt)
        String type = "Board execute Pawn move";
        String name = "Player execute "+cardType+" action";
        String reason = "Player takes "+cardType;
        String resource = Main.URL+pawn.getId();

        // create Event
        EventDTO eventPlayerMoveDTO = createEvent(board,pawn.getPlayer(),type,name,reason,resource);
        eventList.add(eventPlayerMoveDTO);

        // get Game Components
        ComponentsDTO componentsDTO = board.getGameComponents();

        // Wenn kein Beoker für diesen Place eingetragen, dann nicht kaufbares Feld
        if(move.isUeberLos()){
            List<EventDTO> events = notifyBankUeberLos(board,pawn);
            eventList.addAll(eventList);
        }
        if(move.getPlace().getBroker().isEmpty()){
            // Gemeinschaftskarte?
            if(move.getPlace().getName().equals("Gemeinschaftsfeld")){
                // get Card
                String deckURI = componentsDTO.getDecks()+"/community";
                Card card = getCard(deckURI);

                // create event
                EventDTO event = createEvent(board,pawn.getPlayer(),"take Card",
                        "Player takes Community-Card from Deck",
                        "Player stands on Community-Card-Place",card.getText());

                // execute Card Action and create Events
                List<EventDTO> events = executeCardAction(board,card,"Community-Card","Community-Place",pawn);
                eventList.add(event);
                eventList.addAll(eventList);

            // Ereigniskarte?
            }else if(move.getPlace().getName().equals("Ereignisfeld")){
                // get Card
                String deckURI = componentsDTO.getDecks()+"/chance";
                Card card = getCard(deckURI);

                // create event
                EventDTO event = createEvent(board,pawn.getPlayer(),"take Card",
                        "Player takes Chance-Card from Deck",
                        "Player stands on Chance-Card-Place",card.getText());

                // execute Card Action and create Events
                List<EventDTO> events = executeCardAction(board,card,"Chance-Card","Chance-Card place",pawn);
                eventList.add(event);
                eventList.addAll(eventList);

            // Gefängnis
            }else if(move.getPlace().getName().equals("Gehen ins Gefängnis")){
                board.setPawnToJail(pawn.getId());
            }

        }else{
            // Benachrichte Broker (weil Feld besucht)
            List<EventDTO> list = notifyBrokerByVisitPlace(move.getPlace(),pawn);
            eventList.addAll(list);
        }

        return eventList;
    }
}
