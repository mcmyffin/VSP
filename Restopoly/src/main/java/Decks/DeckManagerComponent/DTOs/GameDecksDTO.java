package Decks.DeckManagerComponent.DTOs;

/**
 * Created by dima on 13.05.16.
 */
public class GameDecksDTO {

    private String id;
    private String community;
    private String chance;

    public GameDecksDTO(String id, String community, String chance) {
        this.id = id;
        this.community = community;
        this.chance = chance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getChance() {
        return chance;
    }

    public void setChance(String chance) {
        this.chance = chance;
    }
}
