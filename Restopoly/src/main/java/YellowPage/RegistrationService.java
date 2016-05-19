package YellowPage; /**
 * Created by dima on 12.04.16.
 */

import Common.Abstract.MainAbstract;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static spark.Spark.get;

public class RegistrationService {

    private final String yellowPageURL =  YellowPageService.yellowPageURL;
    private final MainAbstract main;
    private YellowPageDTO responseRegistrationDTO;


    public RegistrationService(MainAbstract main) {
        this.main = main;
    }

    private void sendRegistration(YellowPageDTO dto) throws UnirestException {
        Gson gson = new Gson();
        String json = gson.toJson(dto);

        HttpResponse response = Unirest.post(yellowPageURL)
                .header("Content-Type","application/json")
                .body(json).asString();

        if(response.getStatus() == 201) {

            System.out.println("registration success");
            responseRegistrationDTO = gson.fromJson(response.getBody().toString(),YellowPageDTO.class);

        } else {
            System.out.println("registration failed");
        }
    }

    private void buildRegisteration(String ownURL, String name, String description, String serviceName){
        checkNotNull(ownURL);
        checkNotNull(name);
        checkNotNull(description);
        checkNotNull(serviceName);

        YellowPageDTO dto = new YellowPageDTO(
                null,
                name,
                description,
                serviceName,
                ownURL
                );

        try {
            sendRegistration(dto);
        } catch (UnirestException e) {
            System.out.println("doesn't work");
            e.printStackTrace();
        }
    }

    public void startRegistration(){
        removeOldRegistration();
        buildRegisteration(
                main.getUrlService(),
                main.getName(),
                main.getDescription(),
                main.getService()
        );
    }

    public YellowPageDTO getResponseRegistration(){
        return responseRegistrationDTO;
    }

    public static String sendPost(String uri, String jsonBody) throws UnirestException {
        checkNotNull(uri);
        checkNotNull(jsonBody);

        HttpResponse<String> response = Unirest.post(uri).header("Content-Type","application/json").body(jsonBody).asString();

        if(response.getStatus() != 404 || response.getStatus() != 400){

            String locationHeader = response.getHeaders().getFirst("Location");

            System.out.println(">> send POST TO: "+uri);
            System.out.println(">> body: "+jsonBody);
            System.out.println(">> Location : "+locationHeader);

            return locationHeader;

        } else throw new UnirestException("Response Statuscode Problem : "+response.getStatus());
    }

    private static void sendDelete(String uri){
        checkNotNull(uri);
        try {
            Unirest.delete(uri).asString();
        } catch (UnirestException e) {}
    }

    private void removeOldRegistration() {
        try{
            List<YellowPageDTO> groupList = YellowPageService.getServicesByGroupName(main.getName());

            for(YellowPageDTO dto : groupList){
                if(dto.getService().equals(main.getService())) sendDelete(YellowPageService.yelloPageAdress+dto.get_uri());
            }

        }catch (UnirestException e){
            System.out.println("Yellopage-Service is not reachable");
        }
    }
}
