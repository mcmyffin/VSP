package Boards.BoardManagerComponent;

/**
 * Created by dima on 21.04.16.
 */
public class Pawn {

    private final String id;
    private String player;
    private String position;
    private String move;

    public Pawn(String id, String player, String position, String move) {
        this.id = id;
        this.player = player;;
        this.position = position;
        this.move = move;
    }

    public String getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public String getPosition() {
        return position;
    }

    public String getMove() {
        return move;
    }
}
