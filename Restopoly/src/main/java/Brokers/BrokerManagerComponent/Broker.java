package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTO.BrokerDTO;
import Games.GameManagerComponent.Components;
import Games.GameManagerComponent.DTO.ComponentsDTO;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sasa on 11.05.16.
 */
public class Broker {

    private final String id;
    private String gameID;
    private String gameService;
    private String estates;
    private Map<String, BrokerPlace> placesMap;

    public Broker(String game, String estates) throws URISyntaxException {

        placesMap = new HashMap();
        gameID = getGameIDFromURI(game);
        gameService = getHostFromURI(game);
        id = "/broker" + gameID;

        this.estates = id+"/places";

    }

    public static Broker fromDTO(BrokerDTO brokerDTO) throws URISyntaxException {
        checkNotNull(brokerDTO);

        Broker broker = new Broker(brokerDTO.getGame(), brokerDTO.getEstates());
        return broker;
    }


    private String getHostFromURI(String uri) throws URISyntaxException {
        URI u = URI.create(uri);

        if (u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri, "URI is not absolute");
        String scheme = u.getScheme();
        String host = u.getHost();
        int port = (u.getPort() == -1 ? 80 : u.getPort());

        return scheme + "://" + host + ":" + port;
    }

    private String getGameIDFromURI(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        if (u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri, "URI is not absolute");

        String path = u.getPath();
        int i = path.lastIndexOf("/");
        return path.substring(i,path.length());

    }

//    private ComponentsDTO getGameComponent() throws UnirestException {
//        HttpResponse<String> httpres = Unirest.get(gameService+"/components").asString();
//
//        if(httpres.getStatus() == 200){
//            String body = httpres.getBody();
//            Gson gson = new Gson();
//            ComponentsDTO dto = gson.fromJson(body, ComponentsDTO.class);
//            return dto;
//        }
//        throw new UnirestException("Wron Status from GameService in Broker");
//    }

    private void initializePlaces() throws UnirestException {
       //TODO



    }


    public String getId() {
        return id;
    }

    public String getGameService() {
        return gameService;
    }

    public BrokerDTO toDTO() {
        BrokerDTO brokerDTO = new BrokerDTO(id,gameService,estates);
        return brokerDTO;
    }
}
