package ivanhoe.testcases;

/**
 * Created by hyunminlee on 2016-02-04.
 */

import ivanhoe.networking.Server;
import ivanhoe.utils.Config;
import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestWatcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AppServerTest {

    private static final Logger log = Logger.getLogger(AppServerTest.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);
    Server server;

    @BeforeClass
    public static void BeforeClass() {
        System.out.println("@BeforeClass: AppServerTest");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("@AfterClass: AppServerTest");
    }

    @Before
    public void setUp() {
        System.out.println("@Before(): AppServerTest");
        server = new Server(Config.DEFAULT_PORT, "test");
    }

    @After
    public void tearDown() {
        System.out.println("@After(): AppServerTest");
        server = null;
    }

    @Test
    public void CreateNewServerSocket() {
        System.out.println("Testing: Creating a new networking socket");
        assertTrue(server.accept(9999));
    }

    @Test
    public void addingPlayerAfterMaximumConnection() {
        System.out.println("Testing: Incrementing a player count if connection is already at maximum players");
        Config.MAX_CLIENTS = 4;
        server.setNumberOfConnectedPlayers(4);
        assertFalse(server.incrementPlayer());
    }

    @Test
    public void addingPlayer() {
        System.out.println("Testing: Incrementing a player count if connection is already at maximum players");
        Config.MAX_CLIENTS = 3;
        server.setNumberOfConnectedPlayers(2);
        assertTrue(server.incrementPlayer());
    }

    @Test
    public void closingServer() {
        System.out.println("Testing: Incrementing a player count if connection is already at maximum players");
        assertTrue(server.exit());
    }

}
