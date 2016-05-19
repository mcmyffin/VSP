package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTOs.BrokerDTO;
import Brokers.BrokerManagerComponent.DTOs.BrokerPlaceDTO;
import Common.Exceptions.*;
import Events.EventManagerComponent.DTO.EventDTO;
import Games.GameManagerComponent.DTO.ComponentsDTO;
import Games.GameManagerComponent.DTO.PlayerDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //TODO

        throw new UnsupportedOperationException();
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

    public String getBrokerOwnerByGameID(String gameID, String placeID) {
        checkNotNull(gameID);
        checkNotNull(placeID);

        //TODO  -> impl1
        throw new UnsupportedOperationException();
    }

    public String updateOwnrByGameID(String gameID, String placeID, String body) {
        checkNotNull(gameID);
        checkNotNull(placeID);
        checkNotNull(body);

        //TODO  -> Impl2
        throw new UnsupportedOperationException();
    }

    public Object getOwnerJsonStringByID(String gameID) {
        checkNotNull(gameID);

        //TODO  -> Impl2
        throw new UnsupportedOperationException();
    }

    public void removeOwnerByGameID(String gameID, String placeID) {
        checkNotNull(gameID);
        checkNotNull(placeID);

        //TODO  -> Impl3
        throw new UnsupportedOperationException();
    }

    public String updateHypothecaryByGameID(String gameID, String placeID, String body) {
        checkNotNull(gameID);
        checkNotNull(placeID);
        checkNotNull(body);

        //TODO  -> Impl4
        throw new UnsupportedOperationException();
    }

    public void removeHypothecaryByGameID(String gameID, String placeID) {
        checkNotNull(gameID);
        checkNotNull(placeID);

        //TODO  -> Impl5
        throw new UnsupportedOperationException();
    }

    public String playervisitetInJson(String gameID, String placeID, String playerJsonString)
            throws BrokerNotFoundException, PlaceNotFoundException, UnirestException, TransactionNotFunctionException {
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
        HttpResponse<String> httpVisitorResponse = Unirest.get(visitorPlayerURI).asString();
        HttpResponse<String> httpOwnerResponse = Unirest.get(ownerPlayerURI).asString();

        String visitorBody = httpVisitorResponse.getBody();
        String ownerBody = httpOwnerResponse.getBody();

        PlayerDTO visitorPlayerDTO = gson.fromJson(visitorBody, PlayerDTO.class);
        PlayerDTO ownerPlayerDTO = gson.fromJson(ownerBody, PlayerDTO.class);

        String visitorBankAcc = visitorPlayerDTO.getAccount();
        String ownerBankAcc = ownerPlayerDTO.getAccount();

        //URI von bank herrauszufinden
        String gameURI = broker.getGameService();
        HttpResponse<String> httpGameRes = Unirest.get(gameURI+"/components").asString();
        String gameBody = httpGameRes.getBody();
        ComponentsDTO componentsDTO = gson.fromJson(gameBody, ComponentsDTO.class);
        String bank = componentsDTO.getBank();

        //zu zahlende betrag von visitor-player an owner-player
        int visitCost = broker.getVisitCost(brokerPlace);

        //bank URI aufruf zum ausführen von das Transaktion
        HttpResponse<String> httpBankResponse = Unirest.
                post(bank+"/transfer/from/"+visitorBankAcc+"/to/"+ownerBankAcc+"/"+visitCost).asString();

        //wenn statuscode nicht stimmt, springe raus
        if(httpBankResponse.getStatus() != 201) throw new TransactionNotFunctionException();

        String bankBody = httpBankResponse.getBody();

        //add event in der eventliste
        EventDTO eventDTO = gson.fromJson(bankBody, EventDTO.class);
        eventList.add(eventDTO);

        return gson.toJson(eventList);
    }
}
