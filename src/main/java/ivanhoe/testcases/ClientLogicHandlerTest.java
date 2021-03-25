package ivanhoe.testcases;

import ivanhoe.client.Client;
import ivanhoe.client.ClientLogicHandlerTestHelper;
import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.player.Player;
import ivanhoe.utils.Properties;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by lee on 3/1/2016.
 */
public class ClientLogicHandlerTest {
    private static final Logger log = Logger.getLogger(ClientLogicHandlerTest.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);


    @Test
    public void testCanPlayUnhorse() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.PURPLE, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.UNHORSE, Properties.COLOR.WHITE)));
    }


    @Test
    public void testCannotPlayUnhorse() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.RED, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.UNHORSE, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCanPlayChangeWeaponRed() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.RED, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCanPlayChangeWeaponYellow() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCanPlayChangeWeaponBlue() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.BLUE, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCannotPlayChangeWeaponPurple() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.PURPLE, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCannotPlayChangeWeaponGreen() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.GREEN, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCanPlayShield() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.GREEN, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.SHIELD, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCanPlayStuned() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.GREEN, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.STUNNED, Properties.COLOR.WHITE)));
    }

    @Test
    public void testCanPlayDropWeapon() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.DROP_WEAPON, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayBreakLance() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }
        players.get(1).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.BREAK_LANCE, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayBreakLance() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }
        players.get(1).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.BREAK_LANCE, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayRiposte() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }

        players.get(1).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        players.get(1).addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayRiposte() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }

        players.get(0).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayDodge() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }

        players.get(1).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        players.get(1).addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.DODGE, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayDodge() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }
        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.DODGE, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayRetreat() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }

        players.get(0).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        players.get(0).addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.RETREAT, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayRetreat() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }

        players.get(1).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.RETREAT, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayKnockdown() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToHand(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.KNOCKDOWN, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayKnockdown() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.KNOCKDOWN, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayOutmaneuver() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.OUTMANEUVER, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayOutmaneuver() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i, ""));
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.OUTMANEUVER, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayCharge() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            player.addCardToDisplay(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.CHARGE, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayCharge() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.CHARGE, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayCounterCharge() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            player.addCardToDisplay(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.COUNTERCHARGE, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayCounterCharge() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.GREEN));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.COUNTERCHARGE, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayDisgrace() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.WHITE));

            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayDisgrace() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.WHITE));

            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.DISGRACE, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayAdapt() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.WHITE));

            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.ADAPT, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayAdapt() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            player.addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            player.addCardToDisplay(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.ADAPT, Properties.COLOR.WHITE)));
    }

    @Test
    public void canPlayOutwit() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");

            player.addCardToDisplay(new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.OUTWIT, Properties.COLOR.WHITE)));
    }

    @Test
    public void cannotPlayOutwit() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player(i, "");
            players.add(player);
        }


        GameState gameState = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.YELLOW, null);
        ClientLogicHandlerTestHelper clientLogic = new ClientLogicHandlerTestHelper(gameState, new Client(0));
        assertTrue(!clientLogic.isCardPlayable(new Card(0, 0, Properties.ACTION.OUTWIT, Properties.COLOR.WHITE)));
    }
}