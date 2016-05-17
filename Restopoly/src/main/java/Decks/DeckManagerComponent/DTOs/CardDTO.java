package Decks.DeckManagerComponent.DTOs;

/**
 * Created by dima on 13.05.16.
 */
public class CardDTO {

    private String name;
    private String text;

    public CardDTO(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
