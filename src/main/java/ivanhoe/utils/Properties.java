package ivanhoe.utils;

/**
 * Created by lee on 06/02/16.
 */
public class Properties {
    public enum COLOR {
        BLUE, GREEN, PURPLE, RED, WHITE, YELLOW, NONE
    }

    /**
     * Used by GameState, ClientConnection and Engine
     */
    public enum GAME_ACTION {

        // Action requests from server
        SELECT_OPPONENT_HAND,
        SELECT_OPPONENT_DISPLAY,
        SELECT_OPPONENT_DISPLAY_CARD,
        SELECT_OPPONENT_DISPLAY_CARD_FOR_OUTWIT,

        SELECT_HAND_CARD,
        SELECT_DISPLAY_CARD,
        SELECT_DISPLAY_CARD_TO_KEEP_FOR_ADAPT,

        SELECT_COLOR,
        SELECT_COLOR_OTHER_THAN_PURPLE,
        SELECT_COLOR_OTHER_THAN_PURPLE_OR_GREEN,
        SELECT_TOKEN_TO_RETURN,

        SELECT_IVANHOE,

        // States sent to server
        PLAYED_IVANHOE,
        CONTINUE,
        WITHDRAW,

        // States received from server
        WIN,
        WIN_GAME,

        //used to shutdown AI players
        TERMINATE,

        //used only for scenarios
        END_OF_TEST
    }

    public enum ACTION {
        /* No actions (color cards and supporters)*/
        NONE,
        /* Color change actions */
        UNHORSE,
        CHANGE_WEAPON,
        DROP_WEAPON,

        /* Basic actions */
        BREAK_LANCE,
        RIPOSTE,
        DODGE,
        RETREAT,
        KNOCKDOWN,
        OUTMANEUVER,
        CHARGE,
        COUNTERCHARGE,
        DISGRACE,
        ADAPT,
        OUTWIT,

        /* Special actions */
        SHIELD,
        STUNNED,
        IVANHOE
    }
}
