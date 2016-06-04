package Boards.BoardManagerComponent.DTOs;

import java.util.Collection;

/**
 * Created by dima on 22.04.16.
 */
public class FieldExpandedDTO {

    private PlaceDTO place;
    private Collection<String> pawns;

    public FieldExpandedDTO(PlaceDTO place, Collection<String> pawns) {
        this.place = place;
        this.pawns = pawns;
    }

    public PlaceDTO getPlace() {
        return place;
    }

    public Collection<String> getPawns() {
        return pawns;
    }
}
