package Boards.BoardManagerComponent;

import Common.Exceptions.PawnNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 04.06.16.
 */
public class Jail {

    private final Map<String,Prisoner> prisonerMap;
    private final int rounds;

    public Jail() {
        this.prisonerMap = new HashMap();
        this.rounds = 3;
    }

    private Prisoner getPrisonerById(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);
        if(!prisonerMap.containsKey(pawnID)) throw new PawnNotFoundException("Pawn not in Jail");
        return prisonerMap.get(pawnID);
    }

    public void releasePrisoner(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);

        Prisoner prisoner = getPrisonerById(pawnID);
        prisonerMap.remove(pawnID);
    }

    public boolean addPawnToJail(Pawn pawn){
        checkNotNull(pawn);

        if(prisonerMap.containsKey(pawn.getId())) return false;
        prisonerMap.put(pawn.getId(),new Prisoner(pawn,rounds));
        return true;
    }

    public int getRoundsFromPrisoner(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);

        Prisoner prisoner = getPrisonerById(pawnID);
        return prisoner.getRounds();
    }

    public void roundPassedFromPrisoner(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);

        Prisoner prisoner = getPrisonerById(pawnID);
        prisoner.roundPassed();
    }

    public boolean isPawnInJail(String pawnID) {
        checkNotNull(pawnID);
        return prisonerMap.containsKey(pawnID);
    }
}
