package Boards.BoardManagerComponent;

import Boards.BoardManagerComponent.DTOs.PlaceDTO;
import Games.GameManagerComponent.DTO.PlayerDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 21.04.16.
 */
public class Place {

    private final String id;
    private String name;
    private String broker;

    public Place(String id, String name, String broker) {
        checkNotNull(id);
        this.id = id;
        this.name = name;
        this.broker = broker;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBroker() {
        return broker;
    }

    public PlaceDTO toDTO(){
        return new PlaceDTO(
                name,
                broker
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(getId(), place.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
