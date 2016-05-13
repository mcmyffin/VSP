package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTOs.BrokerDTO;
import Common.Exceptions.PlaceNotFoundException;
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


    private synchronized String getNextBrokerplaceID() {
        int id = placesMap.size();
        String placeID = this.id+"/places/"+id;
        return placeID;
    }

    private void createBrokerplace(String name, int[] cost) {

        String placeID = getNextBrokerplaceID();
        String place = "";
        String owner = "";
        int value = 0;
        int[] rent = {};
        int[] houses = {};
        String visit = this.id+"/places/visit";
        String hypocredit = this.id+"places/hypothecarycredit";

        BrokerPlace brokerPlace = new BrokerPlace(placeID,name,place, owner,value,rent, cost, houses, visit, hypocredit);
        placesMap.put(placeID,brokerPlace);
    }

    private void initializePlaces() throws UnirestException {

        //Deutsche Grund version Wiki/Monopoly (abgeschwaecht)
        //Strassen
        createBrokerplace("Badstrasse", new int[]{60, 90, 120, 150, 180, 250}); //+30 / 70
        createBrokerplace("Turmstrasse", new int[]{60, 90, 120, 150, 180, 250});

        createBrokerplace("Chausseestrasse", new int[]{100, 140, 180, 220, 260, 340}); //+40 / 80
        createBrokerplace("Elisenstrasse", new int[]{100, 140, 180, 220, 260, 340});
        createBrokerplace("Poststrasse", new int[]{120, 140, 180, 220, 260, 340});

        createBrokerplace("Seestrasse", new int[]{140, 190, 240, 290, 340, 430 });//+50 / 90
        createBrokerplace("Hafenstrasse", new int[]{140, 190, 240, 290, 340, 430});
        createBrokerplace("Neue Strasse", new int[]{160, 190, 240, 290, 340, 430});

        createBrokerplace("Muenschener Strasse", new int[]{180, 260, 300, 360, 420, 520});//+60 / 100
        createBrokerplace("Wiener Strasse", new int[]{180, 260, 300, 360, 420, 520});
        createBrokerplace("Berliner Strasse", new int[]{200, 260, 300, 360, 420, 520});

        createBrokerplace("Theaterstrasse", new int[]{220, 290, 360, 430, 500, 610});//+70 / 110
        createBrokerplace("Museumsstrasse", new int[]{220, 290, 360, 430, 500, 610});
        createBrokerplace("Opernplatz", new int[]{240, 290, 360, 430, 500, 610});

        createBrokerplace("Lessinstrasse", new int[]{260, 340, 420, 500, 580, 700}); //+80 / 120
        createBrokerplace("Schillerstrasse", new int[]{260, 340, 420, 500, 580, 700});
        createBrokerplace("Goethestrasse", new int[]{280, 340, 420, 500, 580, 700});

        createBrokerplace("Rathhausplatz", new int[]{300, 390, 480, 570, 660, 790});//+90 / 130
        createBrokerplace("Hauptstrasse", new int[]{300, 390, 480, 570, 660, 790});
        createBrokerplace("Bahnhofstrasse", new int[]{320, 390, 480, 570, 660, 790});

        createBrokerplace("Parkstrasse", new int[]{350, 450, 550, 650, 750, 890});//+100 / 140
        createBrokerplace("Schlossallee", new int[]{400, 450, 550, 650, 750, 890});

        //bahnhoefe
        createBrokerplace("Suedbahnhof", new int[]{200});
        createBrokerplace("Westbahnhof", new int[]{200});
        createBrokerplace("Nordbahnhof", new int[]{200});
        createBrokerplace("Hauptbahnhof", new int[]{200});

        //werke
        createBrokerplace("Elektrizitaetswerk", new int[]{150});
        createBrokerplace("Wasserwerk", new int[]{150});


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

    public Map<String,BrokerPlace> getBrokerPlaceMap() {
        return placesMap;
    }

    public BrokerPlace getBrokerPlaceByID(String placeID) throws PlaceNotFoundException {
        if(!placesMap.containsKey(placeID)) throw new PlaceNotFoundException();
        return placesMap.get(placeID);
    }
}
