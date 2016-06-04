package Events; /**
 * Created by dima on 05.04.16.
 */
//import Common.Abstract.MainAbstract;
import Common.Abstract.MainAbstract;
import Common.Exceptions.*;
import Common.Util.IPFinder;
import Events.EventManagerComponent.EventManager;
import YellowPage.RegistrationService;
import YellowPage.YellowPageService;
import spark.Request;
import spark.Response;

import static spark.Spark.*;
public class Main extends MainAbstract {

    public static int port = 4567;
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
        System.out.println("=== Events ===");
//        RegistrationService registrationService = new RegistrationService(main);
//        registrationService.startRegistration();


        YellowPageService.startListening();


        get("/events/subscriptions", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String subscriptionsJsonArray = eventManager.getSubscribtions();
            return subscriptionsJsonArray;
        });

        get("/events/subscriptions/:subscription", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String subscriberID = "/events/subscriptions/"+req.params(":subscription");
            String subscriberJson = eventManager.getSubscriberById(subscriberID);

            return subscriberJson;
        });

        delete("/events/subscriptions/:subscription", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String subscriberID = "/events/subscriptions/"+req.params(":subscription");
            String subscriberJson = eventManager.removeSubscriberById(subscriberID);

            return subscriberJson;
        });

        post("/events/subscriptions", (req,res) -> {
            res.status(200);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String jsonBody = req.body();
            String subscriberID = eventManager.createSubscriber(jsonBody);
            setLocationHeader(res,URL+subscriberID);

            return eventManager.getSubscriberById(subscriberID);
        });

        get("/events", (req,res) ->  {
            res.status(200);
            setResponseContentTypeJson(res);

            String resultJson = eventManager.searchByQuery(req.queryMap());
            return resultJson;
        });

        post("/events", (req,res) -> {
            res.status(201);
            checkContentTypeJson(req);

            String jsonBody = req.body();
            String eventID = eventManager.addEvent(jsonBody);

            setLocationHeader(res,URL+eventID);
            return eventManager.getEventByid(eventID);
        });

        delete("/events", (req,res) -> {
            res.status(202);
            String deleted = eventManager.deleteByQuerry(req.queryMap());
            return deleted;
        });

        get("/events/:eventid", (req,res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String eventID = "/events/"+req.params(":eventid");
            String eventJson = eventManager.getEventByid(eventID);
            return eventJson;
        });

        /****** EXCEPTION BEHANDLUNG *******/
        exception(SubscriberNotFoundException.class, (ex, req,res) -> {
            res.status(404); // Not Found
            res.body("Subscriber not found");
            ex.printStackTrace();
        });

        exception(QuerryParamsNotFoundException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("QuerryParameter required");
            ex.printStackTrace();
        });

        exception(EventNotFoundException.class, (ex, req, res) -> {
            res.status(404); // Not Found
            res.body("Event not found");
            ex.printStackTrace();
        });

        exception(WrongFormatException.class, (ex, req,res) -> {
            res.status(400);// bad request
            res.body("Wrong Format");
            ex.printStackTrace();
        });

        exception(WrongContentTypeException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Wrong Content-Type");
            ex.printStackTrace();
        });

        exception(RequiredJsonParamsNotFoundException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Required Json-Params not found");
            ex.printStackTrace();
        });

    }

    private static void checkContentTypeJson(Request req) throws WrongContentTypeException {
        if(!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
    }

    private static void setResponseContentTypeJson(Response res){
        res.header("Content-Type","application/json");
    }

    private static void setLocationHeader(Response res, String URL){
        res.header("Location",URL);
    }

}
