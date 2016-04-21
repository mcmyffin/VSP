package Games.GameManagerComponent;

import Games.GameManagerComponent.DTO.ServicesDTO;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 12.04.16.
 */
public class Services {

    private final String id;
    private String game;
    private String dice;
    private String board;
    private String bank;
    private String broker;
    private String decks;
    private String events;

    public static Services fromDTO(ServicesDTO servicesDTO) {
        checkNotNull(servicesDTO);

        Services s = new Services(
                servicesDTO.getId(),
                servicesDTO.getGame(),
                servicesDTO.getDice(),
                servicesDTO.getBoard(),
                servicesDTO.getBank(),
                servicesDTO.getBroker(),
                servicesDTO.getDecks(),
                servicesDTO.getEvents()
        );
        return s;
    }

    public Services(String id, String game, String dice, String board, String bank, String broker, String decks, String events) {
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


    public ServicesDTO toDTO(){
        return new ServicesDTO(
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