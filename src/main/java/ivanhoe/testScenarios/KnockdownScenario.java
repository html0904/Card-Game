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
public class KnockdownScenario implements TestScenario {
    private List<Player> players;
    private int clientID;
    private Tournament tournament;


    public KnockdownScenario(int id) {
        clientID = id;
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.KNOCKDOWN, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(1, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(2, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        for (int i = 1; i < 5; i++) {
            p2.addCardToHand(new Card(i, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }

        tournament = new Tournament(players, new Deck(), Properties.COLOR.PURPLE, 0);
    }

    @Override
    public GameState getGameState(PlayerAction action) {
        action.setPlayerID(0);
        if (action.getActionTaken() == Properties.GAME_ACTION.SELECT_HAND_CARD
                || action.getActionTaken() == Properties.GAME_ACTION.SELECT_OPPONENT_HAND) {
            GameState gs = tournament.performAction(action);
            gs.setTargetPlayerID(clientID);
            return gs;
        }
        return new GameState(players, Properties.GAME_ACTION.END_OF_TEST, clientID, Properties.COLOR.PURPLE, tournament.getLastCardPlayed());
    }
}
