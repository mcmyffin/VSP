package Brokers.BrokerManagerComponent;

import Brokers.BrokerManagerComponent.DTO.BrokerDTO;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sasa on 11.05.16.
 */
public class Broker {

    private final String id;
    private String gameID;
    private String gameService;
    private String estates;

    public Broker(String game, String estates) throws URISyntaxException {

        this.estates = estates;
        gameID = getGameIDFromURI(game);
        gameService = getHostFromURI(game);
        id = "/broker" + gameID;
    }

    public static Broker fromDTO(BrokerDTO brokerDTO) throws URISyntaxException {
        checkNotNull(brokerDTO);

        Broker broker = new Broker(brokerDTO.getGame(), brokerDTO.getEstates());
        return broker;
    }


    private String getHostFromURI(String uri) throws URISyntaxException {
        URI u = URI.create(uri);

        if (u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri, "URI is not absolute");
        String scheme = u.getScheme();
        String host = u.getHost();
        int port = (u.getPort() == -1 ? 80 : u.getPort());

        return scheme + "://" + host + ":" + port;
    }

    private String getGameIDFromURI(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        if (u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri, "URI is not absolute");

        String path = u.getPath();
        int i = path.lastIndexOf("/");
        return path.substring(i,path.length());

    }

    public String getId() {
        return id;
    }

    public String getGameService() {
        return gameService;
    }
}
