package Events.EventManagerComponent.DTO;

import Common.Exceptions.RequiredJsonParamsNotFoundException;
import com.sun.istack.internal.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 06.04.16.
 */
public class EventDTO {

    private String id;
    private String game;
    private String type;
    private String name;
    private String reason;
    private String resource;

    private String player;
    private String time;

    public EventDTO(String game, String type, String name, String reason, String resource, String player, String time) {


        this.game = game;
        this.type = type;
        this.name = name;
        this.reason = reason;
        this.resource = resource;
        this.player = player;
        this.time = time;

    }

    public EventDTO(String id, String game, String type, String name, String reason, String resource, String player, String time) {


        this.id = id;
        this.game = game;
        this.type = type;
        this.name = name;
        this.reason = reason;
        this.resource = resource;
        this.player = player;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    public String getResource() {
        return resource;
    }

    public String getPlayer() {
        return player;
    }

    public String getTime() {
        return time;
    }
    

    public void checkContructorArguments() throws RequiredJsonParamsNotFoundException {
        if(isNull(game) || isNull(type) || isNull(name) || isNull(reason)) throw new RequiredJsonParamsNotFoundException();
    }

    private boolean isNull(Object o){
        return o == null;
    }

    public void setId(String id) {
        checkNotNull(id);
        this.id = id;
    }
}


