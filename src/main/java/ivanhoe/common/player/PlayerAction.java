package ivanhoe.common.player;

import ivanhoe.utils.Properties;

import java.io.Serializable;

/**
 * Object to be passed from the client to the game engine for processing.
 */
public class PlayerAction implements Serializable {

    public static final long serialVersionUID = 6L;

    private int playerID, targetPlayerID, targetCardID, cardPlayedID;
    private Properties.GAME_ACTION action;
    private Properties.COLOR color;



    /**
     * Used for actions that involve selecting a specific card (own or opponent)
     * @param action
     * @param playerID
     * @param targetCardID
     */
    public PlayerAction(Properties.GAME_ACTION action, int playerID, int cardPlayedID, int targetCardID, int targetPlayerID, Properties.COLOR color) {
        this.playerID = playerID;
        this.action = action;
        this.targetCardID = targetCardID;
        this.cardPlayedID = cardPlayedID;
        this.targetPlayerID = targetPlayerID;
        this.color = color;
    }



    public int getTargetCardID() {
        return targetCardID;
    }
    public Properties.COLOR getColor() {
        return color;
    }

    public Properties.GAME_ACTION getActionTaken() {
        return action;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getCardPlayed() {
        return cardPlayedID;
    }

    public int getTargetPlayer() {
        return targetPlayerID;
    }
}
