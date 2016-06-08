package diceservice.Dice.util;

import java.net.URI;
import java.net.URISyntaxException;

public class URIParser {

    public static String getHostFromURI(String uri) throws URISyntaxException {
		URI u = URI.create(uri);

        if(u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri,"URI is not absolute");
        String scheme = u.getScheme();
        String host = u.getHost();
        int port = (u.getPort() == -1 ? 80 : u.getPort());

        return scheme+"://"+host+":"+port;
    }

    public static String getIDFromURI(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        if(u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri,"URI is not absolute");

        String path = u.getPath();
        int i = path.lastIndexOf("/");

        return path.substring(i,path.length());
    }
}