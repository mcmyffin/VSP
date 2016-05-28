package YellowPage;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static spark.Spark.get;

/**
 * Created by dima on 23.04.16.
 */
public class YellowPageService {

    public static String yellowPageURL = "http://172.18.0.5:4567/services";
    public static String yelloPageAdress = "http://172.18.0.5:4567";

    public static String clearYellowPage() {

        try{
            Gson gson = new Gson();
            HttpResponse<String> expandedServices = Unirest.get(yellowPageURL+"?expanded").asString();
            YellowPageListExpandedDTO expandedListDTO = gson.fromJson(expandedServices.getBody(),YellowPageListExpandedDTO.class);

            String txt = "<h1>DEAD SERVICES REMOVED</h1>";
            for(YellowPageExpandedDTO o : expandedListDTO.getServices()){

                if(o.getStatus() != null && o.getStatus().equals("dead")){
                    HttpResponse<String> response = Unirest.delete(yelloPageAdress+o.get_uri()).asString();

                    txt += "<p>"+response.getBody()+"</p>";
                }
            }
            return txt;
        }catch (UnirestException e){
            e.printStackTrace();
            return "ERROR";
        }
    }


    /**** REST ****/
    public static void startListening(){
        get("/clear", (req,res) -> {
            String txt = YellowPageService.clearYellowPage();
            return txt;
        });
    }

    public static List<YellowPageDTO> getServicesByGroupName(String groupName) throws UnirestException {
        checkNotNull(groupName);

        List<YellowPageDTO> serviceList = new ArrayList();

        HttpResponse<JsonNode> response = Unirest.get(yellowPageURL+"/of/name/"+groupName).asJson();
        if(response.getStatus() == 200){ // 200 = OK
            JSONArray jsonArray = response.getBody().getObject().getJSONArray("services");

            for(Object obj: jsonArray){
                YellowPageDTO dto = getServiceURIByID(obj.toString());
                serviceList.add(dto);
            }
        }

        return serviceList;
    }

    public static YellowPageDTO getServiceURIByID(String yellowPageID) throws UnirestException {
        checkNotNull(yellowPageID);

        System.out.println("YelloPageService.java:65 : \n\t ID\t"+yellowPageID);
        System.out.println("\t Adress\t"+yelloPageAdress);
        System.out.println("\t URI\t"+yelloPageAdress+yellowPageID);

       HttpResponse<String> response = Unirest.get(yelloPageAdress+yellowPageID).asString();
        if(response.getStatus() == 200){ // 200 = OK

            Gson g = new Gson();
            YellowPageDTO dto = g.fromJson(response.getBody(),YellowPageDTO.class);

            return dto;
        }
        System.err.println("service in yellowpage not found\ninvalid yellowpage ID !!!");
        System.err.println("ID = "+yellowPageID);
        System.err.println("URI = "+yelloPageAdress+yellowPageID);
        throw new RuntimeException("service in yellowpage not found\ninvalid yellowpage ID !!!");
    }
}
