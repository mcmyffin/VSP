package Games.GameManagerComponent.DTO;


/**
 * Created by dima on 12.04.16.
 */
public class GameDTO {

    private String id;
    private String name;
    private String players;
    private String services;
    private String components;

    public GameDTO(String id, String name, String players, String services, String components) {
        this.id = id;
        this.name = name;
        this.players = players;
        this.services = services;
        this.components = components;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlayers() {
        return players;
    }

    public String getServices() {
        return services;
    }

    public String getComponents() {
        return components;
    }


    @Override
    public String toString() {
        return "GameDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", players='" + players + '\'' +
                ", services=" + services +
                ", components=" + components +
                '}';
    }
}
