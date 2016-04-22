package Events.EventManagerComponent;

import Events.EventManagerComponent.DTO.EventDTO;

/**
 * Created by dima on 06.04.16.
 */
public class Event {

    private final String id;
    private String game;
    private String type;
    private String name;
    private String reason;
    private String resource;

    private String player;
    private String time;

    public Event(String id, String game, String type, String name, String reason, String resource, String player, String time) {
        this.id = id;
        this.game = game;
        this.type = type;
        this.name = name;
        this.reason = reason;
        this.resource = resource;
        this.player = player;
        this.time = time;
    }

    /** getter **/
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

    /** setter **/
    public void setGame(String game) {
        this.game = game;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public EventDTO toDTO(){
        EventDTO dto = new EventDTO(
                id,
                game,
                type,
                name,
                reason,
                resource,
                player,
                time
        );
        return dto;
    }

    public static Event fromDTO(EventDTO dto){
        Event e = new Event(
                dto.getId(),
                dto.getGame(),
                dto.getType(),
                dto.getName(),
                dto.getReason(),
                dto.getResource(),
                dto.getPlayer(),
                dto.getTime()
        );

        e.setPlayer(dto.getPlayer());
        e.setResource(dto.getResource());
        e.setTime(dto.getTime());

        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return id.equals(event.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
