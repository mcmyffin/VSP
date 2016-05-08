package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.DTOs.FieldDTO;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 21.04.16.
 */
public class Field {

    private Place place;
    private Map<String,Pawn> pawns; // String = pawnID

    public Field(Place place) {
        checkNotNull(place);
        this.place = place;
        this.pawns = new HashMap();
    }

    public Place getPlace() {
        return place;
    }
    public Collection<Pawn> getPawns() {
        return pawns.values();
    }

    void addPawn(Pawn pawn){
        checkNotNull(pawn);

        pawns.put(pawn.getId(),pawn);
    }

    boolean isPawnAtField(String pawnID){
        checkNotNull(pawnID);
        return pawns.containsKey(pawnID);
    }

    void removePawn(String pawnID){
        pawns.remove(pawnID);
    }

    public FieldDTO toDTO(){

        return new FieldDTO(
                place.getId(),
                pawns.keySet()
        );
    }
}
