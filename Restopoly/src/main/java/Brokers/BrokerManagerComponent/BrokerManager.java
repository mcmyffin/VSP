package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTOs.BrokerDTO;
import Brokers.BrokerManagerComponent.DTOs.BrokerPlaceDTO;
import Brokers.Main;
import Common.Exceptions.*;
import Events.EventManagerComponent.DTO.EventDTO;
import Games.GameManagerComponent.DTO.ComponentsDTO;
import Games.GameManagerComponent.DTO.PlayerDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sasa on 11.05.16.
 */
public class BrokerManager {

    private Gson gson;

    private final Map<String, Broker> brokerMap;

    public BrokerManager() {
        this.brokerMap = new HashMap();
        this.gson = new Gson();
    }

    public String getBrokers() {

        Set<String> brokerKeys = brokerMap.keySet();
        return gson.toJson(brokerKeys);
    }

    public synchronized String createBroker(String brokerJsonString) throws WrongFormatException, URISyntaxException, BrokerAlreadyExistsException {
        checkNotNull(brokerJsonString);
        try{
            BrokerDTO brokerDTO = gson.fromJson(brokerJsonString, BrokerDTO.class);
            Broker broker = Broker.fromDTO(brokerDTO);
            if(brokerMap.containsKey(broker.getId())) {
                throw new BrokerAlreadyExistsException();
            }

        brokerMap.put(broker.getId(),broker);
        return broker.getId();

        }catch(JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    private Broker getBrokerObjectByID(String gameID) throws BrokerNotFoundException {
        checkNotNull(gameID);
        if(!brokerMap.containsKey(gameID)) throw new BrokerNotFoundException();
        return brokerMap.get(gameID);
    }

    public String getbrokerById(String gameID) throws BrokerNotFoundException {
        checkNotNull(gameID);

        Broker broker = getBrokerObjectByID(gameID);
        BrokerDTO brokerDTO = broker.toDTO();

        return gson.toJson(brokerDTO);
    }

    public String getBrokerPlacesByGameId(String gameID) throws BrokerNotFoundException {
        checkNotNull(gameID);
        Broker broker = getBrokerObjectByID(gameID);
        Map<String, BrokerPlace> brokerPlaceMap = broker.getBrokerPlaceMap();
        return gson.toJson(brokerPlaceMap.keySet());
    }

    public String getPlaceJsonStringByID(String gameID, String placeID) throws BrokerNotFoundException, PlaceNotFoundException {

        checkNotNull(gameID);
        checkNotNull(placeID);

        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace = broker.getBrokerPlaceByID(placeID);

        return gson.toJson(brokerPlace.toDTO());
    }

    public boolean updatePlaceByID(String gameID, String placeID, String body) throws BrokerNotFoundException,
            PlaceNotFoundException, WrongFormatException {
        checkNotNull(gameID);
        checkNotNull(placeID);
        checkNotNull(body);

        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace = broker.getBrokerPlaceByID(placeID);
        BrokerPlaceDTO brokerPlaceDTO = gson.fromJson(body, BrokerPlaceDTO.class);

        if(brokerPlaceDTO.getPlace() == null) throw new WrongFormatException();

        if(brokerPlace.getPlace().equals(brokerPlaceDTO.getPlace())) {
            return false;
        }else {
            brokerPlace.setPlace(brokerPlaceDTO.getPlace());
            return true;
        }
    }

    public String getBrokerOwnerByGameID(String gameID, String placeID)
            throws Exception {

        checkNotNull(gameID);
        checkNotNull(placeID);

        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace =  broker.getBrokerPlaceByID(placeID);

        //TODO was passiert wenn keine owner existiert
        if(!brokerPlace.getOwner().isEmpty()) {
            HttpResponse<String> httpResponse = Unirest.get(brokerPlace.getOwner()).asString();
            if (httpResponse.getStatus() != 200) throw new WrongResponsCodeException("Game Respons code != 200");
            return httpResponse.getBody();
        }
        //TODO Owner nicht eingetragen -> eventuell statuscode hinzufügen
        return "";

    }

    public String updateOwnerByGameID(String gameID, String placeID, String body)
            throws BrokerNotFoundException, PlaceNotFoundException, BrokerOwnerAlreadyExistsException, UnirestException, BrokerMaxAmountHousesRichedException, TransactionFailedException, BrokerPlaceWithoutOwnerException {

        checkNotNull(gameID);
        checkNotNull(placeID);
        checkNotNull(body);

        List<EventDTO> eventList = new ArrayList();
        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace = broker.getBrokerPlaceByID(placeID);

        JSONObject jsonObj = new JSONObject(body);
        String playerOwnerUri = jsonObj.getString("player");

//        if(brokerPlace.isOwnerExists()) throw new BrokerOwnerAlreadyExistsException();
        if(brokerPlace.getOwner().isEmpty()) throw new BrokerPlaceWithoutOwnerException();

        //Accounts von Old-players herrauszufinden
        String oldPlayerOwnerBankID = getPlayerAccountsID(playerOwnerUri);

        //Accounts von players herrauszufinden
        String playerOwnerBankID = getPlayerAccountsID(playerOwnerUri);

        //URI von bank herrauszufinden
        ComponentsDTO componentsDTO = getComponentsDTO(broker);
        String bank = componentsDTO.getBank();

        //amount (betrag)
        int amount = broker.getNextCostOfPlace(brokerPlace);

        //URl: /banks/{bankid}/transfer/to/{to}/{amount}
        HttpResponse<JsonNode> httpOldBankResponse = Unirest
                .post(bank+"/transfer/to/"+oldPlayerOwnerBankID+"/"+amount)
                .header("Content-Type","application/json")
                .body("User get Money for changing estate")
                .asJson();

        //wenn statuscode nicht stimmt, springe raus
        if(httpOldBankResponse.getStatus() != 201) throw new TransactionFailedException();

        //bank URI aufruf zum ausführen von das Transaktion
        //URl: /banks/{bankid}/transfer/from/{from}/{amo unt}
        HttpResponse<JsonNode> httpNewBankResponse = Unirest
                .post(bank+"/transfer/from/"+playerOwnerBankID+"/"+amount)
                .header("Content-Type","application/json")
                .body("User pay Money for changing estate")
                .asJson();

        //wenn statuscode nicht stimmt, springe raus
        if(httpNewBankResponse.getStatus() != 201) throw new TransactionFailedException();

        // bank response eventliste holen
        for(Object o : httpOldBankResponse.getBody().getArray()){
            EventDTO eventDTO = gson.fromJson(o.toString(),EventDTO.class);
            eventList.add(eventDTO);
        }

        // bank response eventliste holen
        for(Object o : httpNewBankResponse.getBody().getArray()){
            EventDTO eventDTO = gson.fromJson(o.toString(),EventDTO.class);
            eventList.add(eventDTO);
        }

        //neue Owner setzen
        brokerPlace.setOwner(playerOwnerUri);


        //URI von events
        String event = componentsDTO.getEvent();

        // erstelle Event , weil Owner neu gestzt
        EventDTO eventOwnerGesetzt = creatEvent(event,
                broker.getGameService(),
                "estate tranfer",
                "User chang estate",
                "User wants to change estate",
                Main.URL+brokerPlace.getId(),
                brokerPlace.getOwner(),
                System.currentTimeMillis()+""
        );

        //erstelle event, weil alte owner geloescht
        EventDTO eventOwnerGeloescht = creatEvent(event,
                broker.getGameService(),
                "estate tranfer",
                "User chang estate",
                "User wants to change estate",
                Main.URL+brokerPlace.getId(),
                brokerPlace.getOwner(),
                System.currentTimeMillis()+""
        );

        //add event in der eventliste
        eventList.add(eventOwnerGesetzt);
        eventList.add(eventOwnerGeloescht);


        return gson.toJson(eventList);

    }

    public String setOwnerByGameID(String gameID, String placeID, String body)
            throws BrokerNotFoundException, PlaceNotFoundException, UnirestException, TransactionFailedException, BrokerOwnerAlreadyExistsException, BrokerMaxAmountHousesRichedException {

        checkNotNull(gameID);
        checkNotNull(placeID);
        checkNotNull(body);

        List<EventDTO> eventList = new ArrayList();
        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace = broker.getBrokerPlaceByID(placeID);

        JSONObject jsonObj = new JSONObject(body);
        String playerOwnerUri = jsonObj.getString("player");

//        if(brokerPlace.isOwnerExists()) throw new BrokerOwnerAlreadyExistsException();
        if(!brokerPlace.getOwner().isEmpty()) throw new BrokerOwnerAlreadyExistsException();

        //Accounts von players herrauszufinden
        String playerOwnerBankID = getPlayerAccountsID(playerOwnerUri);

        //URI von bank herrauszufinden
        ComponentsDTO componentsDTO = getComponentsDTO(broker);
        String bank = componentsDTO.getBank();

        //amount (betrag)
        int amount = broker.getNextCostOfPlace(brokerPlace);

        //bank URI aufruf zum ausführen von das Transaktion
        //URl: /banks/{bankid}/transfer/from/{from}/{amo unt}
        HttpResponse<JsonNode> httpBankResponse = Unirest
                .post(bank+"/transfer/from/"+playerOwnerBankID+"/"+amount)
                .header("Content-Type","application/json")
                .body("User buy estate")
                .asJson();

        //wenn statuscode nicht stimmt, springe raus
        if(httpBankResponse.getStatus() != 201) throw new TransactionFailedException();

        // bank response eventliste holen
        for(Object o : httpBankResponse.getBody().getArray()){
            EventDTO eventDTO = gson.fromJson(o.toString(),EventDTO.class);
            eventList.add(eventDTO);
        }

        //neue Owner setzen
        brokerPlace.setOwner(playerOwnerUri);


        //URI von events
        String event = componentsDTO.getEvent();

        // erstelle Event , weil Owner neu gestzt
        EventDTO eventOwnerGesetzt = creatEvent(event,
                                                broker.getGameService(),
                                                "estate tranfer",
                                                "User set estate owner",
                                                "User wants to buy estate owner",
                                                Main.URL+brokerPlace.getId(),
                                                brokerPlace.getOwner(),
                                                System.currentTimeMillis()+""
                                                 );

        //add event in der eventliste
        eventList.add(eventOwnerGesetzt);

        return gson.toJson(eventList);
    }

    private EventDTO creatEvent(String eventManagerURI, String game, String type,
                                String name, String reason, String resource, String player, String time) {

        EventDTO eventOwnerGesetzt = new EventDTO(game, type, name, reason, resource, player, time );
        Unirest.post(eventManagerURI).header("Content-Type", "application/json").body(eventOwnerGesetzt);
        return eventOwnerGesetzt;
    }

    private ComponentsDTO getComponentsDTO(Broker broker) throws UnirestException {
        String gameURI = broker.getGameService();
        HttpResponse<String> httpGameRes = Unirest.get(gameURI+"/components").asString();
        String gameBody = httpGameRes.getBody();
        return gson.fromJson(gameBody, ComponentsDTO.class);

    }

    private String getPlayerAccountsID(String playerOwnerUri) throws UnirestException {
        HttpResponse<String> httpPlayerOwnerResponse = Unirest.get(playerOwnerUri).asString();
        String playerOwnerBody = httpPlayerOwnerResponse.getBody();
        PlayerDTO playerOwnerDTO = gson.fromJson(playerOwnerBody, PlayerDTO.class);
        String playerOwnerBankAcc = playerOwnerDTO.getAccount();

        int i = playerOwnerBankAcc.lastIndexOf("/");
        String playerOwnerBankID = playerOwnerBankAcc.substring(i, playerOwnerBankAcc.length());

        return playerOwnerBankID;
    }

    public String updateHypothecaryByGameID(String gameID, String placeID, String body)
            throws BrokerNotFoundException, PlaceNotFoundException, BrokerMaxAmountHousesRichedException,
            BrokerPlaceWithoutOwnerException, UnirestException, TransactionFailedException {
        checkNotNull(gameID);
        checkNotNull(placeID);
        checkNotNull(body);

        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace = broker.getBrokerPlaceByID(placeID);

        List<EventDTO> eventList = new ArrayList();
        ComponentsDTO componentsDTO = getComponentsDTO(broker);

        String playerBankAccID = getPlayerAccountsID(brokerPlace.getOwner());

        if(!brokerPlace.hasOwner()) throw new BrokerPlaceWithoutOwnerException();

        // transaktion von Bank an Spieler
        HttpResponse<JsonNode> bankResponse = Unirest.post(componentsDTO.getBank()+"/transfer/to/"+playerBankAccID+"/"+broker.getCurrentCostOfPlace(brokerPlace))
                .header("Content-Type","application/json")
                .body("Bank get money an User")
                .asJson();

        if(bankResponse.getStatus() != 201) throw new TransactionFailedException();


        // hole dir events von der Bank
        for(Object o : bankResponse.getBody().getArray()){
            EventDTO e = gson.fromJson(o.toString(),EventDTO.class);
            eventList.add(e);
        }

        int costOfPlace = broker.getNextCostOfPlace(brokerPlace);
        brokerPlace.setHypo(costOfPlace);

        EventDTO eventUpdateHypothec = creatEvent(componentsDTO.getEvent(),
                broker.getGameService(),
                "get hypothecary",
                "update hypothecary",
                "Bank wants to get hypothecary",
                brokerPlace.getHypoCredit(),
                brokerPlace.getOwner(),
                System.currentTimeMillis()+""
        );

        eventList.add(eventUpdateHypothec);

        return gson.toJson(eventList);
    }

    public void removeHypothecaryByGameID(String gameID, String placeID)
            throws BrokerNotFoundException, PlaceNotFoundException, UnirestException, TransactionFailedException {
        checkNotNull(gameID);
        checkNotNull(placeID);

        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace = broker.getBrokerPlaceByID(gameID);

        ComponentsDTO componentsDTO = getComponentsDTO(broker);
        List<EventDTO> eventList = new ArrayList();

        // bezahlen an die Bank um Hypothek aufzuheben
        String ownerBankAccID = getPlayerAccountsID(brokerPlace.getOwner());
        String bankURI = componentsDTO.getBank();

        int amount = brokerPlace.getHypothec();

        HttpResponse<JsonNode> bankResponse = Unirest.post(bankURI+"/transfer/from/"+ownerBankAccID+"/"+amount)
                                                .header("Content-Type","application/json")
                                                .body("User pay for Hypothecary")
                                                .asJson();

        if(bankResponse.getStatus() != 201) throw new TransactionFailedException();

        // hole events von der Bank
        for(Object o : bankResponse.getBody().getArray()){
            EventDTO e = gson.fromJson(o.toString(),EventDTO.class);
            eventList.add(e);
        }

        EventDTO eventRemoveHypothec = creatEvent(componentsDTO.getEvent(),
                                                    broker.getGameService(),
                                                    "pay hypothecary",
                                                    "pay/remove hypothecary",
                                                    "User wants to pay hypothecary from estate",
                                                    brokerPlace.getHypoCredit(),
                                                    brokerPlace.getOwner(),
                                                    System.currentTimeMillis()+""
                                                );

        brokerPlace.setHypo(0);
        eventList.add(eventRemoveHypothec);
    }

    public String playervisitetInJson(String gameID, String placeID, String playerJsonString)
            throws BrokerNotFoundException, PlaceNotFoundException, UnirestException, TransactionFailedException {
        //null abfrage
        checkNotNull(gameID);
        checkNotNull(placeID);
        checkNotNull(playerJsonString);

        Broker broker = getBrokerObjectByID(gameID);
        BrokerPlace brokerPlace =  broker.getBrokerPlaceByID(placeID);
        JSONObject jsonObj = new JSONObject(playerJsonString);
        String visitorPlayerURI = jsonObj.getString("player");
        String ownerPlayerURI = brokerPlace.getOwner();
        List<EventDTO> eventList = new ArrayList();

        //wenn keine Owner, dann return die leere eventliste
        if(ownerPlayerURI.isEmpty()) return gson.toJson(eventList);

        //Accounts von players herrauszufinden
        String visitorBankAccID = getPlayerAccountsID(visitorPlayerURI);
        String ownerBankAccID = getPlayerAccountsID(ownerPlayerURI);


        //URI von bank herrauszufinden
        ComponentsDTO componentsDTO = getComponentsDTO(broker);
        String bank = componentsDTO.getBank();

        //zu zahlende betrag von visitor-player an owner-player
        int visitCost = broker.getVisitCost(brokerPlace);

        //bank URI aufruf zum ausführen von das Transaktion
        HttpResponse<String> httpBankResponse = Unirest.
                post(bank+"/transfer/from/"+visitorBankAccID+"/to/"+ownerBankAccID+"/"+visitCost)
                .header("Content-Type","application/json")
                .body("User pay for visiting estate")
                .asString();

        //wenn statuscode nicht stimmt, springe raus
        if(httpBankResponse.getStatus() != 201) throw new TransactionFailedException();

        String bankBody = httpBankResponse.getBody();

        //add event in der eventliste
        EventDTO eventDTO = gson.fromJson(bankBody, EventDTO.class);
        eventList.add(eventDTO);

        //URI von events
        String event = componentsDTO.getEvent();
        //post auf EVENT
        //URl: /events
        Unirest.post(event).asString();

        return gson.toJson(eventList);
    }


    public String updateBrokerByID(String gameID, String body)
            throws URISyntaxException, BrokerAlreadyExistsException, WrongFormatException, BrokerNotFoundException {

        checkNotNull(gameID);
        checkNotNull(body);

        //id von aktuelle Broker Aktualiesieren
        Broker currentBroker = getBrokerObjectByID(gameID);

        //id von neue Broker Herausfinden
        BrokerDTO brokerDTO = gson.fromJson(body, BrokerDTO.class);
        Broker newBroker = Broker.fromDTO(brokerDTO);

        if(brokerMap.containsKey(newBroker.getId())) { throw new BrokerAlreadyExistsException(); }

        String gameURI = newBroker.getGameURI();
        newBroker = currentBroker.updateGameID(gameURI);

        brokerMap.remove(currentBroker.getId());
        brokerMap.put(newBroker.getId(),newBroker);

        // debug post condition
        if(!brokerMap.containsKey(newBroker.getId())) throw new RuntimeException("PROBLEM");

        return newBroker.getId();
    }
}
