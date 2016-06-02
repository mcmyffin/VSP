package Decks.DeckManagerComponent.DTOs;

/**
 * Created by dima on 13.05.16.
 */
public class CardDTO {

    private String name;
    private String text;
    private CardActionDTO cardaction;

    public CardDTO(String name, String text, CardActionDTO cardaction) {
        this.name = name;
        this.text = text;
        this.cardaction = cardaction;
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

    public CardActionDTO getCardAction() {
        return cardaction;
    }

    public void setCardaction(CardActionDTO cardaction) {
        this.cardaction = cardaction;
    }
}
