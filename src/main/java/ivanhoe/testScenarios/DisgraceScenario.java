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
 * Created by hyunminlee on 2016-03-07.
 */
public class DisgraceScenario implements TestScenario{
    private Tournament tournament;
    private List<Player> players;

    public DisgraceScenario() {
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(1, 7, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p1.addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(2, 5, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p2.addCardToDisplay(new Card(2, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        tournament = new Tournament(players, new Deck(), Properties.COLOR.PURPLE, 0);
    }

    @Override
    public GameState getGameState(PlayerAction action) {
        action.setPlayerID(0);
        if (action.getActionTaken() == Properties.GAME_ACTION.SELECT_HAND_CARD) {
            return tournament.performAction(action);
        }

        return null;
    }
}
