package ivanhoe.testcases;

import ivanhoe.common.GameState;
import ivanhoe.common.Tournament;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Deck;
import ivanhoe.common.components.Token;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.utils.Properties;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by lee on 2/20/2016.
 */
public class TournamentTest {

    private static Deck masterDeck;
    Logger log = Logger.getLogger(TournamentTest.class);
    @Rule
    public TestWatcher watchman = new Watchman(log);

    @BeforeClass
    public static void beforeTest() {
        masterDeck = new Deck();
        for (int i = 0; i < 10; i++) {
            masterDeck.add(new Card(i + 10000, i + 1, Properties.ACTION.NONE, Properties.COLOR.RED));
        }
    }

    @Test
    public void testPerformActionNull() throws Exception {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            players.add(new Player(i, "Player " + i));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(players, deck, Properties.COLOR.BLUE, 1);
        GameState gs = t.performAction(null);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD);
    }

    @Test
    public void testPerformActionCardColorPlayed() throws Exception {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Player player = new Player(i, "Player " + i);
            player.addCardToHand(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.BLUE));
            players.add(player);
        }

        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(players, deck, Properties.COLOR.BLUE, 0);

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.BLUE));

        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD);
        assertTrue(gs.getPlayers().get(gs.getTargetPlayerID()).getHandSize() == 1
                && gs.getPlayers().get(gs.getTargetPlayerID()).getDisplay().getDisplay().size() == 1);
    }

    @Test
    public void testPerformActionCardContinuePossible() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Player player = new Player(i, "Player " + i);
            player.addCardToHand(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.BLUE));

            players.add(player);
        }

        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(players, deck, Properties.COLOR.BLUE, 2);
        for (Player player : players) {
            player.setInTournament(player.getID() % 2 == 0);
        }
        PlayerAction action = new PlayerAction(Properties.GAME_ACTION.CONTINUE, 2, -1, -1, -1, Properties.COLOR.PURPLE);
        GameState gs = t.performAction(action);
        assertTrue(gs.getTargetPlayerID() == 4);
    }

    @Test
    public void testPerformActionCardContinueOnePlayerLeft() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Player player = new Player(i, "Player " + i);
            player.addCardToHand(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.BLUE));
            players.add(player);
        }

        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(players, deck, Properties.COLOR.BLUE, 2);
        for (Player p : players) {
            p.setInTournament(false);
            if (p.getID() == 2) p.setInTournament(true);
        }
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.CONTINUE, 2, 2, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(gs == null);
    }

    @Test
    public void testPerformActionCardContinueNextPlayerEarlierInList() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Player player = new Player(i, "Player " + i);
            player.addCardToHand(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.BLUE));

            players.add(player);
        }

        Deck deck = new Deck();
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.BLUE));
        deck.add(cards);
        Tournament t = new Tournament(players, deck, Properties.COLOR.BLUE, 3);
        for (Player p : players) {
            p.setInTournament(false);
            if (p.getID() == 2) p.setInTournament(true);
            if (p.getID() == 3) p.setInTournament(true);
        }
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.CONTINUE, 3, -1, -1, -1, Properties.COLOR.PURPLE));
        assertTrue(gs.getTargetPlayerID() == 2);
    }

    /**
     * Withdraw with more than one player left should be like continue.
     */
    @Test
    public void testWithdrawWithMoreThanOnePlayerLeft() {
        //create three players
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new Player(i, "Player " + i));
        }

        Deck deck = new Deck(masterDeck);

        //create tournament
        Tournament t = new Tournament(list, deck, Properties.COLOR.BLUE, 2);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getTargetPlayerID() == 1);
    }

    /**
     * Withdraw with one player left: triggers victory for the next player.
     */
    @Test
    public void testWithdrawWithOnePlayerLeft() {
        //create three players
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new Player(i, "Player " + i));
        }


        Deck deck = new Deck(masterDeck);


        //create tournament
        Tournament t = new Tournament(list, deck, Properties.COLOR.BLUE, 0);
        list.get(1).setInTournament(false);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getTargetPlayerID() == 2);
    }

    @Test
    public void testUnhorseWithPurpleTournament() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card unhorse = new Card(0, 0, Properties.ACTION.UNHORSE, Properties.COLOR.NONE);
        Deck deck = new Deck(masterDeck);
        playerList.get(0).addCardToHand(unhorse);


        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN);
    }


    @Test
    public void testUnhorseWithNONPurpleTournament() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card unhorse = new Card(0, 0, Properties.ACTION.UNHORSE, Properties.COLOR.NONE);
        Deck deck = new Deck(masterDeck);
        playerList.get(0).addCardToHand(unhorse);


        Tournament t = new Tournament(playerList, deck, Properties.COLOR.BLUE, 0);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs == null);
    }

    @Test
    public void testChangeWeapon() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card unhorse = new Card(0, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.NONE);
        Deck deck = new Deck(masterDeck);
        playerList.get(0).addCardToHand(unhorse);


        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN);
    }

    @Test
    public void testDropWeapon() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card unhorse = new Card(0, 0, Properties.ACTION.DROP_WEAPON, Properties.COLOR.NONE);
        Deck deck = new Deck(masterDeck);
        playerList.get(0).addCardToHand(unhorse);


        Tournament t = new Tournament(playerList, deck, Properties.COLOR.BLUE, 0);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD && gs.getTournamentColor() == Properties.COLOR.GREEN);
    }

    @Test
    public void testBreakLance() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }

        Card breakLance = new Card(0, 0, Properties.ACTION.BREAK_LANCE, Properties.COLOR.NONE);
        Card purple2 = new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE);
        Card purple4 = new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE);
        Card squire2 = new Card(3, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE);

        playerList.get(0).addCardToHand(breakLance);

        for (int i = 1; i < 3; i++) {
            playerList.get(i).addCardToHand(purple2);
            playerList.get(i).addCardToHand(purple4);
            playerList.get(i).addCardToHand(squire2);
        }

        Deck deck = new Deck(masterDeck);

        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, 1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY);
    }

    @Test
    public void testRiposte() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }

        Card riposte = new Card(0, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(riposte);

        for (int i = 1; i < 4; i++) {
            playerList.get(1).addCardToDisplay(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(2).addCardToDisplay(new Card(i + 100, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, 1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY);

    }

    @Test
    public void testDodge() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card dodge = new Card(0, 0, Properties.ACTION.DODGE, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(dodge);
        for (int i = 1; i < 4; i++) {
            playerList.get(1).addCardToDisplay(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(2).addCardToDisplay(new Card(i + 100, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        ArrayList<Player> target = new ArrayList<>();
        target.add(playerList.get(1));
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, 1, 1, Properties.COLOR.NONE));
        final boolean[] f = {true};

        //if player id:1 has a card with id 1 then set to false
        gs.getPlayers().get(1).getHand().getCards().stream().filter(c -> c.getId() == 1).forEach(c -> f[0] = false);

        assertTrue(f[0]);
    }

    @Test
    public void testRetreat() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card dodge = new Card(0, 0, Properties.ACTION.RETREAT, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(dodge);
        for (int i = 1; i < 4; i++) {
            playerList.get(1).addCardToDisplay(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(2).addCardToDisplay(new Card(i + 100, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(0).addCardToDisplay(new Card(i + 1000, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        ArrayList<Player> target = new ArrayList<>();
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, 1001, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD);
    }

    @Test
    public void testKnockDown() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card dodge = new Card(0, 0, Properties.ACTION.KNOCKDOWN, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(dodge);
        for (int i = 1; i < 4; i++) {
            playerList.get(1).addCardToHand(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(2).addCardToHand(new Card(i + 100, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        ArrayList<Player> target = new ArrayList<>();
        target.add(playerList.get(1));
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, 1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_OPPONENT_HAND);
    }

    @Test
    public void testOutManeuver() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card dodge = new Card(0, 0, Properties.ACTION.OUTMANEUVER, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(dodge);
        for (int i = 1; i < 4; i++) {
            playerList.get(1).addCardToDisplay(new Card(i, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(2).addCardToDisplay(new Card(i + 100, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(0).addCardToDisplay(new Card(i + 1000, 2, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        ArrayList<Player> target = new ArrayList<>();

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getPlayers().get(0).getDisplaySize() == 3
                && gs.getPlayers().get(1).getDisplaySize() == 2
                && gs.getPlayers().get(2).getDisplaySize() == 2);
    }

    @Test
    public void testCharge() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card dodge = new Card(0, 0, Properties.ACTION.CHARGE, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(dodge);
        for (int i = 1; i < 4; i++) {
            playerList.get(1).addCardToDisplay(new Card(i, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(2).addCardToDisplay(new Card(i + 100, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(0).addCardToDisplay(new Card(i + 1000, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        ArrayList<Player> target = new ArrayList<>();

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getPlayers().get(0).getDisplaySize() == 2
                && gs.getPlayers().get(1).getDisplaySize() == 2
                && gs.getPlayers().get(2).getDisplaySize() == 2);
    }

    @Test
    public void testCounterCharge() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card dodge = new Card(0, 0, Properties.ACTION.COUNTERCHARGE, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(dodge);
        for (int i = 1; i < 4; i++) {
            playerList.get(1).addCardToDisplay(new Card(i, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(2).addCardToDisplay(new Card(i + 100, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(0).addCardToDisplay(new Card(i + 1000, i, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        ArrayList<Player> target = new ArrayList<>();

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getPlayers().get(0).getDisplaySize() == 2
                && gs.getPlayers().get(1).getDisplaySize() == 2
                && gs.getPlayers().get(2).getDisplaySize() == 2);
    }

    @Test
    public void testDisgrace() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        Card dodge = new Card(99, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE);
        playerList.get(0).addCardToHand(dodge);
        for (int i = 0; i < 3; i++) {
            playerList.get(i).addCardToDisplay(new Card(i, 1, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            playerList.get(i).addCardToDisplay(new Card(i + 100, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            playerList.get(i).addCardToDisplay(new Card(i + 1000, 3, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        ArrayList<Player> target = new ArrayList<>();

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 99, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getPlayers().get(0).getDisplay().getValue() == 4
                && gs.getPlayers().get(1).getDisplay().getValue() == 4
                && gs.getPlayers().get(2).getDisplay().getValue() == 4);
    }

    @Test
    public void testAdapt() {
        List<Player> playerList = new ArrayList<>();
        Player p = new Player(0, "Billy-Bob");
        p.addCardToHand(new Card(0, 0, Properties.ACTION.ADAPT, Properties.COLOR.WHITE));
        playerList.add(p);
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT);
    }

    @Test
    public void testPerformAdapt() {
        List<Player> playerList = new ArrayList<>();
        Player p1 = new Player(0, "Player 1");
        Player p2 = new Player(1, "Player 2");
        playerList.add(p1);
        playerList.add(p2);
        p1.addCardToHand(new Card(0, 0, Properties.ACTION.ADAPT, Properties.COLOR.WHITE));
        Tournament t = new Tournament(playerList, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT);
    }

    @Test
    public void testOutwit() {
        List<Player> playerList = new ArrayList<>();
        Player p1 = new Player(0, "Player 1");
        Player p2 = new Player(1, "Player 2");

        p1.addCardToDisplay(new Card(0, 0, Properties.ACTION.SHIELD, Properties.COLOR.WHITE));
        p2.addCardToDisplay(new Card(1, 0, Properties.ACTION.STUNNED, Properties.COLOR.WHITE));

        p2.addCardToHand(new Card(2, 0, Properties.ACTION.OUTWIT, Properties.COLOR.WHITE));
        playerList.add(p1);
        playerList.add(p2);
        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 1);
        ArrayList<Player> target = new ArrayList<>();
        target.add(playerList.get(0));
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 1, 2, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_DISPLAY_CARD);
    }

    @Test
    public void testShield() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }

        playerList.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.SHIELD, Properties.COLOR.WHITE));

        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getPlayers().get(0).hasShield());
    }

    @Test
    public void testStunned() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }

        playerList.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.STUNNED, Properties.COLOR.NONE));

        Deck deck = new Deck(masterDeck);
        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY, 0, -1, -1, 1, Properties.COLOR.NONE));
        assertTrue(gs.getPlayers().get(1).hasStunned());
    }

    @Test
    public void testIVANHOOOOOOE() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        playerList.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE));
        playerList.get(1).addCardToHand(new Card(1, 1, Properties.ACTION.IVANHOE, Properties.COLOR.WHITE));
        Deck deck = new Deck(masterDeck);

        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        GameState gameState = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        assertTrue(gameState.getRequestedAction() == Properties.GAME_ACTION.SELECT_IVANHOE
                && gameState.getTargetPlayerID() == 1);
    }

    @Test
    public void testIvanhoeChosen() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        playerList.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE));
        playerList.get(1).addCardToHand(new Card(1, 1, Properties.ACTION.IVANHOE, Properties.COLOR.WHITE));

        //this is a suporter card.. it will be removed by disgrace unless ivanhoe saves it.
        playerList.get(1).addCardToDisplay(new Card(2, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        Deck deck = new Deck(masterDeck);

        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.PLAYED_IVANHOE, 1, 1, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getPlayers().get(1).getDisplaySize() == 1);
    }

    @Test
    public void testIvanhoeDeclined() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        playerList.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE));
        playerList.get(1).addCardToHand(new Card(1, 1, Properties.ACTION.IVANHOE, Properties.COLOR.WHITE));
        Deck deck = new Deck(masterDeck);

        Tournament t = new Tournament(playerList, deck, Properties.COLOR.PURPLE, 0);
        t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, 0, 0, -1, -1, Properties.COLOR.NONE));
        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, -1, -1, -1, -1, Properties.COLOR.NONE));
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_HAND_CARD);
    }


    @Test
    public void testMaidenLoseTokenReturnNoTokens() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        playerList.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE));

        playerList.get(0).addCardToDisplay(new Card(1, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        Deck deck = new Deck();
        deck.add(new Card(3, 4, Properties.ACTION.NONE, Properties.COLOR.RED));

        Tournament t = new Tournament(playerList, deck, Properties.COLOR.GREEN, 0);

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.GREEN));

        assertTrue(gs.getTargetPlayerID() == 1);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN);
    }

    @Test
    public void testMaidenLoseTokenReturnHasTokens() {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            playerList.add(new Player(i, "Player " + i));
        }
        playerList.get(0).addCardToHand(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE));

        playerList.get(0).addCardToDisplay(new Card(1, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        playerList.get(0).addToken(new Token(Properties.COLOR.PURPLE));
        playerList.get(0).addToken(new Token(Properties.COLOR.RED));
        playerList.get(0).addToken(new Token(Properties.COLOR.GREEN));

        Deck deck = new Deck();
        deck.add(new Card(3, 4, Properties.ACTION.NONE, Properties.COLOR.RED));

        Tournament t = new Tournament(playerList, deck, Properties.COLOR.GREEN, 0);

        GameState gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, 0, -1, -1, -1, Properties.COLOR.GREEN));

        assertTrue(gs.getTargetPlayerID() == 0);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN);

        gs = t.performAction(new PlayerAction(Properties.GAME_ACTION.SELECT_TOKEN_TO_RETURN, 0, -1, -1, -1, Properties.COLOR.GREEN));

        assertTrue(gs.getTargetPlayerID() == 1);
        assertTrue(gs.getRequestedAction() == Properties.GAME_ACTION.WIN);
    }
}