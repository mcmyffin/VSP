package Boards.BoardManagerComponent.DTOs;

/**
 * Created by dima on 08.05.16.
 */
public class PlaceDTO {

    private String name;
    private String broker;

    public PlaceDTO(String name, String broker) {
        this.name = name;
        this.broker = broker;
    }

    public String getName() {
        return name;
    }

    public String getBroker() {
        return broker;
    }
}
