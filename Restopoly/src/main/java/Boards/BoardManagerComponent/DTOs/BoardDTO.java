package Boards.BoardManagerComponent.DTOs;

import java.util.List;

/**
 * Created by dima on 21.04.16.
 */
public class BoardDTO {

    private String          id;
    private List<FieldDTO>  fields;
    private List<Integer>   positions;


    public BoardDTO(String id, List<FieldDTO> fields, List<Integer> positions) {
        this.id = id;
        this.fields = fields;
        this.positions = positions;
    }

    public String getId() {
        return id;
    }

    public List<FieldDTO> getFields() {
        return fields;
    }

    public List<Integer> getPositions() {
        return positions;
    }
}
