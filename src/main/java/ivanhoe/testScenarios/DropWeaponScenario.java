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
public class DropWeaponScenario implements TestScenario {
    private Tournament tournament;
    private List<Player> players;
    private int clientID;

    public DropWeaponScenario(int id) {
        clientID = id;
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.DROP_WEAPON, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(1, 7, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p2.addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        tournament = new Tournament(players, new Deck(), Properties.COLOR.YELLOW, 0);

    }


    @Override
    public GameState getGameState(PlayerAction action) {
        action.setPlayerID(0);
        if (action.getActionTaken() == Properties.GAME_ACTION.SELECT_HAND_CARD) {
            GameState gs = tournament.performAction(action);
            gs.setTargetPlayerID(clientID);
            return gs;
        }
        return new GameState(players, Properties.GAME_ACTION.END_OF_TEST, clientID, Properties.COLOR.GREEN
                , new Card(12, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.NONE));

    }
}
