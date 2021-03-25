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
public class IvanhoeScenario implements TestScenario {

    private int clientID;
    private List<Player> players;
    private Tournament tournament;

    public IvanhoeScenario(int id) {
        players = new ArrayList<>();

        players.add(new Player(0, "Client"));
        players.add(new Player(1, "Opponent"));


        players.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.IVANHOE, Properties.COLOR.NONE));
        for (int i = 1; i < 3; i++) {
            players.get(0).addCardToDisplay(new Card(i, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            players.get(0).addCardToDisplay(new Card(i + 10, i, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            players.get(1).addCardToHand(new Card(i + 1000, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }

        players.get(1).addCardToHand(new Card(69, 0, Properties.ACTION.BREAK_LANCE, Properties.COLOR.NONE));
        tournament = new Tournament(players, new Deck(), Properties.COLOR.PURPLE, 1);
    }

    @Override
    public GameState getGameState(PlayerAction action) {
        action.setPlayerID(0);
        if (action == null || action.getActionTaken() == Properties.GAME_ACTION.SELECT_HAND_CARD) {
            GameState gs = tournament.performAction(action);
            if (gs.getTargetPlayerID() == 0) {
                gs.setTargetPlayerID(clientID);
            } else {
                if (players.get(1).getHandSize() == 3) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return tournament.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 1, 69, -1, -1, Properties.COLOR.PURPLE));
                }
            }
            return gs;
        }
        return new GameState(players, Properties.GAME_ACTION.END_OF_TEST, clientID, Properties.COLOR.GREEN
                , new Card(12, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.NONE));

    }
}
