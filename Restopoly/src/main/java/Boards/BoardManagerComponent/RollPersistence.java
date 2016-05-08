package Boards.BoardManagerComponent;

import Common.Exceptions.PawnNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 04.05.16.
 */
public class RollPersistence {

    // String = PawnID List<Roll> = a List of rolled numbers
    private final Map<String,Rolls> rollMap;


    public RollPersistence() {
        this.rollMap = new HashMap();
    }

    public void addPawnRoll(String pawnID, int number)  {
        if(!rollMap.containsKey(pawnID)) createRollObject(pawnID,number);

        Rolls rollsObject = rollMap.get(pawnID);
        rollsObject.addRoll(number);
    }

    public Rolls getPawnRolls(String pawnID) throws PawnNotFoundException {
        checkPawnID(pawnID);
        return rollMap.get(pawnID);
    }

    private void createRollObject(String pawnID, int number){
        checkNotNull(pawnID);
        Rolls rollObject = new Rolls(pawnID);
        rollMap.put(pawnID,rollObject);
    }

    private void checkPawnID(String pawnID) throws PawnNotFoundException {
        checkNotNull(pawnID);
        if(!rollMap.containsKey(pawnID)) throw new PawnNotFoundException();
    }
}

class Rolls {

    private final String pawnID;
    private final String ROLL_NAME = "roll";
    private final List<String> rollList;

    public Rolls(String pawnID) {
        this.pawnID = pawnID;
        this.rollList = new ArrayList();
    }

    void addRoll(int number){
        String rollAsString = convertToString(number);
        rollList.add(rollAsString);
    }

    public String getPawnID() {
        return pawnID;
    }

    public List<String> getRollList() {
        return rollList;
    }

    private String convertToString(int number){

        String name = ROLL_NAME+rollList.size()+1;
        return "{\""+name+"\" : {\"number\" : \""+number+"\"}}";
    }
}
