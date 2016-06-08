package YellowPage;

import java.util.Objects;

/**
 * Created by dima on 28.04.16.
 */
public class YellowPageExpandedDTO {

    private String _uri;
    private String description;
    private String name;
    private String service;
    private String status;
    private String uri;


    public YellowPageExpandedDTO(String _uri, String description, String name, String service, String status, String uri) {
        this._uri = _uri;
        this.description = description;
        this.name = name;
        this.service = service;
        this.status = status;
        this.uri = uri;
    }

    public String get_uri() {
        return _uri;
    }

    public void set_uri(String _uri) {
        this._uri = _uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
