package ivanhoe.common;

import ivanhoe.common.components.Card;
import ivanhoe.common.components.Token;
import ivanhoe.common.player.Player;
import ivanhoe.utils.Properties;

import java.io.Serializable;
import java.util.List;


/**
 * Object to be passed from the game engine to the clients.
 */
public class GameState implements Serializable {

    public static final long serialVersionUID = 8L;

    private List<Player> players;
    private Properties.GAME_ACTION GAME_action;
    private Properties.COLOR tournamentColor;
    private int targetPlayer;
    private int ivanhoeOwnerID = -1;
    private Card ivanhoedCard = null;
    private List<Token> tokenList = null;



    private Card lastCardPlayed;
    private int targetPlayerID;

    /**
     * Used for most actions

     *
     * @param players      players in whole game
     * @param action
     * @param targetPlayer
     * @param color
     */
    public GameState(List<Player> players, Properties.GAME_ACTION action, int targetPlayer, Properties.COLOR color, Card lastCardPlayed) {
this.lastCardPlayed=lastCardPlayed;
        this.GAME_action = action;
        this.players = players;
        this.targetPlayer = targetPlayer;
        tournamentColor=color;
    }

    /**
     * Used for triggering ivanhoe, client should inform players what occurred then allow current player to play
     *
     * @param players
     * @param ivanhoeOwnerID
     * @param currentPlayerID
     * @param lastActionCardPlayed
     * @param ivanhoeResponse
     * @param color
     */
    public GameState(List<Player> players, int ivanhoeOwnerID, int currentPlayerID, Card lastActionCardPlayed, Properties.GAME_ACTION ivanhoeResponse, Properties.COLOR color) {
        this.ivanhoedCard = lastActionCardPlayed;
        this.ivanhoeOwnerID = ivanhoeOwnerID;
        targetPlayer = currentPlayerID;
        this.players = players;
        this.GAME_action = ivanhoeResponse;
        this.tournamentColor = color;
    }

    /**
     * Used to show players the token they *picked* at the begining of the game.
     */
    public GameState(List<Player> players, List<Token> tokens, Properties.GAME_ACTION action, int targetPlayer) {
        this.players = players;
        this.tokenList = tokens;
        this.GAME_action = action;
        this.targetPlayer = targetPlayer;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public Card getIvanhoedCard() {
        return ivanhoedCard;
    }

    public int getIvanhoeOwnerID() {
        return ivanhoeOwnerID;
    }

    public Properties.COLOR getTournamentColor(){
        return tournamentColor;
    }
    public int getTargetPlayerID() {
        return targetPlayer;
    }

    public void setTargetPlayerID(int targetPlayerID) {
        this.targetPlayerID = targetPlayerID;
    }

    /**
     * Returns the ID of the player with the highest display value
     * @return the id or -1 if tied
     */
    public int getPlayerIdOfHighestDisplay(){
        int max = 0;
        int idOfHighest = -1;

        for(Player player : players){
            int value = tournamentColor == Properties.COLOR.GREEN ? player.getDisplay().size() : player.getDisplay().getValue();

            if(value == max){
                idOfHighest = -1; // no one player has the highest so reset it to -1
            } else if(value > max){
                max = value;
                idOfHighest = player.getID();
            }
        }
        return idOfHighest;
    }

    /**
     * used in glorious tdd
     * @param obj object to be compared to
     * @return comparison result
     */
    public boolean equals(GameState obj) {
        return getRequestedAction() == obj.getRequestedAction()
                && getTournamentColor() == obj.getTournamentColor()
                && getTargetPlayerID() == obj.getTargetPlayerID();
    }

    /**
     * Checks to ensure that given list has the same player id's in the same order as the global list players.
     *
     * @param objList Player list to be compared against
     * @return if game states are eqivalent
     */
    private boolean playersAreEqual(List<Player> objList) {
        if (players.size() != objList.size()) return false;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getID() != objList.get(i).getID()) return false;
        }
        return true;
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Properties.GAME_ACTION getRequestedAction() {
        return GAME_action;
    }

    public Card getLastCardPlayed() {
        return lastCardPlayed;
    }
}
