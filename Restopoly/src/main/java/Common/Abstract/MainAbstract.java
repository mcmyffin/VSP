package Common.Abstract;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 26.04.16.
 */
public class MainAbstract {


    private int port;
    private String ip;

    private String name;
    private String description;
    private String service;

    private String urlService;

    public MainAbstract(int port, String ip, String name, String description, String service, String urlService) {
        checkNotNull(ip);
        checkNotNull(name);
        checkNotNull(description);
        checkNotNull(service);
        checkNotNull(urlService);

        this.port = port;
        this.ip = ip;
        this.name = name;
        this.description = description;
        this.service = service;
        this.urlService = urlService;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getService() {
        return service;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUrlService() {
        return urlService;
    }
}
