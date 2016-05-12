package Events.EventManagerComponent;

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

    public Subscriber(String id, String game, String uri) {
        this.id = id;
        this.game = game;
        this.uri = uri;
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
                uri
        );
    }

    public static Subscriber fromDTO(SubscriberDTO subscriberDTO){
        checkNotNull(subscriberDTO);

        return new Subscriber(
                subscriberDTO.getId(),
                subscriberDTO.getGame(),
                subscriberDTO.getUri()
        );
    }
}
