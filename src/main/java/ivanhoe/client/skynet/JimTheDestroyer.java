package ivanhoe.client.skynet;

import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Token;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.utils.Logic;
import ivanhoe.utils.Properties;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * This Ai will be the "hard" one. it will always choose a token color that the opponent does not possess, will play
 * action cards when there is a possibility doing so can win the tournament as opposed to a guarantee and it will always
 * select a token color that the player already posesses. if no oponents have tokens it will select red.
 */
public class JimTheDestroyer extends ArtificialPlayer {

    public JimTheDestroyer(int serverPort, String serverAddress) {
        super(serverPort, serverAddress);
    }

    public JimTheDestroyer(int serverPort, String address, int id) {
        super(serverPort, address, id);
    }

    /**
     * This is where decisions are made as to which actions the ai will take based on the gamestate and will be different
     * for each AI.
     *
     * @param gs gamestate to respond to
     */
    @Override
    public void handleGameState(GameState gs) throws IOException {
        if (gs == null) {
            System.out.println("Artificial player, id: " + id + " received null gamestate.");
            return;
        }

        //not this A.I.s turn: do nothing
        if (gs.getTargetPlayerID() != id) return;

        System.out.println("Jim the destroyer id: " + id + " received action: " + gs.getRequestedAction());

        switch (gs.getRequestedAction()) {
            case SELECT_HAND_CARD:
                handleSelectHandCard(gs);
                break;
            case SELECT_COLOR:
                handleSelectColor(gs);
                break;
            case SELECT_COLOR_OTHER_THAN_PURPLE:
                handleSelectColorOtherThanPurple(gs);
                break;
            case SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN:
                handleSelectColorOtherThanPurpleOrGreen(gs);
                break;
            case SELECT_DISPLAY_CARD:
                handleSelectDisplayCard(gs);
                break;
            case SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT:
                handleCardToKeepForAdapt(gs);
                break;
            case SELECT_IVANHOE:
                handleIvanhoe(gs);
                break;
            case SELECT_OPPONENT_DISPLAY:
                handleSelectOpponentDisplay(gs);
                break;
            case SELECT_OPPONENT_DISPLAY_CARD:
                handleSelectOpponentDisplayCard(gs);
                break;
            case SELECT_OPPONENT_HAND:
                handleSelectOpponentHand(gs);
                break;
            case SELECT_TOKEN_TO_RETURN:
                handleSelectTokenToReturn(gs);
                break;
            case SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT:
                handleSelectOutwitDisplayCard(gs);
                break;
            case WIN:
                handleWin(gs);
            default:
                break;
        }

    }

