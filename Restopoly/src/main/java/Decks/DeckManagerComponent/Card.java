package Decks.DeckManagerComponent;

import Decks.DeckManagerComponent.DTOs.CardDTO;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 13.05.16.
 */
public class Card {

    private String name;
    private String text;
    private CardAction action;


    public Card(String name, String text, CardAction action) {
        this.name = name;
        this.text = text;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public CardDTO toDTO(){
        return new CardDTO(getName(),getText(),getCardAction().toDTO());
    }

    public CardAction getCardAction() {
        return action;
    }

    public static Card fromDTO(CardDTO cardDTO) {
        checkNotNull(cardDTO);
        CardAction action = CardAction.fromDTO(cardDTO.getCardAction());
        return new Card(cardDTO.getName(),cardDTO.getText(),action);
    }
}

