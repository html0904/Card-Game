package ivanhoe.common;

import ivanhoe.common.components.Card;
import ivanhoe.common.components.Deck;
import ivanhoe.common.player.Display;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.utils.Properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Doubles as both a tournament and game board. Most of the game actions are performed within this class.
 */
public class Tournament implements Serializable {

    public static final long serialVersionUID = 9L;

    private Deck deck;
    private List<Player> players;
    private int currentPlayerID;
    private Properties.COLOR color;
    private int ivanhoeOwnerID;
    private boolean ivanhoeInPlay;
    private Card lastActionCardPlayed;
    private TournamentBackup backup;
    private boolean ivanhoeSent;
    private GameState ifNoIvanhoe;
    private Properties.ACTION currentAction;
    private Card outwitPlaceholder;
    private int adaptCurrentID;
    private Card lastCardPlayed;
    private List<Integer> maidenHolders;
    /**
     * Tournament holds the game board and reflects actions taken by players.
     *
     * @param players        list of all players in the game session
     * @param deck
     * @param color
     * @param startingPlayer
     */
    public Tournament(List<Player> players, Deck deck, Properties.COLOR color, int startingPlayer) {
        maidenHolders = new ArrayList<>();
        lastCardPlayed=null;
        outwitPlaceholder = null;
        ivanhoeSent = false;
        ifNoIvanhoe = null;
        this.color = color;
        this.deck = deck;
        this.players = players;
        currentAction = Properties.ACTION.NONE;
        checkForIvanhoe();
        //marks all players as in the tournament.
        for (Player p : players) {
            p.setInTournament(true);
        }
        currentPlayerID = startingPlayer;

        //gives card to player starting a tournament
        players.get(currentPlayerID).addCardToHand(deck.draw());
        this.lastActionCardPlayed = null;
        makeBackup();
    }

    public void setLastCardPlayed(Card lastCardPlayed){
        this.lastCardPlayed = lastCardPlayed;
    }

    /**
     * Returns the id of the player whose turn it is.
     */
    public int getCurrentPlayerID() {
        return currentPlayerID;
    }

    /**
     * performs specified actions. all actions are assumed to be valid since these are checked in the client code.
     *
     * @param action object containing details about action to be performed.
     * @return current state of the gameboard
     */
    public GameState performAction(PlayerAction action) {

        //this should only happen at the start of a tournament
        if (action == null) {
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
        }

        //preempts he whole thing if ivanhoe was declined by player.
        if (ivanhoeSent && action.getActionTaken() != Properties.GAME_ACTION.PLAYED_IVANHOE) {
            ivanhoeSent = false;
            return ifNoIvanhoe;
        }

        //get players card and set last action card played
        Player player = players.get(action.getPlayerID());
        Card card = players.get(player.getID()).removeCardFromHand(action.getCardPlayed());
        if (card != null && card.getAction() != Properties.ACTION.NONE) lastActionCardPlayed = card;

        //sort by action taken
        switch (action.getActionTaken()) {
            case SELECT_TOKEN_TO_RETURN:
                return handleSelectTokenToReturn(action);
            case SELECT_OPPONENT_HAND:
                return handleSelectOpponentHand(action);
            case SELECT_OPPONENT_DISPLAY:
                return handleSelectOpponentDisplay(action);
            case SELECT_OPPONENT_DISPLAY_CARD:
                return handleSelectOpponentDisplayCard(action);
            case SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT:
                return handleSelectOpponentDisplayCardForOutwit(action);
            case SELECT_DISPLAY_CARD:
                return handleSelectDisplayCard(action);
            case SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT:
                return handleSelectDisplayCardToKeepForAdapt(action);
            case SELECT_HAND_CARD:
                return handleActionOrIvanhoe(action, card);
            case PLAYED_IVANHOE:
                return handleActionOrIvanhoe(action, card);
            case CONTINUE:
                return handleContinue();
            case WITHDRAW:
                return handleWithdraw(action);
            case SELECT_COLOR:
                return handlePickColor(action);
            case SELECT_COLOR_OTHER_THAN_PURPLE:
                return handlePickColor(action);
            case SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN:
                return handlePickColor(action);
            default:
                //should never happen
                return null;
        }
    }

