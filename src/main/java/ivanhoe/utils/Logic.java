package ivanhoe.utils;

import ivanhoe.common.GameState;
import ivanhoe.common.components.Card;
import ivanhoe.common.player.Player;

/**
 * Created by lee on 4/3/2016.
 */
public class Logic {


    public static boolean isPlayableActionCard(Card card, GameState currentGameState) {

        /* make everything a method call even if its a one line method in order to improve readability */
        switch (card.getAction()) {
            case UNHORSE:
                return canPlayUnhorse(currentGameState);
            case CHANGE_WEAPON:
                return canPlayChangeWeapon(currentGameState);
            case SHIELD:
                return true;
            case STUNNED:
                return true;
            case DROP_WEAPON:
                return canPlayDropWeapon(currentGameState);
            case BREAK_LANCE:
                return canPlayBreakLance(currentGameState);
            case RIPOSTE:
                return atLeastOneDisplayWithOneCard(currentGameState);
            case DODGE:
                //same requirements
                return atLeastOneDisplayWithOneCard(currentGameState);
            case RETREAT:
                return canPlayRetreat(currentGameState);
            case KNOCKDOWN:
                return canPlayKnockdown(currentGameState);
            case OUTMANEUVER:
                return canPlayOutmaneuver(currentGameState);
            case CHARGE:
                return canPlayCharge(currentGameState);
            case COUNTERCHARGE:
                return canPlayCountercharge(currentGameState);
            case DISGRACE:
                return canPlayDisgrace(currentGameState);
            case ADAPT:
                return canPlayAdapt(currentGameState);
            case OUTWIT:
                return canPlayOutwit(currentGameState);
        }
        return false;
    }


    public static boolean atLeastOneDisplayWithOneCard(GameState currentGameState) {
        for (Player p : currentGameState.getPlayers()) {
            if (p.getDisplay().size() >= 1 && p.getID() != currentGameState.getTargetPlayerID()) return true;
        }
        return false;
    }

    public static boolean atLeastOneDisplayWithMoreThanOneCard(GameState currentGameState) {
        for (Player p : currentGameState.getPlayers()) {
            if (p.getDisplay().size() >= 2 && p.getID() != currentGameState.getTargetPlayerID()) return true;
        }
        return false;
    }


    public static boolean canPlayOutwit(GameState currentGameState) {
        boolean playerOK = false;
        boolean atLeastOneOpponentOK = false;
        for (Player p : currentGameState.getPlayers()) {
            if (p.getID() == currentGameState.getTargetPlayerID() && p.getDisplaySize() > 0) playerOK = true;
            if (p.getID() != currentGameState.getTargetPlayerID() && p.getDisplaySize() > 0) {
                atLeastOneOpponentOK = true;
                break;
            }
        }
        return playerOK && atLeastOneOpponentOK;
    }

    public static boolean canPlayAdapt(GameState currentGameState) {
        for (Player p : currentGameState.getPlayers()) {
            int[] values = new int[8];
            for (int i = 0; i < 8; i++) {
                values[i] = 0;
            }
            for (Card c : p.getDisplay().getDisplay()) {
                values[c.getValue()]++;
            }
            for (int num : values) {
                if (num > 1) return true;
            }
        }
        return false;
    }

    public static boolean canPlayDisgrace(GameState currentGameState) {
        for (Player p : currentGameState.getPlayers()) {
            if (p.getDisplaySize() > 1) {
                for (Card c : p.getDisplay().getDisplay()) {
                    if (c.getColor() == Properties.COLOR.WHITE) return true;
                }
            }
        }
        return false;
    }

    public static boolean canPlayCountercharge(GameState currentGameState) {
        int lowestValue = 0;
        //find lowest value of all displays
        for (Player p : currentGameState.getPlayers()) {
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() > lowestValue) lowestValue = c.getValue();
            }
        }

        //make sure at least one change will happen if played (can't take away last card from display)
        for (Player p : currentGameState.getPlayers()) {
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() == lowestValue && p.getDisplaySize() > 1) return true;
            }
        }
        return false;
    }

    public static boolean canPlayCharge(GameState currentGameState) {
        int lowestValue = 10;
        //find lowest value of all displays
        for (Player p : currentGameState.getPlayers()) {
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() < lowestValue) lowestValue = c.getValue();
            }
        }

        //make sure at least one change will happen if played (can't take away last card from display)
        for (Player p : currentGameState.getPlayers()) {
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getValue() == lowestValue && p.getDisplaySize() > 1) return true;
            }
        }
        return false;
    }

    public static boolean canPlayOutmaneuver(GameState currentGameState) {
        for (Player p : currentGameState.getPlayers()) {
            if (p.getDisplaySize() > 1 && currentGameState.getTargetPlayerID() != p.getID()) return true;
        }
        return false;
    }

    public static boolean canPlayKnockdown(GameState currentGameState) {
        for (Player p : currentGameState.getPlayers()) {
            if (p.getHandSize() > 0 && p.getID() != currentGameState.getTargetPlayerID()) return true;
        }
        return false;
    }

    public static boolean canPlayRetreat(GameState currentGameState) {
        return currentGameState.getPlayers().get(currentGameState.getTargetPlayerID()).getDisplay().size() > 0;
    }


    public static boolean canPlayBreakLance(GameState currentGameState) {
        for (Player p : currentGameState.getPlayers()) {
            for (Card c : p.getDisplay().getDisplay()) {
                if (c.getColor() == Properties.COLOR.PURPLE) return true;
            }
        }
        return false;
    }

    public static boolean canPlayDropWeapon(GameState currentGameState) {
        return currentGameState.getTournamentColor() != Properties.COLOR.PURPLE
                && currentGameState.getTournamentColor() != Properties.COLOR.GREEN;
    }

    public static boolean canPlayUnhorse(GameState currentGameState) {
        return currentGameState.getTournamentColor() == Properties.COLOR.PURPLE;
    }

    public static boolean canPlayChangeWeapon(GameState currentGameState) {
        Properties.COLOR color = currentGameState.getTournamentColor();
        return (color == Properties.COLOR.RED
                || color == Properties.COLOR.BLUE
                || color == Properties.COLOR.YELLOW);
    }
}
