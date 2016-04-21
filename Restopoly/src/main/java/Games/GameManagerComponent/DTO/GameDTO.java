package Games.GameManagerComponent.DTO;

/**
 * Created by dima on 12.04.16.
 */
public class GameDTO {

    private String id;
    private String name;
    private String players;
    private ServicesDTO servicesDTO;
    private ComponentsDTO componentsDTO;

    public GameDTO(String name, String players, ServicesDTO ServicesDTO, ComponentsDTO ComponentsDTO){
        this(null,name,players,ServicesDTO,ComponentsDTO);
    }

    public GameDTO(String id, String name, String players, ServicesDTO servicesDTO, ComponentsDTO componentsDTO) {
        this.id = id;
        this.name = name;
        this.players = players;
        this.servicesDTO = (servicesDTO == null ? new ServicesDTO(): servicesDTO);
        this.componentsDTO = (componentsDTO == null ? new ComponentsDTO(): componentsDTO);
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

    public void setServicesDTO(ServicesDTO servicesDTO) {
        this.servicesDTO = servicesDTO;
    }

    public void setComponentsDTO(ComponentsDTO componentsDTO) {
        this.componentsDTO = componentsDTO;
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

    public ServicesDTO getServicesDTO() {
        return servicesDTO;
    }

    public ComponentsDTO getComponentsDTO() {
        return componentsDTO;
    }
}
