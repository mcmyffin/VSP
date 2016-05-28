package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTOs.BrokerDTO;
import Common.Exceptions.BrokerMaxAmountHousesRichedException;
import Common.Exceptions.PlaceNotFoundException;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        initializePlaces();
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

    private String getGameURI(){
        return gameService+"/games"+gameID;
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

    private void createBrokerplace(String name, int[] cost, int[] rent) {

        String placeID = getNextBrokerplaceID();
        String place = "";
        String owner = "";
        int value = 0;
        int houses = 0;
        String visit = this.id+"/places/visit";
        String hypocredit = this.id+"places/hypothecarycredit";

        BrokerPlace brokerPlace = new BrokerPlace(placeID,name,place, owner,value,rent, cost, houses, visit, hypocredit);
        placesMap.put(placeID,brokerPlace);
    }

    private void initializePlaces() {

        //Deutsche Grund version Wiki/Monopoly (abgeschwaecht)
        //Strassen
        createBrokerplace("Badstrasse", new int[]{60, 90, 120, 150, 180, 250}, new int[]{30, 60, 90, 120, 190}); //+30 / 70
        createBrokerplace("Turmstrasse", new int[]{60, 90, 120, 150, 180, 250}, new int[]{30, 60, 90, 120, 190});

        createBrokerplace("Chausseestrasse", new int[]{100, 140, 180, 220, 260, 340}, new int[]{40, 80, 120, 160, 240}); //+40 / 80
        createBrokerplace("Elisenstrasse", new int[]{100, 140, 180, 220, 260, 340}, new int[]{40, 80, 120, 160, 240});
        createBrokerplace("Poststrasse", new int[]{120, 140, 180, 220, 260, 340}, new int[]{60, 100, 140, 180, 260});

        createBrokerplace("Seestrasse", new int[]{140, 190, 240, 290, 340, 430 }, new int[]{50, 100, 150, 200, 290});//+50 / 90
        createBrokerplace("Hafenstrasse", new int[]{140, 190, 240, 290, 340, 430}, new int[]{50, 100, 150, 200, 290});
        createBrokerplace("Neue Strasse", new int[]{160, 190, 240, 290, 340, 430}, new int[]{70, 120, 170, 220, 310});

        createBrokerplace("Muenschener Strasse", new int[]{180, 260, 300, 360, 420, 520}, new int[]{60, 120, 180, 240, 340});//+60 / 100
        createBrokerplace("Wiener Strasse", new int[]{180, 260, 300, 360, 420, 520}, new int[]{60, 120, 180, 240, 340});
        createBrokerplace("Berliner Strasse", new int[]{200, 260, 300, 360, 420, 520}, new int[]{80, 140, 200, 260, 360});

        createBrokerplace("Theaterstrasse", new int[]{220, 290, 360, 430, 500, 610}, new int[]{70, 140, 210, 280, 390});//+70 / 110
        createBrokerplace("Museumsstrasse", new int[]{220, 290, 360, 430, 500, 610}, new int[]{70, 140, 210, 280, 390});
        createBrokerplace("Opernplatz", new int[]{240, 290, 360, 430, 500, 610}, new int[]{90, 160, 230, 300, 410});

        createBrokerplace("Lessinstrasse", new int[]{260, 340, 420, 500, 580, 700}, new int[]{80, 160, 240, 320, 440}); //+80 / 120
        createBrokerplace("Schillerstrasse", new int[]{260, 340, 420, 500, 580, 700}, new int[]{80, 160, 240, 320, 440});
        createBrokerplace("Goethestrasse", new int[]{280, 340, 420, 500, 580, 700}, new int[]{100, 180, 260, 340, 460});

        createBrokerplace("Rathhausplatz", new int[]{300, 390, 480, 570, 660, 790}, new int[]{90, 180, 270, 360, 430});//+90 / 130
        createBrokerplace("Hauptstrasse", new int[]{300, 390, 480, 570, 660, 790}, new int[]{90, 180, 270, 360, 430});
        createBrokerplace("Bahnhofstrasse", new int[]{320, 390, 480, 570, 660, 790}, new int[]{110, 200, 290, 380, 450});

        createBrokerplace("Parkstrasse", new int[]{350, 450, 550, 650, 750, 890}, new int[]{100, 200, 300, 400, 540});//+100 / 140
        createBrokerplace("Schlossallee", new int[]{400, 450, 550, 650, 750, 890}, new int[]{150, 250, 350, 450, 590});

        //bahnhoefe
        createBrokerplace("Suedbahnhof", new int[]{200}, new int[]{100});
        createBrokerplace("Westbahnhof", new int[]{200}, new int[]{100});
        createBrokerplace("Nordbahnhof", new int[]{200}, new int[]{100});
        createBrokerplace("Hauptbahnhof", new int[]{200}, new int[]{100});

        //werke
        createBrokerplace("Elektrizitaetswerk", new int[]{150}, new int[]{100});
        createBrokerplace("Wasserwerk", new int[]{150}, new int[]{100});
    }

    public String getId() {
        return id;
    }

    public String getGameService() {
        return gameService;
    }

    public BrokerDTO toDTO() {
        BrokerDTO brokerDTO = new BrokerDTO(id,getGameURI(),estates);
        return brokerDTO;
    }

    public Map<String,BrokerPlace> getBrokerPlaceMap() {
        return placesMap;
    }

    public BrokerPlace getBrokerPlaceByID(String placeID) throws PlaceNotFoundException {
        if(!placesMap.containsKey(placeID)) throw new PlaceNotFoundException();
        return placesMap.get(placeID);
    }

    public int getVisitCost(BrokerPlace brokerPlace) {
        return brokerPlace.getRentListe().get(brokerPlace.getHouses());
    }

    public int getNextCostOfPlace(BrokerPlace brokerPlace) throws BrokerMaxAmountHousesRichedException {

        List<Integer> costList = brokerPlace.getCostList();

        if(brokerPlace.getCostList().size() <= brokerPlace.getHouses()) throw new BrokerMaxAmountHousesRichedException();

        return costList.get(brokerPlace.getHouses());
    }

    public int getCurrentCostOfPlace(BrokerPlace brokerPlace){
        List<Integer> costList = brokerPlace.getCostList();

        int houses = brokerPlace.getHouses();
        return costList.get(houses == 0 ? 0 : houses - 1);
    }
}
