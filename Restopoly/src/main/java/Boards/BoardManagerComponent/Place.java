package Boards.BoardManagerComponent;

import java.util.ArrayList;
import java.util.List;

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

}
