package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.DTOs.*;
import Boards.Main;
import Brokers.BrokerManagerComponent.DTOs.BrokerPlaceDTO;
import Common.Exceptions.*;
import Common.Util.IPFinder;
import Common.Util.URIObject;
import Common.Util.URIParser;
import Games.GameManagerComponent.DTO.ComponentsDTO;
import Games.GameManagerComponent.DTO.ServicesDTO;
import com.google.gson.Gson;
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
public class Board {

    private final String id;
    private URIObject gameURIObject;
    private long counter;
    private List<Field> fieldList;
    private Map<String,Pawn> pawnsMap;        // Map<PawnID,Pawn>
    private Map<String,Place> placeMap;       // Map<PlaceID,Place>
    private Field los;
    private Field jail;

    private final Gson gson;
    private RollPersistence rollPersistence;

    public Board(String game) throws URISyntaxException {
        this.counter = 0;

        this.gameURIObject = URIParser.createURIObject(game);

        this.fieldList= new ArrayList();
        this.pawnsMap = new HashMap();
        this.placeMap = new HashMap();
        this.gson     = new Gson();

        id = "/boards"+gameURIObject.getId();

        this.rollPersistence = new RollPersistence();
    }

    private synchronized String getNextPlaceID(){

        String id = this.id+"/places/"+counter;
        counter++;
        return id;
    }

    private synchronized String getNextPawnID(){

        String id = this.id+"/pawns/"+pawnsMap.size();
        return id;
    }

    private String[] createPlace(int number_of_places, String name, String broker){
        String[] ids = new String[number_of_places];
        for(int i = 0; i < number_of_places ; i++){
            String placeID = getNextPlaceID();
            Place place = new Place(placeID,name,broker);
            placeMap.put(placeID,place);

            Field f = new Field(place);
            fieldList.add(f);

            ids[i] = placeID;
        }
        return ids;
    }

    private void registerPlace(String brokerURI, String placeURI) throws UnirestException {
        checkNotNull(brokerURI);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("place",placeURI);

        Unirest.put(brokerURI).header("Content-Type","application/json").body(jsonObject.toString()).asString();
    }

    private synchronized Field createLosField(){
        String placeID = getNextPlaceID();
        Place place = new Place(placeID,"LOS","");

        placeMap.put(placeID,place);

        return new Field(place);
    }

    private synchronized Field createJail(){
        String placeID = getNextPlaceID();
        Place place = new Place(placeID,"Im Gefägnis/nur zu besuch","");

        placeMap.put(placeID,place);
        Field field = new Field(place);
        fieldList.add(field);
        return field;
    }

    private ServicesDTO getGamesServices() throws UnirestException {

        HttpResponse<String> response = Unirest.get(gameURIObject.getAbsoluteURI()+"/services").asString();
        if(response.getStatus() == 200) {
            ServicesDTO servicesDTO = gson.fromJson(response.getBody(), ServicesDTO.class);
            return servicesDTO;
        } else throw new UnirestException("Response Status not 200");
    }

    private ComponentsDTO getGamesComponents() throws UnirestException {

        HttpResponse<String> response = Unirest.get(gameURIObject.getAbsoluteURI()+"/components").asString();
        if(response.getStatus() == 200) {
            ComponentsDTO componentsDTO = gson.fromJson(response.getBody(), ComponentsDTO.class);
            return componentsDTO;
        } else throw new UnirestException("Response Status not 200");
    }

    private List<String> getEstatesIDList() throws UnirestException {

        List<String> brokerPlacesURIList = new ArrayList<>();
        String brokerURI = getGamesComponents().getBroker();

        if (brokerURI == null || brokerURI.isEmpty()) throw new UnirestException("Broker URI not valid: "+brokerURI);

        HttpResponse<JsonNode> brokerResponse = Unirest.get(brokerURI + "/places").asJson();
        if (brokerResponse.getStatus() == 200) {

            JSONArray jsonArray = brokerResponse.getBody().getArray();
            for (Object o : jsonArray) brokerPlacesURIList.add(o.toString());

        }
        return brokerPlacesURIList;
    }

    private synchronized void createAndRegisterPlacesFromBroker() throws UnirestException, URISyntaxException {

        List<String> estatesIdList = getEstatesIDList();

        List<String> brokerPlacesURIList = new ArrayList<>();
        URIObject uriObject = URIParser.createURIObject(getGamesComponents().getBroker());
        String brokerURI = uriObject.getHost();

        for(String estateID : estatesIdList){

            String estateURI = brokerURI+estateID;
            HttpResponse<String> response = Unirest.get(estateURI).asString();

            if(response.getStatus() != 200){
                throw new UnirestException("Broker: get("+estateURI+") -> statuscode != 200\n" +
                                            "Statuscode: "+response.getStatus()+"\n" +
                                            "Body: "+response.getBody().toString());
            }

            JSONObject jsonObject = new JSONObject(response.getBody());

            String name     = jsonObject.getString("name");
            String broker   = estateURI;

            String[] placeIDs = createPlace(1,name,broker);
            String placeURI = Main.URL+placeIDs[0];

            registerPlace(estateURI,placeURI); // registriere den Place beim Broker (per PUT)
        }
    }

