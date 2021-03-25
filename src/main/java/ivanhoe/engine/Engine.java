package ivanhoe.engine;

import ivanhoe.common.GameState;
import ivanhoe.common.Tournament;
import ivanhoe.common.TournamentBackup;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Deck;
import ivanhoe.common.components.Token;
import ivanhoe.common.player.Display;
import ivanhoe.common.player.Player;
import ivanhoe.common.player.PlayerAction;
import ivanhoe.utils.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Represents a game session and is responsible for running tournaments and determining winners.
 */
public class Engine {
    private boolean tournamentRunning, tournamentWasPurple;
    private List<Player> players;
    private List<Token> tokenBag;
    private Deck deck;
    private Tournament tournament;


    private int playerNum;

    public Engine(List<Player> clients) {
        tournamentWasPurple = false;
        playerNum = clients.size();
        //make player list
        players = new ArrayList<>();
        players.addAll(clients.stream().collect(Collectors.toList()));

        //make token bag
        tokenBag = new ArrayList<>(25);
        for (int i = 0; i < 5; i++) {
            tokenBag.add(new Token(Properties.COLOR.RED));
            tokenBag.add(new Token(Properties.COLOR.YELLOW));
            tokenBag.add(new Token(Properties.COLOR.BLUE));
            tokenBag.add(new Token(Properties.COLOR.GREEN));
            tokenBag.add(new Token(Properties.COLOR.PURPLE));
        }

        //create the deck
        deck = new Deck();

        populateDeck();

        for (Player p : players) {
            for (int i = 0; i < 8; i++) {
                p.addCardToHand(deck.draw());
            }
        }
    }

    /**
     * Picks tokens, player id corresponds to index of token list. Rigged to always only have one purple token
     *
     * @return token list with index corresponding to player id.
     */
    public List<Token> pickDealer() {

        List<Token> tokenList = new ArrayList<>();

        Random rand = new Random();
        int dealer = rand.nextInt(playerNum);
        for (int i = 0; i < playerNum; i++) {
            if (i == dealer) {
                tokenList.add(new Token(Properties.COLOR.PURPLE));
                continue;
            }
            int choice = rand.nextInt(4);
            switch (choice) {
                case 0:
                    //red
                    tokenList.add(new Token(Properties.COLOR.RED));
                    break;
                case 1:
                    //blue
                    tokenList.add(new Token(Properties.COLOR.BLUE));
                    break;
                case 2:
                    //yellow
                    tokenList.add(new Token(Properties.COLOR.YELLOW));
                    break;
                case 3:
                    //green
                    tokenList.add(new Token(Properties.COLOR.GREEN));
                    break;
                default:
                    //should never be reached
                    break;
            }
        }
        return tokenList;
    }

    public GameState getNextStep(PlayerAction action) {

        //very first action taken. should be called once per game
        if (action == null) {
            List<Token> tokens = pickDealer();
            int x = 0;
            for (Token t : tokens) {
                if (t.getTokenColor() == Properties.COLOR.PURPLE) {
                    x = tokens.indexOf(t);
                    break;
                }
            }
            return new GameState(players, tokens, Properties.GAME_ACTION.SELECT_COLOR, x);
        }

        //if there is no tournament running
        if (!tournamentRunning) {

            //if there is no tournament running and a purple tournament just ended then the token choice is expected
            if (tournamentWasPurple) {
                tournamentWasPurple = false;
                //give player a token of the color they selected
                players.get(action.getPlayerID()).addToken(new Token(action.getColor()));

                //ask player to select the next tournament color
                return new GameState(players, Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE, action.getPlayerID(), Properties.COLOR.PURPLE, null);

                //if no tournament is running then action should be selecting a color for the next tournament.
            } else if (action.getActionTaken() == Properties.GAME_ACTION.SELECT_COLOR
                    || action.getActionTaken() == Properties.GAME_ACTION.SELECT_COLOR_OTHER_THAN_PURPLE) {
                tournamentRunning = true;
                tournament = new Tournament(players, deck, action.getColor(), action.getPlayerID());
                return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, action.getPlayerID(), tournament.getColor(), null);
            }
        }

