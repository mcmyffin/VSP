package Decks.DeckManagerComponent;

import Decks.DeckManagerComponent.DTOs.CardActionDTO;

/**
 * Created by dima on 01.06.16.
 */
public enum CardAction {

    ERHALTE_GELD_VON_BANK,
    ERHALTE_GELD_VON_ALLEN,
    BEZAHLE_GELD_AN_BANK,
    GEFAENGNIS,
    BEWEGE_DICH;

    private int number;

    public void setNumber(int number){
        this.number = number;
    }

    public int getNumber(){
        return number;
    }

    public static CardAction createAction(int number, CardAction action){
        action.setNumber(number);
        return action;
    }

    public static CardAction fromDTO(CardActionDTO dto){
        CardAction cardAction = CardAction.valueOf(dto.getName());
        return CardAction.createAction(dto.getNumber(),cardAction);
    }

    public CardActionDTO toDTO(){
        return new CardActionDTO(
                this.toString(),
                getNumber()
        );
    }

    @Override
    public String toString() {
        return name();
    }
}

