package ivanhoe.utils;

import ivanhoe.client.ClientConnectionTest;
import ivanhoe.common.GameState;
import ivanhoe.common.Tournament;
import ivanhoe.common.components.Card;
import ivanhoe.common.components.Deck;
import ivanhoe.common.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan on 2/19/2016.
 */
public class TestStateProvider {

    private static Deck masterDeck;

    /**
     * Generates a vanilla game state for display testing purposes
     * @return the game state
     */
    public static GameState generateGameState() {

        Card redThree = new Card(Card.getImageIDFromName("redThree"), 3, Properties.ACTION.NONE, Properties.COLOR.RED);
        Card greenOne = new Card(Card.getImageIDFromName("greenOne"), 1, Properties.ACTION.NONE, Properties.COLOR.GREEN);
        Card yellowFour = new Card(Card.getImageIDFromName("yellowFour"), 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW);

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Player p = new Player(i, "Player" + i);
            p.getHand().add(redThree);
            p.getHand().add(greenOne);
            p.getHand().add(yellowFour);
            p.getHand().add(yellowFour);
            p.getHand().add(greenOne);
            p.getHand().add(greenOne);

            p.getDisplay().add(redThree);
            p.getDisplay().add(redThree);

            players.add(p);
        }

        players.get(3).setInTournament(false);

