package Events.EventManagerComponent;

import Common.Exceptions.RequiredJsonParamsNotFoundException;
import Events.EventManagerComponent.DTO.EventDTO;
import Events.EventManagerComponent.DTO.SubscriberDTO;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 11.05.16.
 */
public class Subscriber {

    private final Gson gson;
    private final String id;
    private String game;
    private String uri;
    private Event eventRegex;


    public Subscriber(String id, String game, String uri,Event eventRegex) {
        this.id = id;
        this.game = game;
        this.uri = uri;
        this.eventRegex = eventRegex;
        this.gson = new Gson();
    }

    public String getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public String getUri() {
        return uri;
    }

    public Event getEventRegex() {
        return eventRegex;
    }

    void sendToSubscriber(Event event){
        checkNotNull(event);
        sendToSubscriber(event.toDTO());
    }

    void sendToSubscriber(EventDTO eventDTO){
        checkNotNull(eventDTO);

        try{

            String json = gson.toJson(eventDTO);
            Unirest.post(uri).header("Content-Type","application/json")
                                                             .body(json)
                                                             .asString();

        }catch (UnirestException ex){
            System.out.println(uri+" unavailable");
        }
    }


    public SubscriberDTO toDTO(){
        return new SubscriberDTO(
                id,
                game,
                uri,
                eventRegex.toDTO()
        );
    }

    public static Subscriber fromDTO(SubscriberDTO subscriberDTO) throws RequiredJsonParamsNotFoundException {
        checkNotNull(subscriberDTO);

        return new Subscriber(
                subscriberDTO.getId(),
                subscriberDTO.getGame(),
                subscriberDTO.getUri(),
                Event.fromDTO(subscriberDTO.getEvent())
        );
    }
}