    private GameState handleSelectTokenToReturn(PlayerAction action) {
        players.get(action.getPlayerID()).removeToken(action.getColor());
        int dx = -1;

        for (int i = 0; i < maidenHolders.size(); i++) {
            if (maidenHolders.get(i) == action.getPlayerID()) {
                dx = i;
                break;
            }
        }

        if (dx >= 0) maidenHolders.remove(dx);

        if (maidenHolders.size() > 0) {
            int i = maidenHolders.remove(maidenHolders.size() - 1);
            return new GameState(players, Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN, i, color, lastActionCardPlayed);
        } else {
            cleanDisplays();
            makeBackup();
            return new GameState(players, Properties.GAME_ACTION.WIN, currentPlayerID, color, lastActionCardPlayed);

        }
    }

    /**
     * Makes appropriate changes to the game based on action card received.
     *
     * @param action player action taken
     * @param card   card played
     * @return new gamestate after modification
     */
    private GameState handleActionOrIvanhoe(PlayerAction action, Card card) {
        if (card.getAction() != Properties.ACTION.NONE && card.getAction() != Properties.ACTION.IVANHOE) {
            //keep this card as a reference
            lastActionCardPlayed = card;
            makeBackup();
        }
        GameState gs = null;

        //handle card based on card action
        switch (card.getAction()) {
            case NONE:
                gs = handleColorCard(action, card);
                break;
            case UNHORSE:
                gs = handleUnhorse(card);
                break;
            case CHANGE_WEAPON:
                gs = handleChangeWeapon(card);
                break;
            case DROP_WEAPON:
                gs = handleDropWeapon(card);
                break;
            case BREAK_LANCE:
                gs = handleBreakLance(action, card);
                break;
            case RIPOSTE:
                gs = handleRiposte(card);
                break;
            case DODGE:
                gs = handleDodge(card);
                break;
            case RETREAT:
                gs = handleRetreat(action, card);
                break;
            case KNOCKDOWN:
                gs = handleKnockDown(action, card);
                break;
            case OUTMANEUVER:
                gs = handleOutmaneuver(action, card);
                break;
            case CHARGE:
                gs = handleCharge(card);
                break;
            case COUNTERCHARGE:
                gs = handleCounterCharge(card);
                break;
            case DISGRACE:
                gs = handleDisgrace(card);
                break;
            case ADAPT:
                gs = handleAdapt(card);
                break;
            case OUTWIT:
                gs = handleOutwit(card);
                break;
            case SHIELD:
                gs = handleShield(card);
                break;
            case STUNNED:
                gs = handleStunned(card);
                break;
            case IVANHOE:
                return handleIvanhoe(action, card);
            default:
                break;
        }

        //now we check for ivanhoe, if present we send an ivanhoe request to the player holding ivanhoe.
        if (ivanhoeInPlay && ivanhoeOwnerID != currentPlayerID && lastCardPlayed != null && lastCardPlayed.getAction() != Properties.ACTION.NONE) {
            //game state to return if ivanhoe is not played
            ifNoIvanhoe = gs;
            ivanhoeSent = true;

            //Ivanhoe request
            return new GameState(players, Properties.GAME_ACTION.SELECT_IVANHOE, ivanhoeOwnerID, color, lastActionCardPlayed);
        }
        return gs;
    }
    /**
     * Called when the player that is supposed to start the tournament cannot play. Not ready for the wild.
     *
     * @return gamestate with new player
     */
    private GameState nextPlayerThatCanStart() {

        for (int i = currentPlayerID + 1; i < players.size(); i++) {
            if (playerCanPlay(i)) {
                currentPlayerID = i;
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            }
        }

        for (int i = 0; i < currentPlayerID; i++) {
            if (playerCanPlay(i)) {
                currentPlayerID = i;
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            }
        }

        //can only get here if no players can play
        return new GameState(players, Properties.GAME_ACTION.WIN, currentPlayerID, color, lastActionCardPlayed);

    }

    /**
     * Checks if a player can start a tournament. i.e. They have at least one number card of tournament color or white.
     *
     * @param currentPlayerID Id of the player to analyse
     * @return whether the player is eligible to start a tournament
     */
    private boolean playerCanPlay(int currentPlayerID) {
        Player p = players.get(currentPlayerID);
        if (!p.isInTournament()) return false;
        for (Card c : p.getHand().getCards()) {
            //if the player has at least one playable card that is not an action card then return true
            if (c.getAction() == Properties.ACTION.NONE && (c.getColor() == Properties.COLOR.WHITE || c.getColor() == color))
                return true;
        }
        return false;
    }

