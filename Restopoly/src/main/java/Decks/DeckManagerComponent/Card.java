package Decks.DeckManagerComponent;

import Decks.DeckManagerComponent.DTOs.CardDTO;

/**
 * Created by dima on 13.05.16.
 */
public class Card {

    private String name;
    private String text;

    public Card(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public CardDTO toDTO(){
        return new CardDTO(name,text);
    }
}
