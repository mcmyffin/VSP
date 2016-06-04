package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.DTOs.PawnDTO;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 21.04.16.
 */
public class Pawn {

    private final String id;
    private String player;
    private String places;
    private int position;
    private String roll;
    private String move;

    public Pawn(String id, String player, String places, int position, String roll, String move) {
        this.id = id;
        this.player = player;
        this.places = places;
        this.position = position;
        this.roll = roll;
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

    public void setPlaces(String places) {
        this.places = places;
    }

    public static Pawn fromDTO(PawnDTO pawnDTO){
        checkNotNull(pawnDTO);

        return new Pawn(
                pawnDTO.getId(),
                pawnDTO.getPlayer(),
                pawnDTO.getPlaces(),
                pawnDTO.getPosition(),
                pawnDTO.getRoll(),
                pawnDTO.getMove()
        );
    }

    public PawnDTO toDTO(){
        return new PawnDTO(
                this.id,
                this.player,
                this.getPlaces(),
                this.position,
                this.roll,
                this.move
        );
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
