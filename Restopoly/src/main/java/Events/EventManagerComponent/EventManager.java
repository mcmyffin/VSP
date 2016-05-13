package Events.EventManagerComponent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import Common.Exceptions.*;
import Events.EventManagerComponent.DTO.EventDTO;
import Events.EventManagerComponent.DTO.SubscriberDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.util.HostMap;
import spark.QueryParamsMap;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 06.04.16.
 */
public class EventManager {

    private final Gson gson;
    private final Map<String,Event> eventMap;
    private final Map<String,Subscriber> subscriberMap; // <subscriberID,Subscriber>
    private final Map<String,Set<String>> subscriberMapList; // <game,List<subscriberID>>
    private final String regexGetterName = "getEventListBy";
    private long subscriptionsCounter = 0;


    public EventManager() {

        this.eventMap = new HashMap();
        this.subscriberMap = new HashMap();
        this.subscriberMapList = new HostMap();
        this.gson = new Gson();
    }

    private synchronized String getNextSubscriptionsID(){
        String id = "/events/subscriptions/"+subscriptionsCounter;
        subscriptionsCounter++;
        return id;
    }


    private synchronized void notifySubscribers(Event event){
        checkNotNull(event);

        String gameURI  = event.getGame();
        if(!subscriberMapList.containsKey(gameURI)) return;



        Set<String> subscribersList = subscriberMapList.get(gameURI);
        for(String subscriberID : subscribersList){

            Subscriber s = subscriberMap.get(subscriberID);
            if(isRegexMatch(event,s.getEventRegex())) s.sendToSubscriber(event);

        }
    }

    private boolean isRegexMatch(Event event, Event regexEvent){

        return  event.getPlayer().contains(regexEvent.getPlayer()) ||
                event.getName().contains(regexEvent.getName()) ||
                event.getType().contains(regexEvent.getType()) ||
                event.getReason().contains(regexEvent.getReason()) ||
                event.getResource().contains(regexEvent.getResource());
    }


    public String addEvent(String jsonBody) throws RequiredJsonParamsNotFoundException {
        checkNotNull(jsonBody);

        Gson gson = new Gson();
        EventDTO dto = gson.fromJson(jsonBody,EventDTO.class);

        String mapID = Integer.toString(eventMap.size());
        String eventID = "/events/"+mapID;
        dto.setId(eventID);
        Event e = Event.fromDTO(dto);


        eventMap.put(eventID,e);
        notifySubscribers(e);
        return eventID;
    }

    private Subscriber getSubScriberObjectById(String subscriberID) throws SubscriberNotFoundException {
        checkNotNull(subscriberID);
        if(!subscriberMap.containsKey(subscriberID)) throw new SubscriberNotFoundException();
        return subscriberMap.get(subscriberID);
    }

    private Event getEventObjectById(String eventID) throws EventNotFoundException {
        checkNotNull(eventID);
        if(!eventMap.containsKey(eventID)) throw new EventNotFoundException();
        return eventMap.get(eventID);
    }

    private List<Event>  getEventListBygame(String gameRegex){
        checkNotNull(gameRegex);

        List<Event> eventList = new ArrayList();
        for(Event e : eventMap.values()){

            boolean contains = e.getGame().contains(gameRegex);
            if(contains) eventList.add(e);

        }
        return eventList;
    }

    private List<Event>  getEventListBytype(String typeRegex){
        checkNotNull(typeRegex);

        List<Event> eventList = new ArrayList();
        for(Event e : eventMap.values()){

            boolean contains = e.getType().contains(typeRegex);
            if(contains) eventList.add(e);

        }
        return eventList;
    }

    private List<Event>  getEventListByname(String nameRegex){
        checkNotNull(nameRegex);

        List<Event> eventList = new ArrayList();
        for(Event e : eventMap.values()){

            boolean contains = e.getName().contains(nameRegex);
            if(contains) eventList.add(e);

        }
        return eventList;
    }

    private List<Event>  getEventListByreason(String reasonRegex){
        checkNotNull(reasonRegex);

        List<Event> eventList = new ArrayList();
        for(Event e : eventMap.values()){

            boolean contains = e.getReason().contains(reasonRegex);
            if(contains) eventList.add(e);

        }
        return eventList;
    }

    private List<Event>  getEventListByresource(String resourceRegex){
        checkNotNull(resourceRegex);

        List<Event> eventList = new ArrayList();
        for(Event e : eventMap.values()){

            boolean contains = e.getResource().contains(resourceRegex);
            if(contains) eventList.add(e);

        }
        return eventList;
    }

