package Games.GameManagerComponent;

import Games.GameManagerComponent.DTO.ComponentsDTO;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 12.04.16.
 */
public class Components {

    private final String id;
    private String game;
    private String dice;
    private String board;
    private String bank;
    private String broker;
    private String decks;
    private String events;

    public static Components fromDTO(ComponentsDTO componentsDTO) {
        checkNotNull(componentsDTO);

        Components c = new Components(
                componentsDTO.getId(),
                componentsDTO.getGame(),
                componentsDTO.getDice(),
                componentsDTO.getBoard(),
                componentsDTO.getBank(),
                componentsDTO.getBroker(),
                componentsDTO.getDecks(),
                componentsDTO.getEvents()
        );
        return c;
    }

    public Components(String id, String game, String dice, String board, String bank, String broker, String decks, String events) {
        checkNotNull(id);
        this.id = id;
        this.game = game;
        this.dice = dice;
        this.board = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
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

    public ComponentsDTO toDTO(){
        return new ComponentsDTO(
                game,
                dice,
                board,
                bank,
                broker,
                decks,
                events
        );
    }

}
