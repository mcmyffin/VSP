package Decks.DeckManagerComponent.DTOs;

/**
 * Created by dima on 01.06.16.
 */
public class CardActionDTO {

    private String name;
    private int number;

    public CardActionDTO(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
