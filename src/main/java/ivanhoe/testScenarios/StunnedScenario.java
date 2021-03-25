package ivanhoe.testScenarios;

import ivanhoe.common.GameState;
import ivanhoe.common.Tournament;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Deck;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.utils.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 3/7/2016.
 */
public class StunnedScenario implements TestScenario {

    private int clientID;
    private List<Player> players;
    private Tournament tournament;

    public StunnedScenario(int id) {
        clientID = id;
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(200, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p1.addCardToDisplay(new Card(300, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p1.addCardToDisplay(new Card(1, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        tournament = new Tournament(players, new Deck(), Properties.COLOR.PURPLE, 0);
    }

    @Override
    public GameState getGameState(PlayerAction action) {
        action.setPlayerID(0);
        if (action.getActionTaken() == Properties.GAME_ACTION.SELECT_HAND_CARD
                || action.getActionTaken() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY) {
            GameState gs = tournament.performAction(action);
            gs.setTargetPlayerID(clientID);
            return gs;
        }
        return new GameState(players, Properties.GAME_ACTION.END_OF_TEST, clientID, Properties.COLOR.GREEN
                , new Card(12, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.NONE));

    }
}
