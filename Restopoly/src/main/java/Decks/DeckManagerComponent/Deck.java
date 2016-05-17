package Decks.DeckManagerComponent;

import Common.Exceptions.DeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 13.05.16.
 */
public class Deck {

    private List<Card> unusedCards;
    private List<Card> usedCards;

    public Deck(List<Card> cards) {
        checkNotNull(cards);
        this.unusedCards = cards;
        this.usedCards = new ArrayList();
    }

    Card getNextCard() throws DeckException {
        if(unusedCards.isEmpty() && unusedCards.isEmpty()) throw new DeckException("Deck is empty");
        if(!unusedCards.isEmpty()){
            Card c = unusedCards.remove(0);
            unusedCards.add(c);
            return c;

        }else{
            refill();
            shuffle(unusedCards);
            return getNextCard();
        }
    }



    private void shuffle(List<Card> list){
        Collections.shuffle(list);
    }

    private void refill(){
        unusedCards.addAll(usedCards);
        unusedCards.clear();
    }
}
