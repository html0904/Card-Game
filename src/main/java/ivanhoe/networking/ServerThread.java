package ivanhoe.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by hyunminlee on 2016-02-04.
 */
public class ServerThread extends Thread {

    private int ID = -1;
    private Socket socket = null;
    private Server server = null;
    private String clientAddress = null;
    private ObjectOutputStream objectOut = null;
    private ObjectInputStream objectIn = null;

    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.ID = socket.getPort();
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }

    public void run() {
        System.out.println("Server thread is running");
        while (true) {
            try {
                /** Received a message and pass to the server to handle */
                System.out.println("Server received message from " + ID + " and passing it on to the server to handle it");
                server.handle(ID, objectIn.readObject());
            } catch (Exception ioe) {
                ioe.printStackTrace();
                break;
            }}

    }

    /** sending game state to the client*/
    public void send(Object object) {
        try {
            objectOut.flush();
            objectOut.writeObject(object);
            objectOut.flush();
            objectOut.reset();
        } catch (IOException ioe) {
            System.out.println("Could not send the game state");
            ioe.printStackTrace();
        }
    }

    public void open() throws IOException {
        System.out.println("trying to open object in object out");
        objectOut = new ObjectOutputStream(socket.getOutputStream());
        objectIn = new ObjectInputStream(socket.getInputStream());
        System.out.println("opened object outs");
    }


    public void close() {
        try {
            if (objectOut != null) objectOut.close();
            if (objectIn != null) objectIn.close();
            if (socket != null) socket.close();
            this.socket = null;
            objectOut = null;
            objectIn = null;
            socket = null;

        } catch (IOException e) {
            System.err.print("Error: Could not close socket");
            e.printStackTrace();
        }
    }

    public int getID() {
        return this.ID;
    }

    public String getSocketAddress() {
        return clientAddress;
    }
}
