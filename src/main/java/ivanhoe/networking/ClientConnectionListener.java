package ivanhoe.networking;

import java.io.ObjectInputStream;

/**
 * Created by hyunminlee on 2016-03-06.
 */
public class ClientConnectionListener implements Runnable{
    private ClientConnection cc;
    private ObjectInputStream objectInput;

    public ClientConnectionListener(ClientConnection cc, ObjectInputStream oI) {
        this.cc = cc;
        objectInput = oI;
    }

    @Override

    public void run() {
        while (true) {
            try {
                /** Received a message and pass to the server to handle */
                System.out.println("Waiting for a message");
                Object incoming = objectInput.readObject();
                cc.handle(incoming);
            } catch (Exception ioe) {
                break;
            }
        }

    }
}