    /**
     * Removes all cards of same value as selected card from player's display. if there are multiples of other values it
     * returns a request to that player, if not it returns a request to the next player. If all players have adapted return
     * a request to play next ard to the original player.
     * @param action player action
     * @return gamestate
     */
    private GameState handleSelectDisplayCardToKeepForAdapt(PlayerAction action) {
        Card cardToKeep = players.get(action.getPlayerID()).removeCardFromDisplay(action.getTargetCardID());
        List<Integer> cardsToRemove = players.get(action.getPlayerID()).getDisplay().getDisplay().stream().filter(c -> c.getValue() == cardToKeep.getValue()).map((Function<Card, Integer>) Card::getId).collect(Collectors.toList());
        for (Integer i : cardsToRemove) {
            deck.add(players.get(action.getPlayerID()).removeCardFromDisplay(i));
        }
        players.get(action.getPlayerID()).addCardToDisplay(cardToKeep);
        if (allPlayersHaveAdapted()) {
            currentAction = Properties.ACTION.NONE;
            return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
        }
        if (playerHasAdapted(adaptCurrentID)) adaptCurrentID = getNextUnadaptedPlayer(adaptCurrentID);
        return new GameState(players, Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT, adaptCurrentID, color, lastActionCardPlayed);
    }


    /**
     * Handles cases where an opponents display needs to be selected. in this case only knockdown is present.
     * @param action player action received
     * @return gamestate
     */
    private GameState handleSelectOpponentHand(PlayerAction action) {
        switch (currentAction) {
            case KNOCKDOWN:
                players.get(action.getPlayerID()).addCardToHand(players.get(action.getTargetPlayer()).getHand().pickRandom());
                currentAction = Properties.ACTION.NONE;
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            default:
                break;
        }
        return null;
    }

    /**
     * Handles cases where a display card needs to be selected.
     * @param action player action
     * @return gamestate
     */
    private GameState handleSelectDisplayCard(PlayerAction action) {
        switch (currentAction) {
            case RETREAT:
                players.get(action.getPlayerID()).addCardToHand(players.get(action.getPlayerID()).removeCardFromDisplay(action.getTargetCardID()));
                currentAction = Properties.ACTION.NONE;
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            case OUTWIT:
                outwitPlaceholder = players.get(action.getPlayerID()).removeCardFromDisplay(action.getTargetCardID());
                return new GameState(players, Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT, currentPlayerID, color, lastActionCardPlayed);
            default:
                break;
        }
        return null;
    }

    /**
     * Handles cases where an opponents display card needs to be selected.
     * @param action player action
     * @return gamestate
     */
    private GameState handleSelectOpponentDisplayCard(PlayerAction action) {
        switch (currentAction) {
            case DODGE:
                currentAction = Properties.ACTION.NONE;
                deck.add(players.get(action.getTargetPlayer()).removeCardFromDisplay(action.getTargetCardID()));
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            default:
                break;
        }
        return null;
    }

    private GameState handleSelectOpponentDisplayCardForOutwit(PlayerAction action){
        switch (currentAction){
            case OUTWIT:
                currentAction = Properties.ACTION.NONE;
                players.get(currentPlayerID).addCardToDisplay(players.get(action.getTargetPlayer()).removeCardFromDisplay(action.getTargetCardID()));
                players.get(action.getTargetPlayer()).addCardToDisplay(outwitPlaceholder);
                outwitPlaceholder = null;
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            default:
                break;
        }
        return null;
    }

