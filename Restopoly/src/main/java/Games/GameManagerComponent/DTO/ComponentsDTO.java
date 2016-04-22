package Games.GameManagerComponent.DTO;

import Games.GameManagerComponent.Components;
import com.sun.istack.internal.Nullable;

/**
 * Created by dima on 12.04.16.
 */
public class ComponentsDTO {

    private String id;
    private String game;
    private String dice;
    private String board;
    private String bank;
    private String broker;
    private String decks;
    private String events;


    public ComponentsDTO(String id, String game, String dice, String board, String bank, String broker, String decks, String events) {
        this.game = game;
        this.dice = dice;
        this.board = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
        this.events = events;
    }

    public ComponentsDTO(){
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setDice(String dice) {
        this.dice = dice;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public void setDecks(String decks) {
        this.decks = decks;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public String getDice() {
        return dice;
    }

    public String getBoard() {
        return board;
    }

    public String getBank() {
        return bank;
    }

    public String getBroker() {
        return broker;
    }

    public String getDecks() {
        return decks;
    }

    public String getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "ComponentsDTO{" +
                "id='" + id + '\'' +
                ", game='" + game + '\'' +
                ", dice='" + dice + '\'' +
                ", board='" + board + '\'' +
                ", bank='" + bank + '\'' +
                ", broker='" + broker + '\'' +
                ", decks='" + decks + '\'' +
                ", events='" + events + '\'' +
                '}';
    }
}