    public void initialize() throws UnirestException, URISyntaxException {

        // create non estates

        // LOS
        los = createLosField();

        // Im Gefägnis/nur zu besuch
        jail = createJail();

        // Gemeinschaftsfeld x 3
        createPlace(3, "Gemeinschaftsfeld", "");

//        // Einkommensteuer x 1
//        createPlace(1, "Einkommensteuer", "");

        // Ereignisfeld x 3
        createPlace(3, "Ereignisfeld", "");

        // Frei Parken
        createPlace(1, "Frei Parken", "");

        // Gehen ins Gefängnis
        createPlace(1, "Gehen ins Gefängnis", "");

//        // Zusatzsteuer
//        createPlace(1, "Zusatzsteuer", "");

        createAndRegisterPlacesFromBroker();

        // shuffel places
        Collections.shuffle(fieldList);

        // add "LOS" field at first position
        fieldList.add(0,los);

    }

    synchronized void setPawnToJail(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);

        int jailPos = fieldList.indexOf(jail);
        setPawnTo(pawnID,jailPos);
    }

    public URIObject getGameURI(){ return gameURIObject;}

    public String getId() {
        return id;
    }

    public Collection<Field> getFieldMap() {
        return fieldList;
    }

    public Map<String, Pawn> getPawnsMap() {
        return pawnsMap;
    }


    public List<Integer> getPositions(){
        List<Integer> positionList = new ArrayList();
        for(int i = 0; i < fieldList.size(); i++){
            if(!fieldList.get(i).getPawns().isEmpty()) positionList.add(i);
        }

        return positionList;
    }

    public BoardDTO toDTO() {

        List<FieldDTO> fieldDTOList = new ArrayList<>();
        for(Field field : fieldList) fieldDTOList.add(field.toDTO());

        return new BoardDTO(
                this.id,
                fieldDTOList,
                getPositions()
        );
    }

    public synchronized String addPawn(PawnDTO pawnDTO) {
        checkNotNull(pawnDTO);

        String pawnID = getNextPawnID();
        pawnDTO.setId(pawnID);
        pawnDTO.setPlaces(this.id+"/places/0");
        pawnDTO.setMove(pawnID+"/move");
        pawnDTO.setRoll(pawnID+"/roll");
        pawnDTO.setPosition(0);


        Pawn p = Pawn.fromDTO(pawnDTO);

        // add Pawn to pawnsMap
        pawnsMap.put(p.getId(),p);

        // add to Field
        Field f = fieldList.get(0); // first Field == LOS-Field
        f.addPawn(p);

        return p.getId();
    }

    public Pawn getPawnById(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);

        if(!pawnsMap.containsKey(pawnID)) throw new PawnNotFoundException();
        return pawnsMap.get(pawnID);
    }

    public synchronized void updatePawn(String pawnID, PawnDTO pawnDTO) throws PawnNotFoundException{
        checkNotNull(pawnID);
        checkNotNull(pawnDTO);

        Pawn pawn = getPawnById(pawnID);

        // update Player uri in PAWN
        if(pawnDTO.getPlayer() != null) pawn.setPlayer(pawnDTO.getPlayer());

        // alle anderen Werte sind nicht erlaubt. Das Board kümmert sich drum
    }

    public synchronized void removePawn(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);

        if(!pawnsMap.containsKey(pawnID)) throw new PawnNotFoundException();

        Pawn pawn = pawnsMap.remove(pawnID);
        int position = pawn.getPosition();

        Field f = fieldList.get(position);
        f.removePawn(pawnID);
    }

    public RollPersistence getRollPersistence() {
        return rollPersistence;
    }

    public synchronized PawnMove pawnRoll(String pawnID, int number) throws PawnNotFoundException {
        checkNotNull(pawnID);

        // get Pawn
        Pawn pawn = getPawnById(pawnID);

        // get Field and remove pawn from Field
        int positionBefore = pawn.getPosition();
        Field field = fieldList.get(positionBefore);
        field.removePawn(pawnID);

        // get new Position
        int size = fieldList.size();
        int positionAfter = (positionBefore + number) % size;

        // set new Position to Pawn and add Pawn to Field at Position
        pawn.setPosition(positionAfter);
        field = fieldList.get(positionAfter);
        field.addPawn(pawn);

        PawnMove pawnMove = new PawnMove(
                pawn,
                field.getPlace(),
                positionBefore <= positionAfter
        );

        return pawnMove;
    }

    private synchronized void setPawnTo(String pawnID, int pos) throws PawnNotFoundException {
        // get Pawn
        Pawn pawn = getPawnById(pawnID);

        // get Field and remove pawn from Field
        int positionBefore = pawn.getPosition();
        Field field = fieldList.get(positionBefore);
        field.removePawn(pawnID);

        // set new Position to Pawn and add Pawn to Field at Position
        pawn.setPosition(pos);
        field = fieldList.get(pos);
        field.addPawn(pawn);
    }


    public Collection<String> getPlacesIDList() {

        Collection<String> placesIDList = new ArrayList();
        for(Place p : placeMap.values()){
            placesIDList.add(p.getId());
        }

        return placesIDList;
    }

    public Place getPlaceByID(String placeID) throws PlaceNotFoundException {
        checkNotNull(placeID);

        if(!placeMap.containsKey(placeID)) throw new PlaceNotFoundException();
        return placeMap.get(placeID);
    }
}