    /**
     *Handles cases where an opponents display needs to be selected.
     * @param action
     * @return
     */
    private GameState handleSelectOpponentDisplay(PlayerAction action) {
        switch (currentAction) {
            case STUNNED:
                players.get(action.getTargetPlayer()).addCardToDisplay(lastActionCardPlayed);
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            case SHIELD:
                players.get(action.getTargetPlayer()).addCardToDisplay(lastActionCardPlayed);
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            case RIPOSTE:
                currentAction = Properties.ACTION.NONE;
                players.get(action.getPlayerID()).addCardToDisplay(players.get(action.getTargetPlayer()).getDisplay().getLastPlayed());
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);

            case BREAK_LANCE:
                //remove all purple cards from player hand
                List<Integer> cardsToRemove = new ArrayList<>();
                Player target = players.get(action.getTargetPlayer());
                //add id of purple cards to list.
                cardsToRemove.addAll(target.getDisplay().getDisplay().stream().filter(c -> c.getColor() == Properties.COLOR.PURPLE).map((Function<Card, Integer>) Card::getId).collect(Collectors.toList()));
                for (Integer i : cardsToRemove) {
                    deck.add(target.removeCardFromDisplay(i));
                }
                currentAction = Properties.ACTION.NONE;
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
            default:
                break;
        }
        return null;
    }

    /**
     * Gets finds the next available player in the list and set's them as the current player
     * @return gamestate
     */
    private GameState handleContinue() {
        //set turn to next player


        //try and get a player before the end of the list
        for (int i = currentPlayerID + 1; i < players.size(); i++) {
            if (players.get(i).isInTournament()) {
                currentPlayerID = i;
                players.get(i).addCardToHand(deck.draw());
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, i, color, lastActionCardPlayed);
            }
        }
        //if there was no player before the end of the list, find one before the player

        for (int i = 0; i < currentPlayerID; i++) {
            if (players.get(i).isInTournament()) {
                currentPlayerID = i;
                players.get(i).addCardToHand(deck.draw());
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, i, color, lastActionCardPlayed);
            }
        }
        //if none found then return null (something went wrong)
        return null;
    }

    /**
     * Handles withdraw. If there is only one player following withdraw then the win signal is sent indicating
     * that that player won the round.
     * @param action PlayerAction from client
     * @return gamestate
     */
    private GameState handleWithdraw(PlayerAction action) {
        players.get(action.getPlayerID()).setInTournament(false);
        int c = 0;
        Player x = null;
        for (Player p : players) {
            if (p.isInTournament()) {
                //saves looping through list again if there's only one player
                x = p;
                c++;
            }
        }
        if (c == 1) {
            currentPlayerID = x.getID();
            maidenHolders = maidenHolders();
            if (maidenHolders.size() > 0) {
                int i = maidenHolders.get(maidenHolders.size() - 1);
                return new GameState(players, Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN, i, color, lastActionCardPlayed);
            }
            cleanDisplays();

            makeBackup();
            return new GameState(players, Properties.GAME_ACTION.WIN, currentPlayerID, color, lastActionCardPlayed);
        } else {
            return handleContinue();
        }
    }

    private void cleanDisplays() {

        for (Player p : players) {
            deck.add(p.getDisplay().getDisplay());
            deck.add(p.getDisplay().getSpecialDisplay());
            p.setDisplay(new Display());
        }
    }
    /**
     * Returns a backup snapshot of the tournament
     * @return
     */
    public TournamentBackup getSnapshot() {
        return backup;
    }

    private GameState handleColorCard(PlayerAction action, Card card) {
        players.get(action.getPlayerID()).addCardToDisplay(card);

        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * The tournament color changes from purple to red, blue or yellow, as announced
     * by the player.
     * @param card action card played.
     * @return gamestate
     */
    private GameState handleUnhorse(Card card) {
        if (color != Properties.COLOR.PURPLE) return null;
        deck.add(card);
        return new GameState(players, Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * Sets the tournament color to the one indicated in the player action.
     * @param action action from client
     * @return gamestate
     */
    private GameState handlePickColor(PlayerAction action) {
        color = action.getColor();
        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * Requests a color selection from the client. specifics will be handled by the client.
     * @param card charge card
     * @return gamestate
     */
    private GameState handleChangeWeapon(Card card) {
        deck.add(card);
        return new GameState(players, Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * changes tournament to green. logic for ensuring this does not happen during a jousting tournament is
     * handled by client
     * @param card card played
     * @return gamestate
     */
    private GameState handleDropWeapon(Card card) {
        color = Properties.COLOR.GREEN;
        deck.add(card);
        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * Remove purple cards from specific players hand
     * @param card card played
     * @return gamestate
     */
    private GameState handleBreakLance(PlayerAction action, Card card) {
        deck.add(card);
        currentAction = Properties.ACTION.BREAK_LANCE;
        return new GameState(players, Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * Remove last card played from target's hand and add to player hand.
     * @param card card played
     * @return gamestate
     */
    private GameState handleRiposte(Card card) {
        deck.add(card);
        currentAction = Properties.ACTION.RIPOSTE;
        return new GameState(players, Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     *Discard the selected card from target opponents display
     * @param card
     * @return
     */
    private GameState handleDodge(Card card) {
        deck.add(card);
        currentAction = Properties.ACTION.DODGE;
        return new GameState(players, Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * Return selected card from display to player's hand
     * @param action
     * @param card
     * @return
     */
    private GameState handleRetreat(PlayerAction action, Card card) {
        deck.add(card);
        currentAction = Properties.ACTION.RETREAT;
        return new GameState(players, Properties.GAME_ACTION.SELECT_DISPLAY_CARD, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     *Draws random card from target's hand and adds it to player hand.
     * @param action
     * @param card
     * @return
     */
    private GameState handleKnockDown(PlayerAction action, Card card) {
        deck.add(card);
        currentAction = Properties.ACTION.KNOCKDOWN;
        return new GameState(players, Properties.GAME_ACTION.SELECT_OPPONENT_HAND, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     *Discard last card played from all opponent decks
     * @param action
     * @param card
     * @return
     */
    private GameState handleOutmaneuver(PlayerAction action, Card card) {
        deck.add(card);

        for (Player p : players) {
            if (p.hasShield() || p.getID() == action.getPlayerID()) continue;
            deck.add(p.getDisplay().getLastPlayed());
        }

        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }


    /**
     * Find lowest value card throughout all displays. remove all cards of that value.
     *
     * @param card charge card
     * @return gamestate
     */
    private GameState handleCharge(Card card) {
        deck.add(card);
        int val = 100;

        //find lowest value
        for (Player p : players) {
            if (p.hasShield()) continue;
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() < val) val = c.getValue();
            }
        }

        //find all cards of same value
        LinkedList<Card> cardsToRemove = new LinkedList<>();
        for (Player p : players) {
            if (p.hasShield()) continue;
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() == val) cardsToRemove.add(c);
            }
        }

        //remove cards from displays and put in deck
        for (Player p : players) {
            if (p.hasShield()) continue;
            for (Card c : cardsToRemove) {
                p.getDisplay().removeCard(c.getId());
                deck.add(c);
            }
        }

        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }


    /**
     * Find highest value card in all displays and remove all cards of that value from everyones display.
     * @param card
     * @return
     */
    private GameState handleCounterCharge(Card card) {
        deck.add(card);
        int val = 0;
        //find lowest value
        for (Player p : players) {
            if (p.hasShield()) continue;
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() > val) val = c.getValue();
            }
        }

        //find all cards of same value
        LinkedList<Card> cardsToRemove = new LinkedList<>();
        for (Player p : players) {
            if (p.hasShield()) continue;
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() == val) cardsToRemove.add(c);
            }
        }

        //remove cards from displays and put in deck
        for (Player p : players) {
            if (p.hasShield()) continue;
            for (Card c : cardsToRemove) {
                p.getDisplay().removeCard(c.getId());
                deck.add(c);
            }
        }

        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }


    /**
     * Removes all supporters from display
     * @param card
     * @return
     */
    private GameState handleDisgrace(Card card) {
        deck.add(card);
        for (Player p : players) {
            if (p.hasShield()) continue;
            List<Card> discardPile = p.getDisplay().getDisplay().stream()
                    .filter(c -> c.getColor() == Properties.COLOR.WHITE
                            && c.getAction() == Properties.ACTION.NONE).collect(Collectors.toList());
            for (Card c : discardPile) {
                p.getDisplay().removeCard(c.getId());
                deck.add(c);
            }
        }
        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }


    /**
     * Handles the initial reception of the adapt action card.
     * @param card adapt card
     * @return return gamestate requesting adapt actions
     */
    private GameState handleAdapt(Card card) {
        deck.add(card);
        currentAction = Properties.ACTION.ADAPT;
        adaptCurrentID = currentPlayerID;
        if (playerHasAdapted(adaptCurrentID)) {
            adaptCurrentID = getNextUnadaptedPlayer(adaptCurrentID);
            return new GameState(players, Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT, adaptCurrentID, color, lastActionCardPlayed);
        }
        return new GameState(players, Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT, adaptCurrentID, color, lastActionCardPlayed);
    }

    /**
     * Gets the next player id of a player with multiples of cards witht he same value
     * @param adaptCurrentID current player that should be adapted already
     * @return
     */
    private int getNextUnadaptedPlayer(int adaptCurrentID) {
        for (int i = adaptCurrentID + 1; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.isInTournament() && !playerHasAdapted(p.getID())) return p.getID();
        }
        for (int i = 0; i < adaptCurrentID; i++) {
            Player p = players.get(i);
            if (p.isInTournament() && !playerHasAdapted(p.getID())) return p.getID();
        }

        return -1;
    }


    /**
     * Handles the outwit action card.
     * Place one of your faceup cards in front of an opponent, and take one faceup
     * card from this opponent and place it face up in front of yourself. This may include the
     * SHIELD and STUNNED cards.
     *
     * begins the process.
     * @param card outwit card to be added to deck
     * @return game state
     */
    private GameState handleOutwit(Card card) {
        deck.add(card);
        currentAction = Properties.ACTION.OUTWIT;
        return new GameState(players, Properties.GAME_ACTION.SELECT_DISPLAY_CARD, currentPlayerID, color, lastActionCardPlayed);
    }

    /**
     * Was identical code for both
     *
     * SHIELD: A player plays this card face up in front of himself, but separate
     * from his display. As long as a player has the SHIELD card in front of him, all
     * action cards have no effect on his display.
     *
     * @param card shield or stunned card to be added to display
     * @return gamestate
     */
    private GameState handleShield(Card card) {
        players.get(currentPlayerID).addCardToDisplay(card);
        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayerID, color, lastActionCardPlayed);
    }


    /**
     * STUNNED: Place this card separately face up in front of any one opponent.
     * As long as a player has the STUNNED card in front of him, he may add only one
     * new card to his display each turn.
     */
    private GameState handleStunned(Card card) {
        lastActionCardPlayed = card;
        currentAction = Properties.ACTION.STUNNED;
        return new GameState(players, Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY, currentPlayerID, color, lastActionCardPlayed);
    }

    private GameState handleIvanhoe(PlayerAction action, Card card) {
        revertToBackup();
        //remove ivanhoe from player hand
        deck.add(players.get(action.getPlayerID()).removeCardFromHand(card.getId()));
        //remove action card from
        deck.add(players.get(currentPlayerID).removeCardFromHand(lastActionCardPlayed.getId()));
        return new GameState(players, ivanhoeOwnerID, currentPlayerID, lastActionCardPlayed,
                Properties.GAME_ACTION.PLAYED_IVANHOE, color);
    }

    /**
     * Checks if a player owns ivanhoe, to be used at start of tournament.
     *
     * @return
     */
    private void checkForIvanhoe() {
        for (Player p : players) {
            for (Card c : p.getHand().getCards()) {
                if (c.getAction() == Properties.ACTION.IVANHOE) {
                    ivanhoeInPlay = true;
                    ivanhoeOwnerID = p.getID();
                    return;
                }
            }
        }
        ivanhoeOwnerID = -1;
        ivanhoeInPlay = false;
    }

    /**
     * Backs up the tournament using copy constructors
     */
    private void makeBackup() {
        backup = new TournamentBackup(deck, players, currentPlayerID, color, ivanhoeOwnerID, ivanhoeInPlay);
    }

    /**
     * Sets all values in the tournament back to their previous state. Note, this includes Ivanhoe
     */
    private void revertToBackup() {
        deck = backup.getDeck();
        players = backup.getPlayers();
        color = backup.getColor();
        ivanhoeOwnerID = backup.getIvanhoeOwnerID();
        ivanhoeInPlay = backup.isIvanhoeInPlay();
        deck.add(lastActionCardPlayed);
    }

    /**
     * Checks if given player has only one card of each value
     * @param id id# of player to check
     * @return player has only one card of each value
     */
    private boolean playerHasAdapted(int id) {

        for( Card c : players.get(id).getDisplay().getSpecialDisplay()){
            if(c.getAction() == Properties.ACTION.SHIELD)
                return true;
        }

        int[] values = new int[8];
        for (Card c : players.get(id).getDisplay().getDisplay()) {
            values[c.getValue()]++;
        }
        for (Integer i : values) {
            if (i > 1) return false;
        }
        return true;
    }

    /**
     * Checks if all players
     * @return
     */
    private boolean allPlayersHaveAdapted() {
        for (Player p : players) {

            if (p.isInTournament() && !playerHasAdapted(p.getID())) return false;
        }
        return true;
    }

    /**
     * Gets the last card played
     * @return las card played
     */
    public Card getLastCardPlayed() {
        return lastCardPlayed;
    }

    /**
     * gets the tournament color
     * @return color
     */
    public Properties.COLOR getColor() {
        return color;
    }


    /**
     * Checks for any maidens in the losing players display and returns a list of ids
     *
     * @return
     */
    private List<Integer> maidenHolders() {
        List<Integer> list = new ArrayList<>();
        players.stream().filter(p -> p.getID() != currentPlayerID).forEach(p -> {
            list.addAll(p.getDisplay().getDisplay().stream().filter(c -> c.getValue() == 6 && p.getPlayerTokens().size() > 0)
                    .map(c -> p.getID()).collect(Collectors.toList()));
        });
        return list;
    }
}
