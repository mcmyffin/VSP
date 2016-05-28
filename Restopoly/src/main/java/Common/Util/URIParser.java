package Common.Util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by dima on 13.05.16.
 */
public class URIParser {

    @Deprecated
    public static String getHostFromURI(String uri) throws URISyntaxException {
        URI u = URI.create(uri);

        if(u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri,"URI is not absolute");
        String scheme = u.getScheme();
        String host = u.getHost();
        int port = (u.getPort() == -1 ? 80 : u.getPort());

        return scheme+"://"+host+":"+port;
    }

    @Deprecated
    public static String getIDFromURI(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        if(u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri,"URI is not absolute");

        String path = u.getPath();
        int i = path.lastIndexOf("/");

        return path.substring(i,path.length());
    }

    public static URIObject createURIObject(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        if(u.getScheme() == null || u.getHost() == null) throw new URISyntaxException(uri,"URI is not absolute");

        String path = u.getPath();
        int i = path.lastIndexOf("/");

        // id
        String id = path.substring(i,path.length());

        String scheme = u.getScheme();
        String host = u.getHost();
        int port = (u.getPort() == -1 ? 80 : u.getPort());

        // host
        String hostURI = scheme+"://"+host+":"+port;

        return new URIObject(id,host,uri);
    }
}
