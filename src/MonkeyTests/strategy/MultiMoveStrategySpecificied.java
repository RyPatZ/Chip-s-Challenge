package MonkeyTests.strategy;

import com.google.common.base.Preconditions;

import Maze.Board;
import Maze.Character;

/**
 * Description: <br/>
 * This strategy is the multiMoveStrategy, parse the argument of the String to characters in
 * order to apply all movements.Both Chip and Bug can use this strategy.
 * <p>
 * When apply this strategy, only the specificied character can move.
 * 
 * @author Yun Zhou 300442776
 */
public class MultiMoveStrategySpecificied implements MovementStrategy {

    @Override
    public boolean applyMoves(Board board, Character character, String directions) {
        // directions should not be NULL!
        Preconditions.checkNotNull(directions);

        boolean isValid = false;
        // parse the string input and apply the movements times by times
        for (int i = 0; i < directions.length(); i++) {
            char c = directions.charAt(i);
            String move = "";
            if (c == 'W' || c == 'w') {
                move = "up";
            } else if (c == 'A' || c == 'a') {
                move = "left";
            } else if (c == 'S' || c == 's') {
                move = "down";
            } else if (c == 'D' || c == 'd') {
                move = "right";
            } else if (c == 'N' || c == 'n') {
                move = "noMove";
            } else {
                System.err.println(
                        "Find a invalid movement character: " + c + ".\nJump to apply next char.");
                continue;

            }

            isValid = board.executeMove(move, character);
        }

        return isValid;

    }

}
