package ivanhoe.client;

import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;

/**
 * Created by lee on 3/1/2016.
 */
public class ClientLogicHandlerTestHelper extends ClientLogicHandler {
    public ClientLogicHandlerTestHelper(Client client) {
        super(client);
    }

    public ClientLogicHandlerTestHelper(GameState gs, Client client) {
        super(client);
        setGameState(gs);
    }

    public GameState getGameState() {
        return currentGameState;
    }

    public void setGameState(GameState gs) {
        currentGameState = gs;
    }

    public int getID() {
        return playerID;
    }

    public boolean isCardPlayable(Card card) {
        return isPlayable(card);
    }
}
