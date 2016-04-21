package Games.GameManagerComponent.DTO;

import Games.GameManagerComponent.Components;

/**
 * Created by dima on 12.04.16.
 */
public class ComponentsDTO {

    private  String id;
    private  String game;
    private  String dice;
    private  String board;
    private  String bank;
    private  String broker;
    private  String decks;
    private  String events;

    public ComponentsDTO(){
        this(null,null,null,null,null,null,null);
    }

    public ComponentsDTO(String game, String dice, String board, String bank, String broker, String decks, String events){
        this(null,game,dice,board,bank,broker,decks,events);
    }

    public ComponentsDTO(String id, String game, String dice, String board, String bank, String broker, String decks, String events) {
        this.game = game;
        this.dice = dice;
        this.board = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
        this.events = events;
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
}
