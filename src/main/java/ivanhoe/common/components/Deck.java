package ivanhoe.common.components;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Yan on 2/6/2016.
 */
public class Deck implements Serializable {

    public static final long serialVersionUID = 7L;

    /**
     * using sets for random selection. Should work fine as long as a unique ID is in each card.
     */
    private Random rand;
    private LinkedList<Card> deck;
    private LinkedList<Card> backupDeck;


    public Deck() {
        deck = new LinkedList<>();
        backupDeck = new LinkedList<>();
        rand = new Random();
    }

    public Deck(Deck obj) {
        deck = new LinkedList<>();
        deck.addAll(obj.deck.stream().map(Card::new).collect(Collectors.toList()));
        backupDeck = new LinkedList<>();
        backupDeck.addAll(obj.backupDeck.stream().map(Card::new).collect(Collectors.toList()));
        rand = new Random();
    }
    public void add(List<Card> cards) {
        backupDeck.addAll(cards.stream().collect(Collectors.toList()));
    }

    public void add(Card card) {
        backupDeck.add(card);
    }
    /**
     * Not useful, but required for magnificent TDD
     *
     * @return size of the deck
     */
    public int size() {
        return deck.size() + backupDeck.size();
    }

    public Card draw() {
        if (deck.size() == 0) {
            deck = backupDeck;
            backupDeck = new LinkedList<>();
        }
        if (deck.size() == 0) return null;
        int dx = rand.nextInt(deck.size());
        Card c = deck.get(dx);
        deck.remove(dx);
        return c;
    }

    /**
     * Used to verify serialization in tests
     *
     * @param obj
     * @return
     */
    public boolean equals(Deck obj) {
        return size() == obj.size();
    }
    // put methods in here like shuffle, draw, etc

}
