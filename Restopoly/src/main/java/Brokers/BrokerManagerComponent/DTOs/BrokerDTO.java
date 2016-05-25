package Brokers.BrokerManagerComponent.DTOs;

/**
 * Created by sasa on 11.05.16.
 */
public class BrokerDTO {
    private String id;
    private String game;
    private String estates;

    public BrokerDTO(String id, String game, String estates) {
        this.id = id;
        this.game = game;
        this.estates = estates;
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

    public String getEstates() {
        return estates;
    }

    public void setEstates(String estates) {
        this.estates = estates;
    }


}
