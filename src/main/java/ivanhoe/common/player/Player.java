package ivanhoe.common.player;

import ivanhoe.common.components.Card;
import ivanhoe.common.components.Token;
import ivanhoe.utils.Properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Yan on 2/6/2016.
 */
public class Player implements Serializable {

    public static final long serialVersionUID = 5L;

    boolean inTournament;
    private int id;
    private String name;
    private Hand hand;
    private Display display;
    private ArrayList<Token> playerTokens;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        hand = new Hand();
        display = new Display();
        playerTokens = new ArrayList<>();
        inTournament = true;
    }

    public Player(Player obj) {
        id = obj.id;
        name = obj.name;
        hand = new Hand(obj.hand);
        display = new Display(obj.display);
        playerTokens = new ArrayList<>();
        playerTokens.addAll(obj.playerTokens.stream().map(Token::new).collect(Collectors.toList()));
        inTournament = obj.inTournament;
    }

    public boolean isInTournament() {
        return inTournament;
    }

    public void setInTournament(boolean inTournament) {
        this.inTournament = inTournament;
    }

    public void addCardToDisplay(Card card) {
        display.add(card);
    }

    public Card removeCardFromDisplay(int id) {
        return display.removeCard(id);
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public Card removeCardFromHand(int id) {
        return hand.removeCard(id);
    }

    public int getID() {
        return id;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public int getHandSize() {
        return hand.getSize();
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public ArrayList<Token> getPlayerTokens() {
        return playerTokens;
    }

    public void setPlayerTokens(ArrayList<Token> playerTokens) {
        this.playerTokens = playerTokens;
    }

    public void addToken(Token token) {
        if (!playerTokens.stream().map(Token::getTokenColor).collect(Collectors.toCollection(LinkedList::new)).contains(token.getTokenColor()))
            playerTokens.add(token);
    }

    public int getDisplaySize() {
        return display.size();
    }


    public boolean hasStunned() {
        return display.hasStunned();

    }

    public boolean hasShield() {
        return display.hasShield();
    }

    /**
     * Removes token of designated color if such a token exists.
     *
     * @param color
     */
    public void removeToken(Properties.COLOR color) {
        for (int i = 0; i < playerTokens.size(); i++) {
            if (playerTokens.get(i).getTokenColor() == color) {
                playerTokens.remove(i);
                break;
            }
        }
    }
}
