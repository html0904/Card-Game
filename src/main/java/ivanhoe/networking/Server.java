package ivanhoe.networking;

import ivanhoe.client.skynet.JimTheDestroyer;
import ivanhoe.client.skynet.WillardTheWithdrawer;
import ivanhoe.common.components.Card;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.common.GameState;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.engine.Engine;
import ivanhoe.utils.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lee on 03/02/16.
 */

public class Server implements Runnable {

    private int numberOfConnectedPlayers;
    private Engine engine;
    private boolean done;
    private boolean numPlayersSet;

    private ServerSocket server = null;
    private Thread thread = null;
    private ObjectOutputStream objectOut = null;
    private ObjectInputStream objectIn = null;
    private HashMap<Integer, ServerThread> mapOfClients;
    private List<Player> listOfClients;
    private int idCounter = 0;
    private ExecutorService exec;
    private int AINumPlayer;

    public Server(int port) {
        start(port);
        numberOfConnectedPlayers = 0;
        mapOfClients = new HashMap<Integer, ServerThread>();
        done = false;
    }

    public Server(int port, String s) {
        exec = Executors.newCachedThreadPool();
        numberOfConnectedPlayers = 0;
        mapOfClients = new HashMap<Integer, ServerThread>();
        done = false;
        try {
            System.out.println("Binding to port:" + port);
            server = new ServerSocket(port);
            server.setReuseAddress(true);
        } catch (IOException e) {

        }
    }

    public boolean accept(int port) {
        try {

            System.out.println("Binding to port:" + port);
            server = new ServerSocket(port);
            server.setReuseAddress(true);
            Config.MAX_CLIENTS = numberOfConnectedPlayers;

            threadStart();
            return true;
        } catch (IOException ioe) {
            System.err.print("IO Exception Error: Cannot open socket");
            ioe.printStackTrace();
            return false;
        }
    }

    public boolean incrementPlayer() {
        if (numberOfConnectedPlayers < Config.MAX_CLIENTS) {
            numberOfConnectedPlayers++;
            return true;
        }
        return false;
    }

    public boolean start(int port) {
        try {

            System.out.println("Binding to port:" + port);
            server = new ServerSocket(port);
            server.setReuseAddress(true);

            while (!numPlayersSet) {
                Config.MAX_CLIENTS = getNumberOfPlayers(server);
            }

            threadStart();
            return true;
        } catch (IOException ioe) {
            System.err.print("IO Exception Error: Cannot open socket");
            ioe.printStackTrace();
            return false;
        }
    }

    /** opens up connection to get number of players from the first client*/

    public int getNumberOfPlayers(ServerSocket server) {

        try {
            Socket socket = server.accept();

            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
            String raw = (String) objectIn.readObject();
            System.out.println("Raw string: " + raw);
            String[] info = raw.split("_");
            int players = Integer.parseInt(info[0]);
            AINumPlayer = Integer.parseInt(info[1]);

            System.out.println("players: " + players);
            System.out.println("AI players: " + AINumPlayer);


            numPlayersSet = true;
            //closing off unnecessary connections

            objectIn.close();
            objectOut.close();
            objectIn = null;
            objectOut = null;

            socket.close();

            return players;

        } catch (Exception e) {
            System.out.println("Error: Could not open input/output stream to  get number of players");
            e.printStackTrace();
        }

        return 0;
    }

    public boolean threadStart() {
        if (thread == null) {
                thread = new Thread(this);
                thread.start();
                return true;
            } else {
            return false;
        }

    }

    public void run() {
        while (thread != null) {
            try {

                for (int i = 0; i < AINumPlayer; i++) {
                    exec.execute(() -> {
                        JimTheDestroyer jim = new JimTheDestroyer(Config.DEFAULT_PORT, Config.DEFAULT_HOST);
                        jim.enterNewGame();
                    });
                }

                    if (numberOfConnectedPlayers <= Config.MAX_CLIENTS) {
                        System.out.println("Waiting for players to join");
                        createThread(server.accept());
                    }

                    if (numberOfConnectedPlayers == Config.MAX_CLIENTS) {
                        System.out.println("maximum number players connected game commencing");
                        startGame();
                }
            } catch (IOException e) {
                System.err.print("Error: Thread could not be added or server shut down");
                e.printStackTrace();
            }
        }
    }

