package ivanhoe.testcases;


import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.player.Player;
import ivanhoe.utils.Properties;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lee on 06/02/16.
 */
public class GameStateTest {

    private static final Logger log = Logger.getLogger(GameStateTest.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);

    /**
     * Demonstrates that the class can undergo serialization and maintain it's equivalence.
     */
    @Test
    public void testIsSerializable() {
        GameState gameState = new GameState(new ArrayList<>(), Properties.GAME_ACTION.SELECT_IVANHOE, 1, Properties.COLOR.BLUE, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(gameState);
            byte[] arr = bos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ObjectInputStream ois = new ObjectInputStream(bis);
            GameState serializedGameState = (GameState) ois.readObject();
            assertTrue(serializedGameState.equals(gameState));
            oos.close();
            bis.close();
            ois.close();
            bos.close();
            return;
        } catch (IOException | ClassNotFoundException e) {
            fail(e.getMessage());
        }
        fail();
    }

    @Test
    public void testSerialization() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player p = new Player(i, "Player " + (i + 1));
            p.addCardToDisplay(new Card(i * 10 + i, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            p.addCardToHand(new Card(i * 10 + i + 1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            players.add(p);
        }

        GameState gs = new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 0, Properties.COLOR.BLUE,null);
        try {
            ServerSocket two = new ServerSocket(6000);
            Socket one = new Socket(InetAddress.getLocalHost(), 6000);

            ObjectOutputStream oneOut = new ObjectOutputStream(one.getOutputStream());
            oneOut.writeObject(gs);
            Socket received = two.accept();
            ObjectInputStream ois = new ObjectInputStream(received.getInputStream());
            GameState gs2 = (GameState) ois.readObject();
            assertTrue(gs2.getPlayers().size() == 2);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}