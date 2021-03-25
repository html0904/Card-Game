package ivanhoe.networking;

import ivanhoe.client.Client;
import ivanhoe.common.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by hyunminlee on 2016-02-06.
 */
public class ClientConnection implements Runnable {
    private Client client;
    private int ID = 0;
    private String serverIP = null;
    private int serverPort = -1;
    private Socket socket = null;
    private ObjectOutputStream objectOut = null;
    private ObjectInputStream objectIn = null;
    private ClientConnectionListener listener = null;

    public ClientConnection(Client client, String ip, int serverPort) {
        this.client = client;
        this.serverIP = ip;
        this.serverPort = serverPort;
    }

    public void connect() {
        System.out.println("Client connecting to the server...");
        try {
            this.socket = new Socket(serverIP, serverPort);
            this.ID = socket.getLocalPort();

            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());

            System.out.println("Waiting for a response");
            String msg = (String) objectIn.readObject();

            if (msg.equals("Connection Refused")) {
                System.out.println("Maximum number of clients are already connected to server\n " +
                        "Connection has been refused");
                System.exit(1);
            } else {
                client.setPlayerID(Integer.parseInt(msg));
                waitForInput();
                listener = new ClientConnectionListener(this, objectIn);
                new Thread(listener).start();
            }

        } catch (NumberFormatException e) {
            System.out.println("Rejected from the server, maximum number of clients connected\n exiting");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: ClientConnection could not connect to the server");
            e.printStackTrace();
        }
    }

    public void waitForInput() {
            try {
                /** Received a message and pass to the server to handle */
                System.out.println("Waiting for a message");
                handle(objectIn.readObject());
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
    }

    public void temporaryConnection() {
        System.out.println("Client connecting to the server...");
        try {
            this.socket = new Socket(serverIP, serverPort);
            this.ID = socket.getLocalPort();
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.err.println("Error: ClientConnection could not connect to the server");
            e.printStackTrace();
        }
    }

    public synchronized void handle(Object input) {
        client.updateGameState((GameState) input);
    }

    public void send(Object msg) {
        try {
            objectOut.flush();
            objectOut.writeObject(msg);
            objectOut.reset();
            objectOut.flush();
        } catch (IOException ioe) {
            System.out.println("Error: Could not send the message");
        }
    }

    public void exit() {
        try {
            if (objectIn != null) objectIn.close();
            if (objectOut != null) objectOut.close();
            if (socket != null) socket.close();
            this.socket = null;
            objectIn = null;
            objectOut = null;

        } catch(IOException ioe) {
            System.out.println("Error: Could not close down the client");
            ioe.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public Socket getSocket() {
        return this.socket;
    }

}
