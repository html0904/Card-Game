package ivanhoe.client;

import ivanhoe.common.Tournament;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.networking.ClientConnection;
import ivanhoe.utils.Properties;
import ivanhoe.utils.TestStateProvider;

/**
 * Created by Yan on 3/7/2016.
 */
public class ClientConnectionTest extends ClientConnection {

    private SCENARIO scenario;
    private Client client;
    private Tournament currentTestingTournament;

    public ClientConnectionTest(Client client, SCENARIO scenario){
        super(client, "localhost", -1);
        this.scenario = scenario;
        this.client = client;

        sendFirstGameState();
    }

    /**
     * Sends the first game state to the client
     */
    private void sendFirstGameState(){
        client.setPlayerID(0);
        currentTestingTournament = TestStateProvider.generateTournament(scenario);
        client.updateGameState(currentTestingTournament.performAction(scenario != SCENARIO.IVANHOE ? null : new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 1, 69, -1, 0, Properties.COLOR.PURPLE)));
    }

    @Override
    public void send(Object msg) {
        PlayerAction receivedAction = (PlayerAction) msg;
        System.out.println(String.format("\nTesting server received player action %s, color %s", receivedAction.getActionTaken(), receivedAction.getColor()));
        client.updateGameState(currentTestingTournament.performAction(receivedAction));
    }

    public enum SCENARIO {
        UNHORSE,
        CHANGEWEAPON,
        DROPWEAPON,


        BREAKLANCE,
        RIPOSTE,
        DODGE,
        RETREAT,
        KNOCKDOWN,

        OUTMANEUVER,
        CHARGE,
        COUNTERCHARGE,
        DISGRACE,
        ADAPT,

        OUTWIT,
        SHIELD,
        STUNNED,

        IVANHOE
    }
}
