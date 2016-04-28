package YellowPage;

import java.util.Objects;

/**
 * Created by dima on 28.04.16.
 */
public class YellowPageDTO {

    private String _uri;
    private String name;
    private String description;
    private String service;
    private String uri;

    public YellowPageDTO(String _uri, String name, String description, String service, String uri) {
        this._uri = _uri;
        this.name = name;
        this.description = description;
        this.service = service;
        this.uri = uri;
    }

    public String get_uri() {
        return _uri;
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

    public String getUri() {
        return uri;
    }

    public void set_uri(String _uri) {
        this._uri = _uri;
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

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YellowPageDTO that = (YellowPageDTO) o;
        return Objects.equals(get_uri(), that.get_uri()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getService(), that.getService()) &&
                Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_uri(), getName(), getDescription(), getService(), getUri());
    }
}
