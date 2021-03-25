package ivanhoe.client.skynet;

import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.player.Player;
import ivanhoe.utils.Properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Abstract class for all future AIs. if some code is repeated, place here.
 */
public abstract class ArtificialPlayer {

    /**
     * id to check against in gamestates
     */
    protected int id;
    /**
     * Having the streams as globals saves initializing them every round
     */
    protected ObjectOutputStream oos;
    protected ObjectInputStream ois;
    /**
     * keeping socket to be able to close it
     */
    private Socket socket;
    private String name;

    /**
     * Basic constructor for any artificial player. will make its own socket in order to communicate with the server
     * without the need for complex method calls.
     *
     * @param serverPort    port to connect to
     * @param serverAddress server address
     */
    public ArtificialPlayer(int serverPort, String serverAddress) {

        name = "default";
        try {
            socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When Id is known at creation (i.e. replacing a disconnected player)
     *
     * @param serverPort port to connect to
     * @param address    server address
     * @param id         id to replace
     */
    public ArtificialPlayer(int serverPort, String address, int id) {
        this(serverPort, address);
        this.id = id;
    }


    /**
     * Called from server when AI is initiated at the beginning of a match. This should only ever be called when there
     * is a match to join
     */
    public void enterNewGame() {
        try {
            oos.writeObject("T800-requesting to fight the humans");

            //making string for debugging quality of life
            String s = (String) ois.readObject();
            id = Integer.parseInt(s);

            //starts the game
            run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Could not process initial server response. T800 signing out.");
            e.printStackTrace();
        }

    }

    /**
     * Called from the server when AI is initiated during a match.
     *
     * @param gs gamestate received from server on the round the AI was initiated.
     */
    public void enterExistingGame(GameState gs) {
        try {
            handleGameState(gs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        run();
    }


    /**
     * play as long as there is a server connection and the game is not won. To close the Server should send a
     * WIN_GAME to this AI
     */
    protected void run() {

        while (true) {
            try {
                Object obj = ois.readObject();
                GameState gs = null;
                try {
                    gs = (GameState) obj;
                } catch (ClassCastException e) {
                    System.out.println(obj.toString());
                    e.printStackTrace();
                }

                //only normal condition where the AI should quit.
                if (gs.getRequestedAction() == Properties.GAME_ACTION.WIN_GAME
                        || gs.getRequestedAction() == Properties.GAME_ACTION.TERMINATE) break;

                //slow down the AI so humans can follow it
                Thread.sleep(1000);
                handleGameState(gs);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        close();
        System.out.println("AI player \"" + name + "\" is now closing down.");
    }

    /**
     * Close open resources
     */
    protected void close() {
        try {
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a color of token needed by the AI to win. Will select in order
     *
     * @param gameState
     * @return
     */
    protected Properties.COLOR getNeededColor(GameState gameState) {
        return null;
    }

    /**
     * Gets a color that the selected payer already owns. Returns red if opponent has no tokens
     *
     * @param opponentID
     * @param gameState
     * @return
     */
    protected Properties.COLOR getColorAlreadyOwnedByOpponent(int opponentID, GameState gameState) {
        if (gameState.getPlayers().get(opponentID).getPlayerTokens().size() > 0)
            return gameState.getPlayers().get(opponentID).getPlayerTokens().get(0).getTokenColor();
        return Properties.COLOR.RED;
    }

    /**
     * returns the color of the highest valued cards. So if theres two blue cards of value 2 and one purple of value 7
     * it will select purple. Will return white if no color cards are found.
     * <p/>
     * 0->PURPLE
     * 1->RED
     * 2->BLUE
     * 3->YELLOW
     * 4->GREEN
     *
     * @return most plentiful color
     */
    protected Properties.COLOR getMostPossesedColor(GameState gameState) {
        int[] totals = new int[5];
        for (int i = 0; i < 5; i++) {
            totals[i] = 0;
        }
        for (Card c : gameState.getPlayers().get(id).getHand().getCards()) {
            switch (c.getColor()) {
                case PURPLE:
                    totals[0] += c.getValue();
                    break;
                case RED:
                    totals[1] += c.getValue();
                    break;
                case BLUE:
                    totals[2] += c.getValue();
                    break;
                case YELLOW:
                    totals[3] += c.getValue();
                    break;
                case GREEN:
                    totals[4] += c.getValue();
                    break;
            }
        }

        //get the index with the highest value
        int dx = -1;
        int highest = 0;
        for (int i = 0; i < 5; i++) {
            if (totals[i] > highest) {
                highest = totals[i];
                dx = i;
            }
        }

        //return the color corresponding to the highest value
        switch (dx) {
            case 0:
                return Properties.COLOR.PURPLE;
            case 1:
                return Properties.COLOR.RED;
            case 2:
                return Properties.COLOR.BLUE;
            case 3:
                return Properties.COLOR.YELLOW;
            case 4:
                return Properties.COLOR.GREEN;
            default:
                return Properties.COLOR.WHITE;
        }
    }

    /**
     * Returns the value of the highest display in game that is not the current player
     *
     * @param gameState
     * @return
     */
    protected int valueToBeat(GameState gameState) {
        int count = 0;
        for (Player p : gameState.getPlayers()) {
            if (p.getID() == id) continue;
            if (p.getDisplay().getValue() > count) count = p.getDisplay().getValue();
        }
        return count;
    }

    /**
     * Returns the total value of the cards of the specified color
     *
     * @param gameState
     * @return
     */
    protected int getValueOfColorCards(GameState gameState, Properties.COLOR color) {
        if (gameState == null) return -1;
        int count = 0;
        for (Card c : gameState.getPlayers().get(id).getHand().getCards()) {
            if (c.getColor() == color)
                if (color == Properties.COLOR.GREEN) {
                    count++;
                } else {
                    count += c.getValue();
                }
        }
        return count;
    }

    /**
     * Returns the total value of the cards of the same color as the tournament and supporters currently in the
     * players hand
     *
     * @param gameState
     * @return
     */
    protected int getValueOfColorAndSupporterCards(GameState gameState, Properties.COLOR color) {

        if (gameState == null) return -1;
        int count = 0;
        for (Card c : gameState.getPlayers().get(id).getHand().getCards()) {
            if (c.getColor() == color || c.getColor() == Properties.COLOR.WHITE)
                if (color == Properties.COLOR.GREEN) {
                    count++;
                } else {
                    count += c.getValue();
                }
        }
        return count;
    }

    /**
     * Checks if its possible to win using only color cards
     *
     * @param gameState
     * @return
     */
    protected boolean colorCardWin(GameState gameState) {
        if (gameState == null) return false;
        return valueToBeat(gameState) < getValueOfColorCards(gameState, gameState.getTournamentColor());
    }

    /**
     * Checks if it's possible to win using color and supporter cards
     *
     * @param gameState
     * @return
     */
    protected boolean colorCardAndSupporterWin(GameState gameState) {
        if (gameState == null) return false;
        return valueToBeat(gameState) < getValueOfColorAndSupporterCards(gameState, gameState.getTournamentColor());
    }

    /**
     * This is where decisions are made as to which actions the ai will take based on the gamestate and will be different
     * for each AI.
     *
     * @param gs gamestate to respond to
     */
    public abstract void handleGameState(GameState gs) throws IOException;
}
