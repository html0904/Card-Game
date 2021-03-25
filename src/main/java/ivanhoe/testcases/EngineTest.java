package ivanhoe.testcases;

import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Token;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.engine.Engine;
import ivanhoe.utils.Properties;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by lee on 08/02/16.
 */
public class EngineTest {

    private static final Logger log = Logger.getLogger(EngineTest.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);


    private List<Player> generatePlayerList(int num) {
        List<Player> playerList = new ArrayList<>();
        for (int j = 0; j < num; j++) {
            playerList.add(new Player(j, "Player " + (j + 1)));
        }
        return playerList;
    }

    private Engine getNewMatch(int players) {
        //populate player list

        return new Engine(generatePlayerList(players));
    }

    @Test
    public void testPickDealer() throws Exception {

        List<Player> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new Player(i, "Player " + i));
        }
        Engine engine = new Engine(list);
        List<Token> tokenList = engine.pickDealer();
        assertTrue(tokenList.size() == 5);
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (i >= tokenList.size()) continue;
            Token token = tokenList.get(i);
            if (token.getTokenColor() == Properties.COLOR.PURPLE) {
                count++;
                tokenList.remove(token);
            }
        }

        assertTrue(count == 1);
    }

    @Test
    public void testPlayerWonTournament() {
        List<Player> list = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            list.add(new Player(i, "Player " + i));
        }
        Engine engine = new Engine(list);
        GameState gs = engine.getNextStep(null);
        int dealer = gs.getTargetPlayerID();
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.PURPLE));
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN
                && gs.getTargetPlayerID() != dealer);

    }


    @Test
    public void testPurpleTournamentWin() {
        //make players
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        //give them purple 7s
        for (Player p : playerList) {
            p.addCardToHand(new Card(1000 + p.getID(), 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }

        Engine engine = new Engine(playerList);

        GameState gs1 = engine.getNextStep(null);
        assertTrue(gs1.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR);
        int currentPlayer = gs1.getTargetPlayerID();

        GameState gs2 = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, currentPlayer, -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(gs2.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD);

        GameState gs3 = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, currentPlayer, 1000 + currentPlayer, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(gs3.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD);

        GameState p1Continue = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.CONTINUE, currentPlayer, -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(p1Continue.getTargetPlayerID() != currentPlayer);

        GameState p2Withdraw = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, p1Continue.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(p2Withdraw.getRequestedAction() == Properties.GAME_ACTION.WIN);

        GameState p1ChoosePurple = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, currentPlayer, -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(p1ChoosePurple.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE);
    }


    /**
     * A player should be able to start a game of Ivanhoe for X players where (2 <= X <= 5)
     */
    @Test
    public void testTwoToFivePlayers() {


        for (int i = 2; i < 6; i++) {

            //populate player list
            List<Player> playerList = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                playerList.add(new Player(j - 1, "Player " + (j + 1)));
            }

            //create engine instance
            Engine engine = new Engine(playerList);
            engine.getNextStep(null);
            playerList.stream().forEach(p -> assertTrue(p.getHandSize() == 8));
        }
    }

    @Test
    public void testStartFirstTournament() {

        //test for red
        Engine engine = getNewMatch(2);
        GameState gs = engine.getNextStep(null);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR);
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD
                && gs.getTournamentColor() == Properties.COLOR.RED);


        //test yellow
        engine = getNewMatch(2);
        gs = engine.getNextStep(null);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR);

        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.YELLOW));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD
                && gs.getTournamentColor() == Properties.COLOR.YELLOW);


        //test blue
        engine = getNewMatch(2);
        gs = engine.getNextStep(null);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR);

        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.BLUE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD
                && gs.getTournamentColor() == Properties.COLOR.BLUE);

        //test purple
        engine = getNewMatch(2);
        gs = engine.getNextStep(null);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR);

        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD
                && gs.getTournamentColor() == Properties.COLOR.PURPLE);

        //test green
        engine = getNewMatch(2);
        gs = engine.getNextStep(null);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR);

        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.GREEN));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD
                && gs.getTournamentColor() == Properties.COLOR.GREEN);
    }


    @Test
    public void testStartingATournamentPreviousTournamentWasPurple() {
        List<Player> players = generatePlayerList(2);
        players.get(0).addCardToHand(new Card(9000, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        Engine engine = new Engine(players);

        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.PURPLE));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.PURPLE));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.CONTINUE, 0, -1, -1, -1, Properties.COLOR.PURPLE));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 1, -1, -1, -1, Properties.COLOR.PURPLE));

        //since the tournament was purple the client knows to select a token color
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 1, -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE);


    }

    @Test
    public void testStartingATournamentPreviousTournamentWasNotPurple() {
        List<Player> players = generatePlayerList(2);
        players.get(0).addCardToHand(new Card(9000, 7, Properties.ACTION.NONE, Properties.COLOR.RED));

        Engine engine = new Engine(players);

        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.CONTINUE, 0, -1, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 1, -1, -1, -1, Properties.COLOR.RED));

        //since the tournament was not purple we select a new tournament color
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 1, -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD);
    }

    /**
     * When a player begins his turn he draws a card
     */
    @Test
    public void testDrawACard() {
        List<Player> players = generatePlayerList(2);
        Engine engine = new Engine(players);

        GameState gs = engine.getNextStep(null);
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.RED));
        assertTrue(players.get(gs.getTargetPlayerID()).getHandSize() == 9);

    }


    @Test
    public void testPlayACardColored() {
        List<Player> players = generatePlayerList(2);
        players.get(0).addCardToHand(new Card(9000, 2, Properties.ACTION.NONE, Properties.COLOR.RED));
        Engine engine = new Engine(players);

        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getPlayers().get(0).getDisplay().getDisplay().get(0).getId() == 9000);
    }

    @Test
    public void testPlayACardSupporter() {
        List<Player> players = generatePlayerList(4);
        players.get(0).addCardToHand(new Card(9000, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        Engine engine = new Engine(players);

        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getPlayers().get(0).getDisplay().getDisplay().get(0).getId() == 9000);
    }

    @Test
    public void testPlayACardMaiden() {
        List<Player> players = generatePlayerList(2);
        players.get(0).addCardToHand(new Card(9000, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        Engine engine = new Engine(players);

        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.YELLOW));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.YELLOW));
        assertTrue(gs.getPlayers().get(0).getDisplay().getDisplay().get(0).getId() == 9000);
    }

    @Test
    public void testPlayACardAction() {
        List<Player> players = generatePlayerList(2);
        players.get(0).addCardToHand(new Card(9000, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(0).addCardToHand(new Card(9001, 0, Properties.ACTION.RETREAT, Properties.COLOR.NONE));

        Engine engine = new Engine(players);

        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9001, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD);
    }


    @Test
    public void testWithdrawFromTournamentWithMaidenAndHasToken() {

        List<Player> players = generatePlayerList(2);
        players.get(0).addCardToHand(new Card(9000, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(0).addToken(new Token(Properties.COLOR.BLUE));

        Engine engine = new Engine(players);
        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN);
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN, 0, -1, -1, -1, Properties.COLOR.BLUE));
        assertTrue(gs.getPlayers().get(0).getPlayerTokens().size() == 0);
    }

    @Test
    public void testWithdrawFromTournamentWithMaidenAndHasNoToken() {

        List<Player> players = generatePlayerList(2);
        players.get(0).addCardToHand(new Card(9000, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        Engine engine = new Engine(players);
        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 9000, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.RED));
        System.out.println("Action: " + gs.getRequestedAction());
        System.out.println("Player: " + gs.getTargetPlayerID());
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN
                && gs.getTargetPlayerID() == 1);
    }

    @Test
    public void testPlayerWithdrawsFromTournamentAndNoLongerGetsATurn() {
        List<Player> players = generatePlayerList(3);

        //no way of knowing which player starts so all must have identical hands
        players.get(0).addCardToHand(new Card(9000, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(0).addCardToHand(new Card(9001, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        players.get(1).addCardToHand(new Card(9000, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(1).addCardToHand(new Card(9001, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        players.get(2).addCardToHand(new Card(9000, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(2).addCardToHand(new Card(9001, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        int firstToDrop;
        Engine engine = new Engine(players);

        //one player starts, and withdraws immediately
        GameState gs = engine.getNextStep(null);
        firstToDrop = gs.getTargetPlayerID();
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, firstToDrop, -1, -1, -1, Properties.COLOR.YELLOW));

        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, firstToDrop, -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getTargetPlayerID() != firstToDrop);

        //next player plays a supporter card and continues
        int secondToPlay = gs.getTargetPlayerID();
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, secondToPlay, 9000, -1, -1, Properties.COLOR.RED));
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.CONTINUE, secondToPlay, -1, -1, -1, Properties.COLOR.RED));

        assertTrue(gs.getTargetPlayerID() != firstToDrop && gs.getTargetPlayerID() != secondToPlay);

        //third player plays two suporter cards and continues. which should result in the second player playing again, not the first
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, gs.getTargetPlayerID(), 9000, -1, -1, Properties.COLOR.RED));
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, gs.getTargetPlayerID(), 9001, -1, -1, Properties.COLOR.RED));
        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.CONTINUE, gs.getTargetPlayerID(), -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getTargetPlayerID() == secondToPlay);
    }

    @Test
    public void testPlayerWinsNonPurpleTournamentAndReceivesRightColoredToken() {
        List<Player> players = generatePlayerList(2);

        Engine engine = new Engine(players);
        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN
                && gs.getTargetPlayerID() == 1 && gs.getPlayers().get(1).getPlayerTokens().get(0).getTokenColor() == Properties.COLOR.RED);
    }

    @Test
    public void testPlayerWinsPurpleTournamentAndSelectsAndReceivesRightColoredToken() {
        List<Player> players = generatePlayerList(2);

        Engine engine = new Engine(players);
        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.PURPLE));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.PURPLE));

        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 1, -1, -1, -1, Properties.COLOR.BLUE));
        assertTrue(gs.getPlayers().get(1).getPlayerTokens().get(0).getTokenColor() == Properties.COLOR.BLUE);
    }

    @Test
    public void testPlayerWinsTournamentAndDoesNotReceiveDuplicateTokens() {
        List<Player> players = generatePlayerList(2);
        players.get(0).addToken(new Token(Properties.COLOR.RED));
        Engine engine = new Engine(players);
        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN
                && gs.getTargetPlayerID() == 1 && gs.getPlayers().get(1).getPlayerTokens().get(0).getTokenColor() == Properties.COLOR.RED
                && gs.getPlayers().get(1).getPlayerTokens().size() == 1);
    }

    @Test
    public void testWinGameWithTwoOrThreePlayers() {
        List<Player> players = generatePlayerList(2);
        players.get(1).addToken(new Token(Properties.COLOR.PURPLE));
        players.get(1).addToken(new Token(Properties.COLOR.BLUE));
        players.get(1).addToken(new Token(Properties.COLOR.GREEN));
        players.get(1).addToken(new Token(Properties.COLOR.YELLOW));
        Engine engine = new Engine(players);
        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.RED));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN_GAME);

    }

    @Test
    public void testWinGameWithFourOrFivePlayers() {
        List<Player> players = generatePlayerList(4);
        players.get(3).addToken(new Token(Properties.COLOR.PURPLE));
        players.get(3).addToken(new Token(Properties.COLOR.BLUE));
        players.get(3).addToken(new Token(Properties.COLOR.YELLOW));
        Engine engine = new Engine(players);
        engine.getNextStep(null);
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.RED));
        engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 1, -1, -1, -1, Properties.COLOR.RED));
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 2, -1, -1, -1, Properties.COLOR.RED));

        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN_GAME);

    }

    @Test
    public void testPlayerUpdated() {
        List<Player> players = generatePlayerList(2);

        Engine engine = new Engine(players);
        engine.getNextStep(null);
        GameState gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, 0, -1, -1, -1, Properties.COLOR.PURPLE));

        int handSize = gs.getPlayer(0).getDisplaySize();

        gs = engine.getNextStep(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, gs.getPlayers().get(0).getHand().getCards().get(0).getId(), -1, -1, Properties.COLOR.PURPLE));

        assertTrue(handSize != gs.getPlayers().get(0).getHandSize());
    }
}