    private List<Event>  getEventListByplayer(String playerRegex){
        checkNotNull(playerRegex);

        List<Event> eventList = new ArrayList();
        for(Event e : eventMap.values()){

            boolean contains = e.getPlayer().contains(playerRegex);
            if(contains) eventList.add(e);

        }
        return eventList;
    }

    private Collection<Event> searchByquery(Map<String,String[]> map){
        checkNotNull(map);
        if(map.isEmpty()) return eventMap.values();

        Set<Event> events = new HashSet();

        for(String key : map.keySet()){
            try {
                Method method = this.getClass().getDeclaredMethod(regexGetterName+key, String.class);
                for(String val : map.get(key)) {
                    List<Event> eventList = (List<Event>) method.invoke(this,val);
                    events.addAll(eventList);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return events;
    }

    private String eventCollectionToJson(Collection<Event> collection){
        checkNotNull(collection);

        Collection<EventDTO> dtoCollection = new ArrayList();

        for(Event e : collection){
            EventDTO eventDTO = e.toDTO();
            dtoCollection.add(eventDTO);
        }

        Gson gson = new Gson();
        String searchResultJson = gson.toJson(dtoCollection);

        return searchResultJson;
    }

    public String searchByQuery(QueryParamsMap queryParamsMap) throws QuerryParamsNotFoundException {
        checkNotNull(queryParamsMap);

        if(queryParamsMap.toMap().isEmpty()) throw new QuerryParamsNotFoundException();

        Collection<Event> searchResult = searchByquery(queryParamsMap.toMap());
        return eventCollectionToJson(searchResult);
    }

    public String deleteByQuerry(QueryParamsMap queryParamsMap) throws QuerryParamsNotFoundException {
        checkNotNull(queryParamsMap);

        if(queryParamsMap.toMap().isEmpty()) throw new QuerryParamsNotFoundException();
        Collection<Event> eventCollection = searchByquery(queryParamsMap.toMap());
        for(Event e : eventCollection) eventMap.remove(e.getId().substring(e.getId().indexOf("/")+1));

        return eventCollectionToJson(eventCollection);
    }

    public List<Event> getEventListByid(String idregex){
        checkNotNull(idregex);

        List<Event> eventList = new ArrayList();
        for(Event e : eventMap.values()){

            boolean contains = e.getId().contains(idregex);
            if(contains) eventList.add(e);

        }
        return eventList;

    }

    public String getEventByid(String eventID) throws EventNotFoundException {
        checkNotNull(eventID);

        Event event = getEventObjectById(eventID);
        return gson.toJson(event.toDTO());
    }

    public String getSubscribtions() {
        Set<String> subscribtions = subscriberMap.keySet();
        return  gson.toJson(subscribtions);
    }

    public synchronized String getSubscriberById(String subscriberID) throws SubscriberNotFoundException {
        checkNotNull(subscriberID);

        Subscriber subscriber = getSubScriberObjectById(subscriberID);
        return gson.toJson(subscriber.toDTO());
    }

    public synchronized String createSubscriber(String jsonBody) throws WrongFormatException, RequiredJsonParamsNotFoundException {
        checkNotNull(jsonBody);

        try{
            SubscriberDTO subscriberDTO = gson.fromJson(jsonBody, SubscriberDTO.class);
            subscriberDTO.setId(getNextSubscriptionsID());

            Subscriber subscriber = Subscriber.fromDTO(subscriberDTO);
            subscriberMap.put(subscriber.getId(),subscriber);

            if(subscriberMapList.containsKey(subscriber.getGame())){
                Set<String> subscribersList = subscriberMapList.get(subscriber.getGame());
                subscribersList.add(subscriber.getId());
            }else{
                Set<String> subscribersList = new HashSet();
                subscribersList.add(subscriber.getId());
                subscriberMapList.put(subscriber.getGame(),subscribersList);
            }

            return subscriber.getId();

        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    public String removeSubscriberById(String subscriberID) throws SubscriberNotFoundException {
        checkNotNull(subscriberID);

        Subscriber subscriber = getSubScriberObjectById(subscriberID);
        subscriberMap.remove(subscriberID);

        if(subscriberMapList.containsKey(subscriber.getGame())){
            Set<String> subscriberList = subscriberMapList.get(subscriber.getGame());
            subscriberList.remove(subscriberID);
        }
        return gson.toJson(subscriber.toDTO());
    }
}
