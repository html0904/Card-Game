package ivanhoe.testcases;

import ivanhoe.common.components.Card;
import ivanhoe.common.components.Deck;
import ivanhoe.utils.Properties;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by lee on 08/02/16.
 */
public class DeckTest {
    private static final Logger log = Logger.getLogger(DeckTest.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);


    @Test
    public void testAdd() throws Exception {

        List<Card> list = new LinkedList<>();
        for (int i = 0; i < 110; i++) {
            list.add(new Card(i, 4, Properties.ACTION.ADAPT, Properties.COLOR.BLUE));
        }
        Deck deck = new Deck();
        deck.add(list);
        assertTrue(deck.size() == 110);
    }

    @Test
    public void testDraw() throws Exception {
        List<Card> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new Card(i, 4, Properties.ACTION.ADAPT, Properties.COLOR.BLUE));
        }
        Deck deck = new Deck();
        deck.add(list);
        for (int i = 0; i < 5; i++) {
            assertTrue(deck.draw() != null);
        }
        assertTrue(deck.draw() == null);
    }
}