        //if this point reached then a tournament is in session
        GameState gs = tournament.performAction(action);
        if (gs.getRequestedAction() == Properties.GAME_ACTION.WIN) {

            tournamentRunning = false;

            //need to do this in case ivanhoe was played, do it every time to be safe
            TournamentBackup tournamentBackup = tournament.getSnapshot();
            players = tournamentBackup.getPlayers();
            deck = tournamentBackup.getDeck();
            //empty display to deck
            for(Player p:players){
                deck.add(p.getDisplay().getDisplay());
                p.setDisplay(new Display());
            }

            //add token right away if tournament was not purple
            if (tournamentBackup.getColor() != Properties.COLOR.PURPLE) {
                players.get(gs.getTargetPlayerID()).addToken(new Token(tournamentBackup.getColor()));
                gs.setPlayers(players);
                //tournament was purple
            } else {
                tournamentWasPurple = true;
            }
        }

        int tokens = players.get(gs.getTargetPlayerID()).getPlayerTokens().size();
        //if that seals the deal and the game is won
        if ((playerNum < 4 && tokens == 5) || (playerNum > 3 && tokens == 4))
            return new GameState(players, Properties.GAME_ACTION.WIN_GAME, gs.getTargetPlayerID(), Properties.COLOR.WHITE,tournament.getLastCardPlayed());

        return gs;
    }


    /**
     * Creates an ivanhoe deck according to the card counts given in the ivanhoe instructions
     */
    private void populateDeck() {

        int nextID = 1;

        //change colors
        deck.add(new Card(nextID++, 0, Properties.ACTION.UNHORSE, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.DROP_WEAPON, Properties.COLOR.NONE));

        //special
        deck.add(new Card(nextID++, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.STUNNED, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.IVANHOE, Properties.COLOR.NONE));

        //affect display
        deck.add(new Card(nextID++, 0, Properties.ACTION.BREAK_LANCE, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.DODGE, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.RETREAT, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.OUTMANEUVER, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.CHARGE, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.COUNTERCHARGE, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.DISGRACE, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.ADAPT, Properties.COLOR.NONE));
        deck.add(new Card(nextID++, 0, Properties.ACTION.OUTWIT, Properties.COLOR.NONE));

        //make multiple of 2
        for (int i = 0; i < 2; i++) {

            //purple
            deck.add(new Card(nextID++, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

            //red
            deck.add(new Card(nextID++, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

            //blue
            deck.add(new Card(nextID++, 5, Properties.ACTION.NONE, Properties.COLOR.BLUE));

            //yellow
            deck.add(new Card(nextID++, 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

            //knockdown
            deck.add(new Card(nextID++, 0, Properties.ACTION.KNOCKDOWN, Properties.COLOR.NONE));
        }

        //multiples of 3
        for (int i = 0; i < 3; i++) {

            //riposte
            deck.add(new Card(nextID++, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.NONE));
        }

        //make cards in multiples of 4
        for (int i = 0; i < 4; i++) {

            //purple
            deck.add(new Card(nextID++, 3, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            deck.add(new Card(nextID++, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            deck.add(new Card(nextID++, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

            //blue
            deck.add(new Card(nextID++, 2, Properties.ACTION.NONE, Properties.COLOR.BLUE));
            deck.add(new Card(nextID++, 3, Properties.ACTION.NONE, Properties.COLOR.BLUE));
            deck.add(new Card(nextID++, 4, Properties.ACTION.NONE, Properties.COLOR.BLUE));

            //yellow
            deck.add(new Card(nextID++, 2, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

            //maidens
            deck.add(new Card(nextID++, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        }

        //multiples of 6
        for (int i = 0; i < 6; i++) {

            //sword
            deck.add(new Card(nextID++, 3, Properties.ACTION.NONE, Properties.COLOR.RED));
            deck.add(new Card(nextID++, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        }

        //8 yellow 3
        for (int i = 0; i < 8; i++) {
            deck.add(new Card(nextID++, 3, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

            //squires
            deck.add(new Card(nextID++, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            deck.add(new Card(nextID++, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        }

        //greens
        for (int i = 0; i < 14; i++) {
            deck.add(new Card(nextID++, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        }


    }
}
