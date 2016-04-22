package Events.EventManagerComponent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import Events.EventManagerComponent.DTO.EventDTO;
import com.google.gson.Gson;
import spark.QueryParamsMap;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 06.04.16.
 */
public class EventManager {

    private final Map<String,Event> eventMap;
    private final String regexGetterName = "getEventListBy";

    public EventManager() {
        this.eventMap = new HashMap();
    }



    public void addEvent(String jsonBody) {
        checkNotNull(jsonBody);

        Gson gson = new Gson();
        EventDTO dto = gson.fromJson(jsonBody,EventDTO.class);

        String mapID = Integer.toString(eventMap.size());
        String eventID = "event/"+mapID;
        Event e = new Event(
                                eventID,
                                dto.getGame(),
                                dto.getType(),
                                dto.getName(),
                                dto.getReason()
                            );

        eventMap.put(mapID,e);
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

            boolean contains = e.getTime().contains(typeRegex);
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

    public String searchByQuery(QueryParamsMap queryParamsMap){
        checkNotNull(queryParamsMap);

        Collection<Event> searchResult = searchByquery(queryParamsMap.toMap());
        return eventCollectionToJson(searchResult);
    }

    public String deleteByQuerry(QueryParamsMap queryParamsMap) {
        checkNotNull(queryParamsMap);

        Collection<Event> eventCollection = searchByquery(queryParamsMap.toMap());
        for(Event e : eventCollection) eventMap.remove(e.getId());

        return eventCollectionToJson(eventCollection);
    }

    public List<Event> getEventListByid(String eventID) {
        checkNotNull(eventID);

        List<Event> eventList = new ArrayList();
        Event e = eventMap.get(eventID);

        if(e == null) return eventList;
        eventList.add(e);
        return eventList;
    }

    public String getEventByID(String eventID){
        Event e = eventMap.get(eventID);
        if(e == null) return "";

        Gson g = new Gson();
        return g.toJson(e.toDTO());
    }
}
