package Boards.BoardManagerComponent.DTOs;

import java.util.List;

/**
 * Created by dima on 21.04.16.
 */
public class BoardExpandedDTO {

    private String          id;
    private List<FieldExpandedDTO>  fields;
    private List<Integer>   positions;


    public BoardExpandedDTO(String id, List<FieldExpandedDTO> fields, List<Integer> positions) {
        this.id = id;
        this.fields = fields;
        this.positions = positions;
    }

    public String getId() {
        return id;
    }

    public List<FieldExpandedDTO> getFields() {
        return fields;
    }

    public List<Integer> getPositions() {
        return positions;
    }
}
