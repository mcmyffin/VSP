package Events; /**
 * Created by dima on 12.04.16.
 */

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import static com.google.common.base.Preconditions.checkNotNull;
import static spark.Spark.*;

public class RegistrationService {

    private final String yellowPageURL;
    private final String name = "event";
    private final String description = "Manage events";
    private final String serviceName = "Events Manager Services";

    private boolean isSignedIn = false;


    public RegistrationService(String yellowPageURL){
        checkNotNull(yellowPageURL);
        this.yellowPageURL = yellowPageURL;
    }

    private String getRegistrationJSON(String name, String description, String serviceName, String ownURL){
        String json ="{\n" +
                        "name: "+name+",\n" +
                        "description: "+description+",\n" +
                        "service: "+serviceName+",\n" +
                        "uri: "+ownURL+"/event"+"\n" +
                    "}";
        return json;
    }


    public void signIn(){
        get("/registerservice",(req,res) -> {
            if(!isSignedIn){
                String uri = req.queryParams("uri");

                HttpResponse response = new Unirest().post(yellowPageURL)
                        .header("Content-Type","application/json")
                        .body(getRegistrationJSON(name,description,serviceName,uri))
                        .asJson();


                if(response.getStatus() == 201){
                    res.status(200);
                    isSignedIn = true;
                    return "OK you are signed in";
                }else{
                    res.status(409);
                    isSignedIn = false;
                    return "not work";
                }

            }else {
                return "You are already signed in :)";
            }
        });
    }

//    public void signOut(){
//        delete("/registerservice/:nr",(req,res) -> {
//            if(isSignedIn){
//                res.status(200);
//
//                res.body(getRegistrationJSON(name,description,serviceName,uri));
//            }
//
//        });
//    }
}
