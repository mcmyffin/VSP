package Decks.DeckManagerComponent;

import Decks.DeckManagerComponent.DTOs.CardActionDTO;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 01.06.16.
 */
public class CardAction {

    public static final String ERHALTE_GELD_VON_BANK = "ERHALTE_GELD_VON_BANK";
    public static final String ERHALTE_GELD_VON_ALLEN = "ERHALTE_GELD_VON_ALLEN";
    public static final String BEZAHLE_GELD_AN_BANK = "BEZAHLE_GELD_AN_BANK";
    public static final String GEFAENGNIS = "GEFAENGNIS";
    public static final String BEWEGE_DICH = "BEWEGE_DICH";

    private final String action;
    private final int number;

    CardAction(String action, int number) {
        checkNotNull(action);
        String a = CardAction.valueOf(action);
        this.action = a;
        this.number = number;
    }

    public int getNumber(){
        return number;
    }

    public String getName() {
        return action;
    }

    public static synchronized CardAction createAction(int number, String actionName){
        String a = valueOf(actionName);
        return new CardAction(a,number);
    }

    public static String valueOf(String actionName){
        checkNotNull(actionName);

        if(actionName.equals(ERHALTE_GELD_VON_BANK)) return ERHALTE_GELD_VON_BANK;
        if(actionName.equals(ERHALTE_GELD_VON_ALLEN)) return ERHALTE_GELD_VON_ALLEN;
        if(actionName.equals(BEZAHLE_GELD_AN_BANK)) return BEZAHLE_GELD_AN_BANK;
        if(actionName.equals(GEFAENGNIS)) return GEFAENGNIS;
        if(actionName.equals(BEWEGE_DICH)) return BEWEGE_DICH;
        throw new IllegalArgumentException("Constant name \""+actionName+"\" not found");
    }

    public static CardAction fromDTO(CardActionDTO dto){
        String cardAction = CardAction.valueOf(dto.getName());
        int number = dto.getNumber();
        return new CardAction(cardAction,number);
    }

    public CardActionDTO toDTO(){
        return new CardActionDTO(
                getName(),
                getNumber()
        );
    }

    @Override
    public String toString() {
        return "CardAction{" +
                "name="+this.action+
                ", number=" + number +
                '}';
    }
}

