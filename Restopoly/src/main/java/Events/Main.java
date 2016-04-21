package Events; /**
 * Created by dima on 05.04.16.
 */
import Events.EventManagerComponent.EventManager;

import static spark.Spark.*;
public class Main {


    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        RegistrationService service = new RegistrationService("http://172.18.0.8:4567/services");

        port(4567);

        service.signIn();

        get("/events", (req,res) ->  {
            res.status(200);
            res.header("Content-Type","application/json");
            String resultJson = eventManager.searchByQuery(req.queryMap());
            return resultJson;
        });

        post("/events", (req,res) -> {
            String jsonBody = req.body();
            eventManager.addEvent(jsonBody);
            res.status(201);
            return "";
        });

        delete("/events", (req,res) -> {
            res.status(202);
            if(req.queryMap().toMap().isEmpty()) throw new Exception("Events ID not found in querry");
            String deleted = eventManager.deleteByQuerry(req.queryMap());

            return deleted;
        });

        get("/events/:eventid", (req,res) -> {
            res.status(200);
            res.header("Content-Type","application/json");
            String eventID = req.params(":eventid");
            String eventJson = eventManager.getEventByID(eventID);
            return eventJson;
        });

        get("/api", (req,res) -> {

            // todo rest api in yaml/raml
            return "TODO";
        });

        exception(Exception.class, (ex, req,res) -> {
            res.status(400);
            res.body(ex.getMessage());
        });
    }

}
