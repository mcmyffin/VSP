package Decks;

import Common.Abstract.MainAbstract;
import Common.Util.IPFinder;
import Decks.DeckManagerComponent.DeckManager;
import YellowPage.RegistrationService;

import static spark.Spark.port;

/**
 * Created by dima on 13.05.16.
 */
public class Main extends MainAbstract{

    public static int    port = 4567;
    public static String ip   = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Deck Manager";
    public static String service = "decks";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/decks";

    public Main(){
        super(port,ip,name,description,service,URLService);
    }

    public static void main(String[] args) {

        port(port);
        Main main = new Main();

        DeckManager deckManager = new DeckManager();
        RegistrationService registrationService = new RegistrationService(main);


        registrationService.startRegistration();

        // TODO REST-Resources implementieren


    }
}
