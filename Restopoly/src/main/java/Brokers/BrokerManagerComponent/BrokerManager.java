package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTO.BrokerDTO;
import Common.Exceptions.BrokerAlreadyExistsException;
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
    private long brokerIDCounter = 0;

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
        //TODO

        }catch(JsonSyntaxException ex){
            throw new WrongFormatException();

        }
        //TODO
        return "TODO";

    }

    public String getbrokerById(String gameID) {
        checkNotNull(gameID);
        //TODO
        return "TODO";
    }

    public String getBrokerPlacesByGameId(String gameID) {
        checkNotNull(gameID);
        //TODO
        return "TODO";
    }
}
