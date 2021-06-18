package MonkeyTests.strategy;

import com.google.common.base.Preconditions;

import Maze.Board;
import Maze.Character;
import Maze.Chip;

/**
 * Description: <br/>
 * Strategy for the chip. This strategy use random movements to play the Chip on the
 * Board.
 * 
 * When apply this, both Chip and Bug(if there is a Bug) are move randomly.
 * 
 * @author Yun Zhou 300442776
 */
public class RandomMoveStrategyBoth implements MovementStrategy {

    /**
     * When apply this, only Chip can be used as argument, bug will be invoked inside.
     * 
     * @see MonkeyTests.strategy.MovementStrategy#applyMoves(Maze.Board, Maze.Character,
     *      java.lang.String)
     */
    @Override
    public boolean applyMoves(Board board, Character character, String directions) {
        // only chip is accetable for this strategy
        Preconditions.checkArgument(character instanceof Chip);

        // define the range
        int max = 4;
        int min = 1;
        int range = max - min + 1;

        // generate random numbers within 1 to 4(including)
        int randomNumber = (int) (Math.random() * range) + min;
        // check if the random value is in the range
        assert randomNumber >= 1 && randomNumber <= 4;
        assert randomNumber == 1 || randomNumber == 4 || randomNumber == 2
                || randomNumber == 3;

        /*
         * assign the direction and apply the move
         */
        // assign direction
        String dir = "";
        if (randomNumber == 1) {
            dir = "up";
        } else if (randomNumber == 2) {
            dir = "left";
        } else if (randomNumber == 3) {
            dir = "right";
        } else if (randomNumber == 4) {
            dir = "down";
        }
        // apply it
        return board.moveChip(dir);
    }

}
