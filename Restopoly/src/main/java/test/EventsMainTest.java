package test;

import Events.EventManagerComponent.DTO.EventDTO;
import Events.Main;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
import org.junit.Assert.*;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by sasa on 21.04.16.
 */
public class EventsMainTest {

    private String GAME = "game";
    private String TYPE = "type";
    private String NAME = "name";
    private String REASON = "reason";
    private String RESOURCE = "resource";


    public EventsMainTest() {
        Main.main(null);
    }

    @Test
    public void testPostGetEvents() throws UnirestException {
        EventDTO eventDTO = new EventDTO(GAME+1,TYPE+1,NAME+1,REASON+1,RESOURCE+1);

        Gson g = new Gson();
        String s = g.toJson(eventDTO);
        try {
            HttpResponse postResponse = new Unirest().post("http://0.0.0.0:4567/events")
                                                 .header("Content-Type","application/json")
                                                 .body(s)
                                                 .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        HttpResponse<JsonNode> response = Unirest.get("http://0.0.0.0:4567/events").asJson();

        Object jsonObject  = response.getBody().getArray().get(0);
        EventDTO eventDTO1 = g.fromJson(jsonObject.toString(),EventDTO.class);


        String eventId = eventDTO1.getId();

        HttpResponse<JsonNode> response2 = Unirest.get("http://0.0.0.0:4567/"+eventId).asJson();
//        System.out.printf(response2.getBody().toString());
        EventDTO eventDTO3 = g.fromJson(response2.getBody().toString(), EventDTO.class);


        assertEquals(200,response.getStatus());
        assertEquals(eventDTO.getGame(),eventDTO1.getGame());
        assertEquals(eventId,eventDTO3.getId());

    }


}