package Boards.BoardManagerComponent;

/**
 * Created by dima on 18.05.16.
 */
public class PawnMove {

    private final Pawn pawn;
    private final Place place;
    private final boolean ueberLos;

    public PawnMove(Pawn pawn, Place place, boolean ueberLos) {
        this.pawn = pawn;
        this.place = place;
        this.ueberLos = ueberLos;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public Place getPlace() {
        return place;
    }

    public boolean isUeberLos() {
        return ueberLos;
    }
}
