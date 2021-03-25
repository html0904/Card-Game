package ivanhoe.client;

import ivanhoe.common.GameState;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.networking.ClientConnection;
import ivanhoe.utils.Config;
import ivanhoe.utils.TestStateProvider;

import java.awt.*;

public class Client {

    /**
     * Current game state of the client
     */
    protected GameState currentGameState;
    /**
     * ID of the player of this client
     */
    protected int playerID;
    /** The client window */
    private ClientJFrame clientJFrame;
    /** The panel (GUI) inside the window */
    private ClientJPanel clientJPanel;
    /** Reference to 'this' */
    private Client that;
    /** used to connect client to server */
    private ClientConnection clientConnection;

    public Client() {
        this.playerID = 1; // hardcoded for testing purposes. DO NOT FORGET!

        that = this;
        initializeClientUI();
    }

    /**
     * Implemented in the name of TDD
     * @param id
     */
    public Client(int id) {
        this.playerID = id; // your mother was a hamster and your father smelled of elderberries.
    }

    /**
     * Runs the client application.
     *
     * @param args command arguments
     */
    public static void main(String[] args) {
        Client client = new Client();
    }

    /**
     * Initializes the window and GUI for the client application's UI.
     */
    private void initializeClientUI() {

        // Start the window up
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                clientJFrame = new ClientJFrame(that);
                clientJFrame.setVisible(true);
                clientJPanel = clientJFrame.getClientJPanel();
            }
        });
    }

    /**
     * Called when the user clicks 'create' in the File dropdown menu.
     * @param numberOfPlayers indicates the number of players in a string array where [number of players, number of AI]
     */
    public void createAGame(String serverIP, String numberOfPlayers, String numberOfAI) {
        System.out.println("Player wants to create a game with " + numberOfPlayers + " players where "+ numberOfAI +" are AI.");

        clientConnection = new ClientConnection(this, serverIP, Config.DEFAULT_PORT);
        System.out.println("sending number of players");
        clientConnection.temporaryConnection();

        // TODO: Modify this to send number of players and number of AI as well (numberOfPlayers[1] is number of AI as a String)
        clientConnection.send(numberOfPlayers + "_" + numberOfAI);

        System.out.println("Successfully created game with " + numberOfPlayers + " players.");
        clientConnection.exit();

        connectToServer(serverIP);
    }

    /**
     * Called when the user clicks 'connect' in the File dropdown menu.
     */
    protected void connectToServer(String ipAddress) {
        System.out.println("Client clicked connect");
        clientConnection = new ClientConnection(this, ipAddress, Config.DEFAULT_PORT);
        clientConnection.connect();

    }

    /**
     * Sends a player action through the client connection
     * @param playerAction the action to send
     */
    protected void sendPlayerActionToServer(PlayerAction playerAction){
        System.out.println("\nAttempting to send player action to server.");
        if(clientConnection != null)
            clientConnection.send(playerAction);
        System.out.println("Player action: " + playerAction.getActionTaken().toString().toLowerCase());
    }

    /**
     * Updates the GUI with a GameState object
     * @param gameState the state of the game to be drawn
     */
    public void updateGameState(GameState gameState){
        System.out.println("\nClient received GameState from server");
        System.out.println("    Action - " + gameState.getRequestedAction());
        System.out.println("    Color  - " + gameState.getTournamentColor());
        System.out.println("    Player - " + gameState.getTargetPlayerID() + " (Client is " + playerID + ")");

        this.currentGameState = gameState;
        clientJPanel.repaint();

        if(currentGameState.getTargetPlayerID() != playerID)
            clientJPanel.resetStunned();
    }

    /**
     * Sets the client's player ID and cascades that down all the client threads
     * @param playerID the id to set
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
        clientJPanel.setClientID(playerID);
    }

    /**
     * Generates a sample game state view of the game
     */
    public void generateSampleGameState(){
        playerID = -1;
        updateGameState(TestStateProvider.generateGameState());
    }

    /**
     *
     * @param scenario the scenario to emulate
     */
    public void generateScenarioGameState(ClientConnectionTest.SCENARIO scenario){
        clientConnection = new ClientConnectionTest(this, scenario);
    }

    public void closedWindow() {
        // TODO: Implement and clean closing here
        System.out.println("Sending gameState to the server");
        try {
            sendPlayerStateToServer(currentGameState);
        } catch(Exception e){
            
        }
        System.out.println("Client closed the window");
        System.exit(0);
    }

    /**
     *
     * @param gs the client sends its game state to the server before it closes
     */
    public void sendPlayerStateToServer(GameState gs) {
        if (clientConnection != null) {
            clientConnection.send(gs);
        }
    }
}
