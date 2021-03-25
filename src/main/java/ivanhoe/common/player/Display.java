package ivanhoe.common.player;

import ivanhoe.common.components.Card;
import ivanhoe.utils.Properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yan on 2/6/2016.
 */
public class Display implements Serializable {

    public static final long serialVersionUID = 3L;

    private ArrayList<Card> displayCards;
    private ArrayList<Card> specialDisplay;
    private boolean shield;

    private boolean stunned;


    public Display() {
        specialDisplay = new ArrayList<>(2);
        displayCards = new ArrayList<>();
        shield = false;
        stunned = false;
    }

    public Display(Display obj) {
        specialDisplay = new ArrayList<>(2);
        specialDisplay.addAll(obj.specialDisplay.stream().map(Card::new).collect(Collectors.toList()));
        displayCards = new ArrayList<>();
        displayCards.addAll(obj.displayCards.stream().map(Card::new).collect(Collectors.toList()));
        shield = obj.shield;
        stunned = obj.stunned;
    }

    public Card getLastPlayed() {
        int size = displayCards.size();
        if (size > 0) return displayCards.remove(size - 1);
        return null;
    }


    public void add(Card card) {
        if (card.getAction() == Properties.ACTION.SHIELD) {
            specialDisplay.add(card);
            shield = true;
        } else if (card.getAction() == Properties.ACTION.STUNNED) {
            specialDisplay.add(card);
            stunned = true;
        } else {
            displayCards.add(card);
        }
    }

    public int getValue() {
        int count = 0;
        for (Card c : displayCards) {
            count += c.getValue();
        }
        return count;
    }

    public List<Card> getDisplay() {
        return displayCards;
    }

    public void setDisplay(ArrayList<Card> cardsToKeep) {
        displayCards = cardsToKeep;
    }

    public List<Card> getSpecialDisplay() {
        return specialDisplay;
    }

    public boolean hasShield() {
        return shield;
    }

    public boolean hasStunned() {
        return stunned;
    }

    public Card removeCard(Properties.ACTION action, int value, Properties.COLOR color) {

        for (Card c : specialDisplay) {
            if (c.getAction() == action) {
                if (action == Properties.ACTION.SHIELD) shield = false;
                if (action == Properties.ACTION.STUNNED) stunned = false;
                specialDisplay.remove(c);
                return c;
            }
        }
        for (Card c : displayCards) {
            if (c.getAction() == action && c.getValue() == value && c.getColor() == color) {
                displayCards.remove(c);
                return c;
            }
        }


        return null;
    }

    /**
     * Goes through the cards in the display and checks if there is a maiden supporter.
     *
     * @return true if there is a maiden supporter in the display, false otherwise
     */
    public boolean hasMaiden(){
        for (int i=0; i<displayCards.size(); i++){
            if(displayCards.get(i).getColor() == Properties.COLOR.WHITE && displayCards.get(i).getValue() == 6)
                return true;
        }
        return false;
    }

    /**
     * Removes card from display and returns it. returns null if not found
     *
     * @param id card id
     * @return card removed from display
     */
    public Card removeCard(int id) {
        for (int i = 0; i < displayCards.size(); i++) {

            Card card = displayCards.get(i);
            if (card.getId() == id) {
                displayCards.remove(i);
                return card;
            }
        }

        for (int i = 0; i < specialDisplay.size(); i++) {
            Card card = specialDisplay.get(i);
            if (card.getId() == id) {
                specialDisplay.remove(i);
                return card;
            }
        }
        return null;
    }

    public int size() {
        return displayCards.size();
    }

    public boolean contains(int id) {
        for (Card c : displayCards) {
            if (c.getId() == id) return true;
        }
        return false;
    }
}
