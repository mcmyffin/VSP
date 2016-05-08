package Boards.BoardManagerComponent.DTOs;

/**
 * Created by dima on 02.05.16.
 */
public class PawnDTO {

    private String id;
    private String player;
    private String places;
    private int position;
    private String roll;
    private String move;

    public PawnDTO(String id, String player, String places, int position, String roll, String move) {
        this.id = id;
        this.player = player;
        this.places = places;
        this.position = position;
        this.roll = roll;
        this.move = move;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public String getPlaces() {
        return places;
    }

    public int getPosition() {
        return position;
    }

    public String getRoll() {
        return roll;
    }

    public String getMove() {
        return move;
    }
}