        players.get(1).getDisplay().add(redThree);
        return new GameState(players, Properties.GAME_ACTION.SELECT_HAND_CARD, 1, Properties.COLOR.RED, null);
    }

    /**
     * Generates a game state based on the provided scenario
     * @param scenario the scenario
     * @return the game state corresponding to that scenario test
     */
    public static Tournament generateTournament(ClientConnectionTest.SCENARIO scenario){

        masterDeck = new Deck();
        masterDeck.add(new Card(10000, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        switch (scenario){
            case UNHORSE:
                return createUnhorseTournament();
            case CHANGEWEAPON:
                return createChangeWeaponTournament();
            case DROPWEAPON:
                return createDropWeaponTournament();
            case BREAKLANCE:
                return createBreakLanceTournament();
            case RIPOSTE:
                return createRiposteTournament();
            case DODGE:
                return createDodgeTournament();
            case RETREAT:
                return createRetreatTournament();
            case KNOCKDOWN:
                return createKnockdownTournament();
            case OUTMANEUVER:
                return createOutmaneuverTournament();
            case CHARGE:
                return createChargeTournament();
            case COUNTERCHARGE:
                return createCounterchargeTournament();
            case ADAPT:
                return createAdaptTournament();
            case DISGRACE:
                return createDisgraceTournament();
            case OUTWIT:
                return createOutwitTournament();
            case SHIELD:
                return createShieldTournament();
            case STUNNED:
                return createStunnedTournament();
            case IVANHOE:
                return createIvanhoeTournament();
            default:
                return createRiposteTournament();
        }
    }

    /**
     * Creates and returns the tournament for the ivanhoe scenario
     * @return the tournament
     */
    private static Tournament createIvanhoeTournament() {
        List<Player> players = new ArrayList<>();

        players.add(new Player(0, "Client"));
        players.add(new Player(1, "Opponent"));


        players.get(0).addCardToHand(new Card(99999, 0, Properties.ACTION.IVANHOE, Properties.COLOR.NONE));
        for (int i = 1; i < 3; i++) {
            players.get(0).addCardToDisplay(new Card(i, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
            players.get(0).addCardToDisplay(new Card(i + 10, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            players.get(1).addCardToDisplay(new Card(i + 10, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
            players.get(1).addCardToHand(new Card(i + 1000, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        }

        Card disgrace = new Card(69, 0, Properties.ACTION.DISGRACE, Properties.COLOR.NONE);
        players.get(1).addCardToHand(disgrace);
        Tournament t = new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 1);
        t.setLastCardPlayed(disgrace);
        return t;
    }

    /**
     * Creates and returns the game state for the stunned scenario
     * @return the tournament
     */
    private static Tournament createStunnedTournament() {
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(2, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p1.addCardToHand(new Card(44, 3, Properties.ACTION.NONE, Properties.COLOR.RED));
        p1.addCardToHand(new Card(6, 0, Properties.ACTION.DROP_WEAPON, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(3, 0, Properties.ACTION.STUNNED, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(7, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        p2.addCardToDisplay(new Card(9, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.RED, 0);
    }

    /**
     * Creates and returns the game state for the shield scenario
     * @return the tournament
     */
    private static Tournament createShieldTournament() {
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(1, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(2, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p1.addCardToDisplay(new Card(2, 3, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

        p2.addCardToDisplay(new Card(3, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));
        p2.addCardToDisplay(new Card(4, 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p2.addCardToDisplay(new Card(6, 2, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p2.addCardToDisplay(new Card(5, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        Player p3 = new Player(2, "Opponent2");
        players.add(p3);
        p3.addCardToHand(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToHand(new Card(9, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        p3.addCardToDisplay(new Card(905, 2, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p3.addCardToDisplay(new Card(907, 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.YELLOW, 0);
    }

    /**
     * Creates and returns the game state for the outwit scenario
     * @return the tournament
     */
    private static Tournament createOutwitTournament() {
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(1, 0, Properties.ACTION.OUTWIT, Properties.COLOR.NONE));
        p1.addCardToHand(new Card(8, 0, Properties.ACTION.OUTWIT, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(2, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p1.addCardToDisplay(new Card(2, 3, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p1.addCardToDisplay(new Card(7, 0, Properties.ACTION.STUNNED, Properties.COLOR.NONE));

        p2.addCardToDisplay(new Card(3, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));
        p2.addCardToDisplay(new Card(4, 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p2.addCardToDisplay(new Card(5, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p2.addCardToDisplay(new Card(6, 2, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.YELLOW, 0);
    }

    /**
     * Creates and returns the game state for the drop weapon scenario
     * @return the tournament
     */
    private static Tournament createDropWeaponTournament() {
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.DROP_WEAPON, Properties.COLOR.NONE));
        p1.addCardToHand(new Card(1, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        p1.addCardToHand(new Card(2, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        p1.addCardToHand(new Card(3, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));
        p1.addCardToDisplay(new Card(4, 3, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p2.addCardToDisplay(new Card(5, 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW));
        p2.addCardToDisplay(new Card(6, 4, Properties.ACTION.NONE, Properties.COLOR.YELLOW));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.YELLOW, 0);
    }

    /**
     * Creates and returns the game state for the change weapon scenario
     * @return the tournament
     */
    private static Tournament createChangeWeaponTournament() {
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.CHANGE_WEAPON, Properties.COLOR.NONE));
        p1.addCardToHand(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.BLUE));
        p1.addCardToHand(new Card(2, 3, Properties.ACTION.NONE, Properties.COLOR.BLUE));
        p1.addCardToDisplay(new Card(3, 3, Properties.ACTION.NONE, Properties.COLOR.RED));
        p2.addCardToDisplay(new Card(4, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        p2.addCardToDisplay(new Card(5, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.RED, 0);
    }

    /**
     * Creates and returns the game state for the unhorse scenario
     * @return the tournament
     */
    private static Tournament createUnhorseTournament() {

        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        List <Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(1, 0, Properties.ACTION.UNHORSE, Properties.COLOR.NONE));
        p1.addCardToHand(new Card(2, 5, Properties.ACTION.NONE, Properties.COLOR.RED));
        p1.addCardToHand(new Card(3, 3, Properties.ACTION.NONE, Properties.COLOR.BLUE));
        p1.addCardToDisplay(new Card(4, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(5, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        Player p3 = new Player(2, "Opponent2");
        players.add(p3);
        for (int i = 0; i < 5; i++)
            p3.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        p3.addCardToDisplay(new Card(905, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(906, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(907, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
    }

    /**
     * Creates and returns the game state for the disgrace scenario
     * @return the tournament
     */
    private static Tournament createDisgraceTournament() {
        List<Player> players = new ArrayList<>();

        //adding players
        for (int i = 0; i < 4; i++) {
            players.add(new Player(i, "Player " + i));
        }

        //add fake cards to opponent hands
        for (int i = 0; i < 5; i++) {
            players.get(1).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(2).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(3).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        //add cards to opponent one hand
        players.get(1).addCardToDisplay(new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(1).addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(1).addCardToDisplay(new Card(3, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        players.get(2).addCardToDisplay(new Card(4, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(2).addCardToDisplay(new Card(54, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));
        players.get(2).addCardToDisplay(new Card(5, 5, Properties.ACTION.NONE, Properties.COLOR.RED));


        players.get(3).addCardToDisplay(new Card(6, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(3).addCardToDisplay(new Card(7, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(3).addCardToDisplay(new Card(8, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        players.get(0).addCardToHand(new Card(9, 0, Properties.ACTION.DISGRACE, Properties.COLOR.NONE));
        players.get(0).addCardToHand(new Card(10, 5, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(0).addCardToHand(new Card(11, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.RED, 0);
    }

    /**
     * Creates and returns the game state for the countercharge scenario
     * @return the tournament
     */
    private static Tournament createCounterchargeTournament() {
        List<Player> players = new ArrayList<>();

        //adding players
        for (int i = 0; i < 4; i++) {
            players.add(new Player(i, "Player " + i));
        }

        //add fake cards to opponent hands
        for (int i = 0; i < 5; i++) {
            players.get(1).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(2).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(3).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        //add cards to opponent one hand
        players.get(1).addCardToDisplay(new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(1).addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(1).addCardToDisplay(new Card(3, 5, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(1).addCardToDisplay(new Card(4, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        players.get(2).addCardToDisplay(new Card(45, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(2).addCardToDisplay(new Card(4, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(2).addCardToDisplay(new Card(54, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));
        players.get(2).addCardToDisplay(new Card(5, 5, Properties.ACTION.NONE, Properties.COLOR.RED));


        players.get(3).addCardToDisplay(new Card(6, 5, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(3).addCardToDisplay(new Card(7, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(3).addCardToDisplay(new Card(8, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        players.get(0).addCardToHand(new Card(9, 0, Properties.ACTION.COUNTERCHARGE, Properties.COLOR.NONE));
        players.get(0).addCardToHand(new Card(10, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.RED, 0);
    }

    /**
     * Creates and returns the game state for the charge scenario
     * @return the tournament
     */
    private static Tournament createChargeTournament() {
        List<Player> players = new ArrayList<>();

        //adding players
        for (int i = 0; i < 4; i++) {
            players.add(new Player(i, "Player " + i));
        }

        //add fake cards to opponent hands
        for (int i = 0; i < 5; i++) {
            players.get(1).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(2).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(3).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        //add cards to opponent one hand
        players.get(1).addCardToDisplay(new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(1).addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(1).addCardToDisplay(new Card(3, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(1).addCardToDisplay(new Card(4, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        players.get(2).addCardToDisplay(new Card(4500, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(2).addCardToDisplay(new Card(4, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(2).addCardToDisplay(new Card(5409, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));
        players.get(2).addCardToDisplay(new Card(5, 5, Properties.ACTION.NONE, Properties.COLOR.RED));


        players.get(3).addCardToDisplay(new Card(6, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(3).addCardToDisplay(new Card(7, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(3).addCardToDisplay(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        players.get(0).addCardToHand(new Card(9, 0, Properties.ACTION.CHARGE, Properties.COLOR.NONE));
        players.get(0).addCardToHand(new Card(10, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.RED, 0);
    }

    /**
     * Creates and returns the game state for the outmaneuver scenario
     * @return the tournament
     */
    private static Tournament createOutmaneuverTournament() {
        List<Player> players = new ArrayList<>();

        //adding players
        for (int i = 0; i < 3; i++) {
            players.add(new Player(i, "Player " + i));
        }

        //add fake cards to opponent hands
        for (int i = 0; i < 5; i++) {
            players.get(1).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
            players.get(2).addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        //add cards to opponent one hand
        players.get(1).addCardToDisplay(new Card(1, 4, Properties.ACTION.NONE, Properties.COLOR.RED));
        players.get(1).addCardToDisplay(new Card(1, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        //add cards to opponent two hand
        players.get(2).addCardToDisplay(new Card(1, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        players.get(2).addCardToDisplay(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        players.get(0).addCardToHand(new Card(100, 0, Properties.ACTION.OUTMANEUVER, Properties.COLOR.NONE));
        players.get(0).addCardToHand(new Card(100, 5, Properties.ACTION.NONE, Properties.COLOR.RED));


        Player p3 = new Player(3, "Opponent2");
        players.add(p3);
        for (int i = 0; i < 5; i++)
            p3.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        p3.addCardToDisplay(new Card(905, 3, Properties.ACTION.NONE, Properties.COLOR.RED));
        p3.addCardToDisplay(new Card(906, 5, Properties.ACTION.NONE, Properties.COLOR.RED));
        p3.addCardToDisplay(new Card(907, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.RED, 0);
    }

    /**
     * Creates and returns the game state for the knockdown scenario
     * @return the tournament
     */
    private static Tournament createKnockdownTournament() {
        List<Player> players;

        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.KNOCKDOWN, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(1, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(2, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p2.addCardToDisplay(new Card(3, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        p2.addCardToHand(new Card(5, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToHand(new Card(6, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToHand(new Card(7, 5, Properties.ACTION.NONE, Properties.COLOR.RED));

        Player p3 = new Player(2, "Opponent2");
        players.add(p3);
        p3.addCardToHand(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToHand(new Card(9, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        p3.addCardToDisplay(new Card(905, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(907, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
    }

    /**
     * Creates and returns the game state for the retreat scenario
     * @return the tournament
     */
    private static Tournament createRetreatTournament() {
        List<Player> players;

        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.RETREAT, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(2, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, i, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        Player p3 = new Player(2, "Opponent2");
        players.add(p3);
        p3.addCardToHand(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToHand(new Card(9, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        p3.addCardToDisplay(new Card(905, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(907, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
    }

    /**
     * Creates and returns the game state for the dodge scenario
     * @return the tournament
     */
    private static Tournament createDodgeTournament() {
        List<Player> players;

        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.DODGE, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(1, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(3, 6, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        Player p3 = new Player(2, "Opponent2");
        players.add(p3);
        p3.addCardToHand(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToHand(new Card(9, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        p3.addCardToDisplay(new Card(906, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToDisplay(new Card(907, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToDisplay(new Card(908, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
    }

    /**
     * Creates and returns the game state for the break lance scenario
     * @return the tournament for the break lance scenario
     */
    private static Tournament createBreakLanceTournament() {
        List<Player> players;
        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(0, 0, Properties.ACTION.BREAK_LANCE, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(1, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(2, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(3, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(4, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        Player p3 = new Player(2, "Opponent2");
        players.add(p3);
        p3.addCardToHand(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToHand(new Card(9, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        p3.addCardToDisplay(new Card(905, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(907, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
    }

    /**
     * Creates and returns the game state for the riposte scenario
     * @return the tournament for the riposte scenario
     */
    private static Tournament createRiposteTournament(){
        List<Player> players;

        Player p1 = new Player(0, "Client");
        Player p2 = new Player(1, "Opponent");
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        p1.addCardToHand(new Card(1, 0, Properties.ACTION.RIPOSTE, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(2, 7, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(3, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(4, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        Player p3 = new Player(2, "Opponent2");
        players.add(p3);
        p3.addCardToHand(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToHand(new Card(9, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        p3.addCardToDisplay(new Card(905, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(906, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(907, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
    }

    /**
     * Creates and returns the game state for the adapt scenario
     * @return the tournament for the riposte scenario
     */
    private static Tournament createAdaptTournament() {
        List<Player> players;

        Player p1 = new Player(0, "Client");
        Player p2 = new Player(2, "Opponent");
        players = new ArrayList<>();
        players.add(p1);


        p1.addCardToHand(new Card(1, 0, Properties.ACTION.ADAPT, Properties.COLOR.NONE));
        p1.addCardToDisplay(new Card(2, 3, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p1.addCardToDisplay(new Card(3, 3, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p1.addCardToDisplay(new Card(11, 3, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p1.addCardToDisplay(new Card(4, 3, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p1.addCardToDisplay(new Card(5, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p1.addCardToDisplay(new Card(6, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p1.addCardToDisplay(new Card(7, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(8, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(9, 4, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p2.addCardToDisplay(new Card(10, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));

        for (int i = 0; i < 5; i++) {
            p2.addCardToHand(new Card(0, 0, Properties.ACTION.NONE, Properties.COLOR.NONE));
        }

        Player p3 = new Player(1, "Opponent2");
        players.add(p3);
        p3.addCardToHand(new Card(8, 2, Properties.ACTION.NONE, Properties.COLOR.WHITE));
        p3.addCardToHand(new Card(9, 1, Properties.ACTION.NONE, Properties.COLOR.GREEN));

        p3.addCardToDisplay(new Card(905, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(905, 5, Properties.ACTION.NONE, Properties.COLOR.PURPLE));
        p3.addCardToDisplay(new Card(907, 0, Properties.ACTION.SHIELD, Properties.COLOR.NONE));


        players.add(p2);

        return new Tournament(players, new Deck(masterDeck), Properties.COLOR.PURPLE, 0);
    }
}
