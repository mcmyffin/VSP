package Events; /**
 * Created by dima on 05.04.16.
 */
//import Common.Abstract.MainAbstract;
import Common.Abstract.MainAbstract;
import Common.Util.IPFinder;
import Events.EventManagerComponent.EventManager;
import YellowPage.RegistrationService;
import YellowPage.YellowPageService;

import static spark.Spark.*;
public class Main extends MainAbstract {

    public static int port = 4568;
    public static String ip = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Manage Events";
    public static String service = "events";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/events";


    public Main(){
        super(port,ip,name,description,service,URLService);
    }


    public static void main(String[] args) {

        port(port);

        Main main = new Main();
        EventManager eventManager = new EventManager();
        RegistrationService registrationService = new RegistrationService(main);
        registrationService.startRegistration();


        YellowPageService.startListening();

        get("/events", (req,res) ->  {
            res.status(200);
            res.header("Content-Type","application/json");
            String resultJson = eventManager.searchByQuery(req.queryMap());
            return resultJson;
        });

        post("/events", (req,res) -> {
            res.status(201);
            String jsonBody = req.body();
            String eventID = eventManager.addEvent(jsonBody);
            res.header("Location",URL+"/"+eventID);
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