    public void startGame() {

        if (mapOfClients != null) {
            listOfClients = generateListOfPlayers(mapOfClients);
        } else {
            System.out.println("Error: Client hash map is null");
        }
        engine = new Engine(listOfClients);

        System.out.println("sending game state to players");

        sendGameState(engine.getNextStep(null));
    }

    public void sendGameState(GameState gameState) {
        Set<Integer> keys = mapOfClients.keySet();
        for (Integer key : keys) {
            mapOfClients.get(key).send(gameState);
        }
    }

    public List<Player> generateListOfPlayers(HashMap<Integer, ServerThread> map) {
        System.out.println("Creating players on the server");

        int playerID = 0;
        listOfClients = new ArrayList<>();
        for (Integer key : map.keySet())
            listOfClients.add(new Player(playerID++, map.get(key).getSocketAddress()));

        return listOfClients;
    }

    private void createThread(Socket socket) {
            try {
                ServerThread serverThread = new ServerThread(this, socket);
                System.out.println("Creating new socket");
                serverThread.open();
                System.out.println("Opening the socket");
                serverThread.start();
                System.out.println("Starting the socket");

                if (numberOfConnectedPlayers < Config.MAX_CLIENTS) {
                    System.out.println("Accepting a connection");
                    mapOfClients.put(serverThread.getID(), serverThread);
                    incrementPlayer();
                    serverThread.send(Integer.toString(idCounter));
                    idCounter++;
                } else {
                    System.out.println("maximum number of players connected");
                    serverThread.send("Connection Refused\n");
                    serverThread.close();
                }

            } catch (IOException e) {
                System.err.print("Error: could not open new thread for a client");
                e.printStackTrace();
            }
    }

    public int getNumberOfConnectedPlayers() {
        return mapOfClients.size();
    }

    public void setNumberOfConnectedPlayers(int i) {
        numberOfConnectedPlayers = i;
    }

    public void shutdown() {
        thread = null;
        try {
            server.close();
        } catch (IOException e) {
            System.err.print("Error: could not shut down networking properly");
            e.printStackTrace();
        }

    }

    public synchronized void remove(int ID) {
        if (mapOfClients.containsKey(ID)) {
            ServerThread threadToClose = mapOfClients.get(ID);
            mapOfClients.remove(ID);
            numberOfConnectedPlayers--;
            threadToClose.close();
            threadToClose = null;
        }
    }

    public synchronized void handle(int ID, Object object) {
        System.out.println("Server received message from " + ID + " with " + object.getClass() + " class");

        if (object.getClass().equals(String.class)) {
            if (object.equals("quit")) {
                if (mapOfClients.containsKey(ID)) {
                    mapOfClients.get(ID).send("quit\n");
                    remove(ID);
                }
            }

        } else if (object.getClass().equals(GameState.class)){
            System.out.println("Creating AI to take over disconnected player");
            //removes client from the Hashmap
            if (mapOfClients.containsKey(ID)) {
                remove(ID);
            }
            WillardTheWithdrawer ai = new WillardTheWithdrawer(Config.DEFAULT_PORT, Config.DEFAULT_HOST, ID);

            ai.enterExistingGame((GameState) object);

        } else if (object.getClass().equals(PlayerAction.class)) {
            PlayerAction action = (PlayerAction) object;
            System.out.println(String.format("\nReceived player action %s, color %s, card %s", action.getActionTaken().toString(), action.getColor(), Card.cardIDMap.get(action.getCardPlayed())));
            GameState nextState = engine.getNextStep(action);

            System.out.println(String.format("Sending game state with action %s, color %s, target player %d", nextState.getRequestedAction().toString(), nextState.getTournamentColor(), nextState.getTargetPlayerID()));
            sendGameState(nextState);
        }
    }

    public boolean exit() {
        Set<Integer> keys = mapOfClients.keySet();

        if (thread != null) {
            thread = null;
        }

        try {
            for (Integer key : keys) {
                mapOfClients.get(key).close();
                mapOfClients.put(key, null);
            }
            mapOfClients.clear();
            if (server != null) {
                server.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
