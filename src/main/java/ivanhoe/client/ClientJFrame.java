package ivanhoe.client;

import ivanhoe.utils.Config;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * Created by Yan on 2/7/2016.
 */
public class ClientJFrame extends JFrame {

    /** Reference to the client */
    private Client client;
    /** Reference to the GUI */
    private ClientJPanel clientJPanel;

    public ClientJFrame(Client client) {
        this.client = client;

        initializeMenuBar();
        initializePanel();
        initializeCloseHandler();

    }

    /**
     * Initializes the window event handler for when the client closes the window
     */
    private void initializeCloseHandler() {
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                client.closedWindow();
            }
        } );

    }

    /**
     * Initializes the GUI panel and adds it to the window
     */
    private void initializePanel() {
        clientJPanel = new ClientJPanel(client);
        add(clientJPanel);
    }

    /**
     * Initializes the menu bar at the top of the window
     */
    private void initializeMenuBar() {
        createMenuBar();

        setTitle("Ivanhoe");
        setSize(ClientSettings.WINDOW_WIDTH, ClientSettings.WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Creates a menu bar at the top of the window
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File dropdown
        menuBar.add(generateFileMenu());

        // Test dropdown
        menuBar.add(generateTestMenu());

        setJMenuBar(menuBar);
    }

    /**
     * Generates the menu items inside the test menu bar and returns the menu item
     * @return the menu item for Test
     */
    private JMenu generateTestMenu() {
        JMenu testMenu = new JMenu("Test");
        testMenu.setMnemonic(KeyEvent.VK_T);

        // Sample game state
        JMenuItem sampleGameStateMenuItem = new JMenuItem("Sample GameState");
        sampleGameStateMenuItem.setMnemonic(KeyEvent.VK_S);
        sampleGameStateMenuItem.setToolTipText("Generate a sample game state for testing");
        sampleGameStateMenuItem.addActionListener(e -> client.generateSampleGameState());
        testMenu.add(sampleGameStateMenuItem);

        JMenuItem scenarioMenuItem;
        for(ClientConnectionTest.SCENARIO scenario : ClientConnectionTest.SCENARIO.values()){
            String menuTitle = scenario.toString().toLowerCase();
            scenarioMenuItem = new JMenuItem(Character.toUpperCase(menuTitle.charAt(0)) + menuTitle.substring(1));
            scenarioMenuItem.addActionListener(e -> client.generateScenarioGameState(scenario));
            testMenu.add(scenarioMenuItem);
        }

        return testMenu;
    }

    /**
     * Generates the menu items inside the menu bar and returns the menu item
     * @return the menu item for File
     */
    private JMenu generateFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // Connect
        JMenuItem createMenuItem = new JMenuItem("Create a game");
        createMenuItem.setMnemonic(KeyEvent.VK_G);
        createMenuItem.setToolTipText("Create a game");
        createMenuItem.addActionListener(e -> createGameInputDialog());
        fileMenu.add(createMenuItem);

        // Connect
        JMenuItem connectMenuItem = new JMenuItem("Connect to a game");
        connectMenuItem.setMnemonic(KeyEvent.VK_C);
        connectMenuItem.setToolTipText("Connect to the networking");
        connectMenuItem.addActionListener(e -> createServerAddressInputDialog());
        fileMenu.add(connectMenuItem);

        // Exit
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.setToolTipText("Exit application");
        exitMenuItem.addActionListener(e -> client.closedWindow());
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private void createServerAddressInputDialog(){
        Object result = JOptionPane.showInputDialog("Enter server IP:", Config.DEFAULT_HOST);
        if(result != null)
            client.connectToServer((String)result);
    }

    private void createGameInputDialog(){
        String serverIP = JOptionPane.showInputDialog("Enter server IP:", Config.DEFAULT_HOST);
        if(serverIP == null)
            return;

        Object[] selectionValues = {"2", "3", "4", "5"};
        String initialSelection = "2";
        Object selection = JOptionPane.showInputDialog(null, "How many players?", "Create a game", JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);

        if(selection != null) {
            int numberOfPlayers = Integer.parseInt(selection.toString());
            Object[] aiSelectionValues = new Object[numberOfPlayers+1];
            for(int i=0; i<numberOfPlayers+1; i++)
                aiSelectionValues[i] = i + "";
            String aiInitialSelection = aiSelectionValues[0].toString();
            selection = JOptionPane.showInputDialog(null, "How many of those are AI?", "Create a game", JOptionPane.QUESTION_MESSAGE, null, aiSelectionValues, aiInitialSelection);

            if(selection != null)
                client.createAGame(serverIP, numberOfPlayers + "", selection.toString());
        }
    }

    /**
     * Gets a reference to the GUI JPanel
     * @return the client's GUI JPanel
     */
    public ClientJPanel getClientJPanel(){
        return clientJPanel;
    }
}
