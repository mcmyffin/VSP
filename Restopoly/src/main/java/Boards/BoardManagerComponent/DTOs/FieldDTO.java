package Boards.BoardManagerComponent.DTOs;

import java.util.Collection;

/**
 * Created by dima on 22.04.16.
 */
public class FieldDTO {

    private String place;
    private Collection<String> pawns;

    public FieldDTO(String place, Collection<String> pawns) {
        this.place = place;
        this.pawns = pawns;
    }

    public String getPlace() {
        return place;
    }

    public Collection<String> getPawns() {
        return pawns;
    }
}
