package ivanhoe.testcases;

import ivanhoe.common.components.Card;
import ivanhoe.common.player.Hand;
import ivanhoe.utils.Properties;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import static org.junit.Assert.assertTrue;

/**
 * Created by lee on 09/02/16.
 */
public class HandTest {

    private static final Logger log = Logger.getLogger(HandTest.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);

    @Test
    public void testAdd() throws Exception {
        Hand hand = new Hand();
        assertTrue(hand.getSize() == 0);
        for (int i = 0; i < 5; i++) {
            hand.add(new Card(i, i, Properties.ACTION.NONE, Properties.COLOR.BLUE));
            assertTrue(hand.getSize() == (i + 1));
        }
    }

    @Test
    public void testPickRandom() throws Exception {
        Hand hand = new Hand();

        for (int i = 0; i < 5; i++) {
            hand.add(new Card(i, i, Properties.ACTION.NONE, Properties.COLOR.BLUE));
        }
        int before = hand.getSize();
        hand.pickRandom();
        assertTrue(hand.getSize() == (before - 1));
    }
}