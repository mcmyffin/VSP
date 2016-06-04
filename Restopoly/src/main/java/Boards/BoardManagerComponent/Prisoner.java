package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.Pawn;

/**
 * Created by dima on 04.06.16.
 */
public class Prisoner {

    private final Pawn pawn;
    private int rounds;

    public Prisoner(Pawn pawn, int rounds) {
        this.pawn = pawn;
        this.rounds = rounds;
    }

    public void roundPassed(){
        this.rounds--;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public int getRounds() {
        return rounds;
    }
}
