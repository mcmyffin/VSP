package Events.EventManagerComponent;

import Common.Exceptions.RequiredJsonParamsNotFoundException;
import Common.Exceptions.ServiceNotAvaibleException;
import Events.EventManagerComponent.DTO.EventDTO;
import Events.EventManagerComponent.DTO.SubscriberDTO;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.List;

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

    void sendToSubscriber(List<EventDTO> eventlist){
        checkNotNull(eventlist);

        // create Runnabe
        Runnable sendRunnable =  new Runnable() {
            @Override
            public void run() {
                sendToSubscriber(gson.toJson(eventlist));
            }
        };

        // create and run Thread
        Thread sendThread = new Thread(sendRunnable);
        sendThread.start();
    }

    void sendToSubscriber(String obj){
        checkNotNull(obj);

        try{
            HttpResponse<String> response = Unirest.post(uri).header("Content-Type","application/json")
                                                                     .body(obj)
                                                                     .asString();

            if(response.getStatus() != 200){
                System.out.println(uri+" sendet unerwarteten Response-Code\n" +
                                    "post("+uri+")\n" +
                                    "req.header(\"Content-Type\",\"application/json\")\n" +
                                    "req..body("+obj+")\n" +
                                    "res.status: "+response.getStatus()+"\n" +
                                    "res.body: "+response.getBody());
            }

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
