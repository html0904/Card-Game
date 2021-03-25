package ivanhoe.client.skynet;

import ivanhoe.common.GameState;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.utils.Properties;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Simple AI to test concept. will withdraw whenever possible
 */
public class WillardTheWithdrawer extends ArtificialPlayer {
    /**
     * Basic constructor for any artificial player. will make its own socke in order to communicate with the server
     * without the need for complex method calls.
     *
     * @param serverPort    port to connect to
     * @param serverAddress server address
     */
    public WillardTheWithdrawer(int serverPort, String serverAddress) {
        super(serverPort, serverAddress);
    }

    /**
     * When Id is known at creation (i.e. replacing a disconnected player)
     *
     * @param serverPort port to connect to
     * @param address    server address
     * @param id         id to replace
     */
    public WillardTheWithdrawer(int serverPort, String address, int id) {
        super(serverPort, address, id);
    }

    /**
     * This AI always withdraws when possible and selects yellow when asked for a color
     *
     * @param gs gamestate to respond to
     */
    @Override
    public void handleGameState(GameState gs) throws IOException {
        if (gs.getTargetPlayerID() != id) return;
        switch (gs.getRequestedAction()) {
            case SELECT_COLOR:
                oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, id, -1, -1, -1, Properties.COLOR.YELLOW));
                break;
            case SELECT_COLOR_OTHER_THAN_PURPLE:
                oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, id, -1, -1, -1, Properties.COLOR.YELLOW));
                break;
            case SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN:
                oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, id, -1, -1, -1, Properties.COLOR.YELLOW));
                break;
            case WIN:
                oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, id, -1, -1, -1, Properties.COLOR.YELLOW));
                break;
            default:
                oos.writeObject(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, id, -1, -1, -1, Properties.COLOR.YELLOW));
                break;
        }
    }


}
