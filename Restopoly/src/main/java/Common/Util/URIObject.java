package Common.Util;

/**
 * Created by dima on 27.05.16.
 */
public class URIObject {

    private final String id;
    private final String host;
    private final String absoluteURI;

    public URIObject(String id, String host, String absoluteURI) {
        this.id = id;
        this.host = host;
        this.absoluteURI = absoluteURI;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getAbsoluteURI() {
        return absoluteURI;
    }
}
