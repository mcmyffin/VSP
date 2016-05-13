package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTOs.BrokerDTO;
import Brokers.BrokerManagerComponent.DTOs.BrokerPlaceDTO;
import Common.Exceptions.BrokerAlreadyExistsException;
import Common.Exceptions.BrokerNotFoundException;
import Common.Exceptions.PlaceNotFoundException;
import Common.Exceptions.WrongFormatException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URISyntaxException;
import java.util.HashMap;
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

        return null;
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
}
