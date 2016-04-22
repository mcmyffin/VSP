package Games.GameManagerComponent.DTO;


/**
 * Created by dima on 12.04.16.
 */
public class GameCreateDTO {

    private String id;
    private String name;
    private String players;
    private ServicesDTO services;
    private ComponentsDTO components;


    public GameCreateDTO(String id, String name, String players, ServicesDTO services, ComponentsDTO components) {
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

    public void setServices(ServicesDTO services) {
        this.services = services;
    }

    public void setComponents(ComponentsDTO components) {
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

    public ServicesDTO getServices() {
        if( services == null ) services = new ServicesDTO();
        return services;
    }

    public ComponentsDTO getComponents() {
        if( components == null ) components = new ComponentsDTO();
        return components;
    }

    @Override
    public String toString() {
        return "GameDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", players='" + players + '\'' +
                ", services=" + services.toString() +
                ", components=" + components.toString() +
                '}';
    }
}
