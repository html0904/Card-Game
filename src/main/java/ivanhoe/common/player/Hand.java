package ivanhoe.common.player;

import ivanhoe.common.components.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Yan on 2/6/2016.
 */
public class Hand implements Serializable {

    public static final long serialVersionUID = 4L;

    private ArrayList<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public Hand(Hand hand) {
        cards = new ArrayList<>();
        if (hand.getSize() == 0) return;
        cards.addAll(hand.cards.stream().map(Card::new).collect(Collectors.toList()));
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void add(Card card) {
        cards.add(card);
    }

    public int getSize() {
        return cards.size();
    }

    public Card getCard(int i){
        return cards.get(i);
    }

    public Card pickRandom() {
        if (cards.size() == 0) return null;
        Random rand = new Random();
        return cards.remove(rand.nextInt(cards.size()));
    }

    public Card removeCard(int id) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getId() == id) {
                cards.remove(i);
                return card;
            }
        }
        return null;
    }

    public boolean contains(int id) {
        for (Card c : cards) {
            if (c.getId() == id) return true;
        }
        return false;
    }
}
