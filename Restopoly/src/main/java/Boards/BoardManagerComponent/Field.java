package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.DTOs.FieldDTO;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 21.04.16.
 */
public class Field {

    private Place place;
    private List<Pawn> pawns;

    public Field(Place place) {
        checkNotNull(place);
        this.place = place;
        this.pawns = new ArrayList();
    }

    public Place getPlace() {
        return place;
    }
    public List<Pawn> getPawns() {
        return pawns;
    }

    void addPawn(Pawn pawn){
        checkNotNull(pawn);
        pawns.add(pawn);
    }

    public FieldDTO toDTO(){
        throw new UnsupportedOperationException();
    }
}
