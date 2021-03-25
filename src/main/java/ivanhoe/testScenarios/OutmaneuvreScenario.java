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
public class OutmaneuvreScenario implements TestScenario {
    private int clientID;
    private List<Player> players;
    private Tournament tournament;

    public OutmaneuvreScenario(int id) {

        clientID = id;
        players = new ArrayList<>();

        //adding players
        for (int i = 0; i < 3; i++) {
            players.add(new Player(i, "Player " + i));
        }

        //add fake cards to opponent hands
        for (int i = 0; i < 5; i++) {
            players.get(1).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(2).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        //add cards to opponent one hand
        players.get(1).addCardToHand(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(1).addCardToHand(new Card(1, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        //add cards to opponent two hand
        players.get(1).addCardToHand(new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(1).addCardToHand(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        players.get(0).addCardToHand(new Card(100, 0, Properties.ACTION.DODGE, Properties.COLOR.NONE));

        tournament = new Tournament(players, new Deck(), Properties.COLOR.RED, 0);
    }

    @Override
    public GameState getGameState(PlayerAction action) {
        action.setPlayerID(0);
        if (action.getActionTaken() == Properties.GAME_ACTION.SELECT_HAND_CARD) {
            GameState gs = tournament.performAction(action);
            gs.setTargetPlayerID(clientID);
            return gs;
        }
        return new GameState(players, Properties.GAME_ACTION.END_OF_TEST, clientID, Properties.COLOR.PURPLE, tournament.getLastCardPlayed());

    }
}
