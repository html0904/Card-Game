package ivanhoe.networking;

import ivanhoe.utils.Config;

import java.util.Scanner;

/**
 * Created by hyunminlee on 2016-02-06.
 */

public class StartServer {
    private static boolean finished = false;
    private static Server server = null;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Staring the server");

        server = new Server(Config.DEFAULT_PORT);


        while (Boolean.TRUE) {
            System.out.println("Enter quit to shut down networking");
            String input = sc.nextLine();

            if (input.equals("quit")) {
                server.shutdown();
                System.exit(1);
            }
        }

    }

}
