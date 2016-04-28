package Boards.BoardManagerComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dima on 21.04.16.
 */
public class Board {

    private final String id;
    private String game;
    private List<Field> fields;
    private Map<String,Pawn> pawnsMap;      // Map<PawnID,Pawn>
    private Map<String,Place> placesMap;    // Map<PlaceID,Place>

    public Board(String id, String game) {
        this.id = id;
        this.game = game;

        this.fields = new ArrayList();
        this.pawnsMap = new HashMap();
        this.placesMap = new HashMap();
    }

    public String getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Map<String, Pawn> getPawnsMap() {
        return pawnsMap;
    }

    public Map<String, Place> getPlacesMap() {
        return placesMap;
    }
}
