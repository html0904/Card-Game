package ivanhoe.testScenarios;

import ivanhoe.common.GameState;
import ivanhoe.common.player.PlayerAction;

/**
 * Created by lee on 3/7/2016.
 */
public interface TestScenario {
    GameState getGameState(PlayerAction action);
}
