package Games.GameManagerComponent;

import Games.GameManagerComponent.DTO.GameDTO;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 12.04.16.
 */
public class Game {

    private final String id;
    private String name;
    private PlayerManager playerManager;
    private GameStatus status;
    private Services services;
    private Components components;

    public static Game fromDTO(GameDTO dto){
        checkNotNull(dto);

        Game g = new Game(
                dto.getId(),
                dto.getName(),
                new PlayerManager(dto.getPlayers()),
                GameStatus.REGISTRATION,
                Services.fromDTO(dto.getServicesDTO()),
                Components.fromDTO(dto.getComponentsDTO())
        );
        return g;
    }

    public Game(String id, String name, PlayerManager playerManager, GameStatus status, Services services, Components components) {
        checkNotNull(id);
        this.id = id;
        this.name = name;
        this.playerManager = playerManager;
        this.status = status;
        this.services = services;
        this.components = components;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Services getServices() {
        return services;
    }

    public Components getComponents() {
        return components;
    }

    public GameDTO toDTO(){
        return new GameDTO(
                id,
                name,
                playerManager.getId(),
                services.toDTO(),
                components.toDTO()
        );
    }

    public void setServices(Services services) {
        checkNotNull(services);
        this.services = services;
    }

    public void setComponents(Components components) {
        checkNotNull(components);
        this.components = components;
    }
}

enum GameStatus {
    REGISTRATION("registration"),
    RUNNING("running"),
    FINISHED("finished");

    private String val;

    GameStatus(String val) {
        this.val = val;
    }

    @Override
    public String toString(){
        return this.val;
    }
}