    /**
     * will steal shield if an opponent has one otherwise will pick the highest value card in the display of the
     * highest total value
     *
     * @param gs
     */
    private void handleSelectOutwitDisplayCard(GameState gs) {
        for (Player p : gs.getPlayers()) {
            if (p.getDisplay().hasShield() && p.getID() != id) {
                try {
                    oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT, id, -1, 4, p.getID(), gs.getTournamentColor()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        //if no shield was present then we look for the highest value card in the highest value display

        int highestDisplayID = -1;
        int displayValue = 0;
        for (Player p : gs.getPlayers()) {
            if (p.getID() != id && p.getDisplay().getValue() > displayValue) {
                highestDisplayID = p.getID();
                displayValue = p.getDisplay().getValue();
            }
        }
        //the highest id should never be -1 since the card could not have been played if all opponent displays were empty
        int cardValue = 0;
        int higestCardID = 0;
        for (Card c : gs.getPlayer(highestDisplayID).getDisplay().getDisplay()) {
            if (c.getValue() > cardValue) {
                cardValue = c.getValue();
                higestCardID = c.getId();
            }
        }
        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT, id, -1, higestCardID, highestDisplayID, gs.getTournamentColor()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will play as many valid cards as possible if the round is winnable
     *
     * @param gs
     */
    private void handleSelectHandCard(GameState gs) {

        //check if there is a playable action card in hand.
        for (Card c : gs.getPlayers().get(id).getHand().getCards()) {

            try {
            if (Logic.isPlayableActionCard(c, gs)) {

                //a playable action card is found, play it.

                if (c.getAction() != Properties.ACTION.IVANHOE)
                    oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, id, c.getId(), -1, -1, gs.getTournamentColor()));
                return;
            }
                } catch (IOException e) {
                    System.out.println("Failed to play card: " + c.getAction() + " id: " + c.getId());
                    e.printStackTrace();
                } catch (NullPointerException e) {
                System.out.println("a null exception was encountered");
                printHand(gs);
                    System.out.println("Failed to check card: " + c.getAction() + " id: " + c.getId());

                }
            }



        //no action cards in hand are playable, check if it is possible to win this round

        int toBeat = valueToBeat(gs);

        int cardsInHand = getValueOfColorAndSupporterCards(gs, gs.getTournamentColor());
        System.out.println("Value to beat: " + toBeat + " value of cards in hand: " + cardsInHand + " display value: " + gs.getPlayer(id).getDisplay().getValue());
        //if this display is the largest and there are no cards left to play, continue.
        if (gs.getPlayers().get(id).getDisplay().getValue() > toBeat && cardsInHand == 0) {
            try {
                oos.writeObject(new PlayerAction(Properties.GAME_ACTION.CONTINUE, id, -1, -1, -1, gs.getTournamentColor()));
                return;
            } catch (IOException e) {
                System.out.println("Could not send continue. id: " + id);
                e.printStackTrace();
            }
        }


        if (cardsInHand + gs.getPlayer(id).getDisplay().getValue() <= toBeat) {
            //withdraw
            try {
                oos.writeObject(new PlayerAction(Properties.GAME_ACTION.WITHDRAW, id, -1, -1, -1, gs.getTournamentColor()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //play card
            for (int i = 0; i < gs.getPlayers().get(id).getHand().getCards().size(); i++) {
                Properties.COLOR color = gs.getPlayers().get(id).getHand().getCards().get(i).getColor();
                if (color == gs.getTournamentColor() || color == Properties.COLOR.WHITE) {
                    try {
                        oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_HAND_CARD, id, gs.getPlayers().get(id).getHand().getCards().get(i).getId(), -1, -1, gs.getTournamentColor()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
        System.out.println("Jim the destroyer reached the end of the handleSelectHandCard() method. This should not happen!");
        printHand(gs);

    }

    /**
     * will select a token color it needs
     * 0-Purple
     * 1-Blue
     * 2-Red
     * 3-Yellow
     * 4-Green
     *
     * @param gs
     */
    @SuppressWarnings("Duplicates")
    private void handleSelectColor(GameState gs) {
        List<Token> tokens = gs.getPlayer(id).getPlayerTokens();
        boolean[] arr = new boolean[5];
        for (int i = 0; i < 5; i++) {
            arr[i] = false;
        }

        tokens.stream().forEach(t -> {
            switch (t.getTokenColor()) {
                case PURPLE:
                    arr[0] = true;
                case BLUE:
                    arr[1] = true;
                case RED:
                    arr[2] = true;
                case YELLOW:
                    arr[3] = true;
                case GREEN:
                    arr[4] = true;
                default:
                    break;
            }
        });
        Properties.COLOR color = Properties.COLOR.PURPLE;
        if (!arr[0]) {
            color = Properties.COLOR.PURPLE;
        } else if (!arr[1]) {
            color = Properties.COLOR.BLUE;
        } else if (!arr[2]) {
            color = Properties.COLOR.RED;
        } else if (!arr[3]) {
            color = Properties.COLOR.YELLOW;
        } else if (!arr[4]) {
            color = Properties.COLOR.GREEN;
        }

        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, id, -1, -1, -1, color));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will select a colortoken it needs
     *
     * @param gs
     */
    private void handleSelectColorOtherThanPurple(GameState gs) {
        List<Token> tokens = gs.getPlayer(id).getPlayerTokens();
        boolean[] arr = new boolean[5];
        for (int i = 0; i < 4; i++) {
            arr[i] = false;
        }

        tokens.stream().forEach(t -> {
            switch (t.getTokenColor()) {
                case BLUE:
                    arr[0] = true;
                case RED:
                    arr[1] = true;
                case YELLOW:
                    arr[2] = true;
                case GREEN:
                    arr[3] = true;
                default:
                    break;
            }
        });
        Properties.COLOR color = Properties.COLOR.BLUE;

        if (!arr[0]) {
            color = Properties.COLOR.BLUE;
        } else if (!arr[1]) {
            color = Properties.COLOR.RED;
        } else if (!arr[2]) {
            color = Properties.COLOR.YELLOW;
        } else if (!arr[3]) {
            color = Properties.COLOR.GREEN;
        }

        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, id, -1, -1, -1, color));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will select the card color it has the most of. or yellow if no cards are available
     *
     * @param gs
     */
    private void handleSelectColorOtherThanPurpleOrGreen(GameState gs) {
        int blue = getValueOfColorAndSupporterCards(gs, Properties.COLOR.BLUE);
        int red = getValueOfColorAndSupporterCards(gs, Properties.COLOR.RED);
        int yellow = getValueOfColorAndSupporterCards(gs, Properties.COLOR.YELLOW);
        Properties.COLOR color = Properties.COLOR.YELLOW;
        if (blue > red && blue > yellow) {
            color = Properties.COLOR.BLUE;
        } else if (red > blue && red > yellow) {
            color = Properties.COLOR.RED;
        }

        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN, id, -1, -1, -1, color));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * always selects the highest display card
     *
     * @param gs
     */
    private void handleSelectDisplayCard(GameState gs) {
        int cardID = -1;
        int val = 0;
        for (Card c : gs.getPlayers().get(id).getHand().getCards()) {
            if (c.getValue() > val && (c.getColor() == gs.getTournamentColor() || c.getColor() == Properties.COLOR.WHITE)) {
                val = c.getValue();
                cardID = c.getId();
            }
        }

        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_DISPLAY_CARD, id, cardID, -1, -1, gs.getTournamentColor()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Plays adapt.
     *
     * @param gs
     */
    private void handleCardToKeepForAdapt(GameState gs) {
        boolean[] values = new boolean[8];
        for (Card c : gs.getPlayer(id).getDisplay().getDisplay()) {
            values[c.getValue()]=true;
        }
        values[0] = false;
        int count = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i]) count++;
        }
        System.out.println("There are " + count + " cards to be selected");
        for (int i = 1; i < 8; i++) {
            if (!values[i]) continue;
            count--;
            System.out.println("Processing card of value " + i);
            //find card of appropriate value and send. we are keeping the first appropriate card encountered for simplicity
            for (int j = 0; j < gs.getPlayer(id).getDisplay().getDisplay().size(); j++) {
                if (gs.getPlayer(id).getDisplay().getDisplay().get(j).getValue() == i) {
                    try {
                        oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT, id,
                                gs.getPlayer(id).getDisplay().getDisplay().get(j).getId(), -1, -1, gs.getTournamentColor()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            //if this was not the last value needed we listen and ignore the incomming gamestate as it will be another request
            if (count > 0) {
                try {
                    ois.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Always use ivanhoe
     *
     * @param gs
     */
    private void handleIvanhoe(GameState gs) {
        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.PLAYED_IVANHOE, id, -1, -1, -1, gs.getTournamentColor()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * selects the opponent with the highest total value display. shouldn't happen on empty displays
     *
     * @param gs
     */
    private void handleSelectOpponentDisplay(GameState gs) {

        int opponent = -1;
        int value = 0;
        for (Player p : gs.getPlayers()) {
            if (p.getID() == id) continue;
            if (p.getDisplay().getValue() > value) {
                opponent = p.getID();
                value = p.getDisplay().getValue();
            }
        }

        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY, id, -1, -1, opponent, gs.getTournamentColor()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * always select the highest opponent display card
     *
     * @param gs
     */
    private void handleSelectOpponentDisplayCard(GameState gs) {
        int opponent = -1;
        int value = 0;
        int card = -1;
        for (Player p : gs.getPlayers()) {
            if (p.getID() == id) continue;
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() > value) {
                    value = c.getValue();
                    opponent = p.getID();
                    card = c.getId();
                }
            }
        }

        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_DISPLAY_CARD, id, -1, card, opponent, gs.getTournamentColor()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Select hand of opponent randomly.
     *
     * @param gs
     */
    private void handleSelectOpponentHand(GameState gs) {
        Random rand = new Random();
        int dx = id;
        while (dx == id) {
            dx = rand.nextInt(gs.getPlayers().size());
        }
        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_OPPONENT_HAND, id, -1, -1, dx, gs.getTournamentColor()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param gs
     */
    @SuppressWarnings("Duplicates")
    private void handleSelectTokenToReturn(GameState gs) {
        List<Token> tokens = gs.getPlayer(id).getPlayerTokens();
        boolean[] arr = new boolean[5];
        for (int i = 0; i < 5; i++) {
            arr[i] = false;
        }

        tokens.stream().forEach(t -> {
            switch (t.getTokenColor()) {
                case PURPLE:
                    arr[0] = true;
                case BLUE:
                    arr[1] = true;
                case RED:
                    arr[2] = true;
                case YELLOW:
                    arr[3] = true;
                case GREEN:
                    arr[4] = true;
                default:
                    break;
            }
        });
        Properties.COLOR color = Properties.COLOR.PURPLE;
        if (!arr[4]) {
            color = Properties.COLOR.GREEN;
        } else if (!arr[3]) {
            color = Properties.COLOR.YELLOW;
        } else if (!arr[2]) {
            color = Properties.COLOR.RED;
        } else if (!arr[1]) {
            color = Properties.COLOR.BLUE;
        } else if (!arr[0]) {
            color = Properties.COLOR.PURPLE;
        }

        try {
            oos.writeObject(new PlayerAction(Properties.GAME_ACTION.SELECT_COLOR, id, -1, -1, -1, color));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleWin(GameState gs) {
        handleSelectColor(gs);
    }

    private void printHand(GameState gs) {
        System.out.println("Hand contained " + gs.getPlayer(id).getHand().getSize() + " cards:");
        for (Card c : gs.getPlayer(id).getHand().getCards()) {
            System.out.println("action: " + c.getAction() + " color: " + c.getColor() + " value: " + c.getValue() + " id: " + c.getId());
        }
}
}
