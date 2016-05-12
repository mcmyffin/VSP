package Events.EventManagerComponent.DTO;

/**
 * Created by dima on 11.05.16.
 */
public class SubscriberDTO {

    private String id;
    private String game;
    private String uri;

    public SubscriberDTO(String id, String game, String uri) {
        this.id = id;
        this.game = game;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
