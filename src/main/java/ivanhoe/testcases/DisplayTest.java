package ivanhoe.testcases;

import ivanhoe.common.components.Card;
import ivanhoe.common.player.Display;
import ivanhoe.utils.Properties;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import static org.junit.Assert.assertTrue;


/**
 * Created by lee on 08/02/16.
 */
public class DisplayTest {

    private static final Logger log = Logger.getLogger(DisplayTest.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);

    @Test
    public void testAdd() {
        Display display = new Display();
        Card c1 = new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE);
        Card c2 = new Card(1, 0, Properties.ACTION.SHIELD, Properties.COLOR.WHITE);
        Card c3 = new Card(1, 0, Properties.ACTION.STUNNED, Properties.COLOR.WHITE);
        display.add(c1);
        display.add(c2);
        display.add(c3);
        //regular display
        assertTrue(display.getDisplay().size() == 1);
        assertTrue(display.getDisplay().get(0).equals(c1));

        //special display
        assertTrue(display.getSpecialDisplay().size() == 2);
        assertTrue(display.hasShield());
        assertTrue(display.hasStunned());
    }

    @Test
    public void testGetValue() {
        Display display = new Display();
        Card c1 = new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE);
        Card c2 = new Card(2, 0, Properties.ACTION.SHIELD, Properties.COLOR.WHITE);
        Card c3 = new Card(3, 0, Properties.ACTION.STUNNED, Properties.COLOR.WHITE);
        Card c4 = new Card(4, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE);
        display.add(c1);
        display.add(c2);
        display.add(c3);
        display.add(c4);
        assertTrue(display.getValue() == 9);
    }

    @Test
    public void testGetCard() {
        Display display = new Display();
        Card c1 = new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE);
        Card c2 = new Card(2, 0, Properties.ACTION.SHIELD, Properties.COLOR.WHITE);
        Card c3 = new Card(3, 0, Properties.ACTION.STUNNED, Properties.COLOR.WHITE);
        Card c4 = new Card(4, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE);
        display.add(c1);
        display.add(c2);
        display.add(c3);
        display.add(c4);

        assertTrue(display.removeCard(Properties.ACTION.SHIELD, 0, Properties.COLOR.WHITE).getId() == 2);
        assertTrue(display.removeCard(Properties.ACTION.STUNNED, 0, Properties.COLOR.WHITE).getId() == 3);

        assertTrue(display.removeCard(Properties.ACTION.NONE, 7, Properties.COLOR.PURPLE).getId() == 4);

        assertTrue(display.getSpecialDisplay().size() == 0);
        assertTrue(display.getDisplay().size() == 1);
    }

    @Test
    public void testGetLastPlayed() {
        Display display = new Display();
        Card c1 = new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE);
        Card c2 = new Card(2, 0, Properties.ACTION.SHIELD, Properties.COLOR.WHITE);
        Card c3 = new Card(3, 0, Properties.ACTION.STUNNED, Properties.COLOR.WHITE);
        Card c4 = new Card(4, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE);
        display.add(c1);
        display.add(c2);
        display.add(c3);
        display.add(c4);


        assertTrue(display.getLastPlayed().getId() == 4);
        assertTrue(display.getLastPlayed().getId() == 1);
        assertTrue(display.getLastPlayed() == null);
    }
}