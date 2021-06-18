package MonkeyTests.strategy;

import com.google.common.base.Preconditions;

import Maze.Board;
import Maze.Character;
import Maze.UserGeneratedCharacter;

/**
 * Description: <br/>
 * This Strategy is for appling random movements on the UserGenerateCharacters which is
 * the Bug.
 * <p>
 * When apply this, only Bug can be used to move.
 * 
 * @author Yun Zhou 300442776
 */
public class RandomMoveStrategyBug implements MovementStrategy {

    @Override
    public boolean applyMoves(Board board, Character character, String directions) {
        // this method requires the type of character arg must be UserGeneratedCharacter
        Preconditions.checkArgument(character instanceof UserGeneratedCharacter);

        /*
         * Define the range, for the Bug, we have noMove option, so the range is 5.
         */
        int max = 5;
        int min = 1;
        int range = max - min + 1;
        // generate random numbers within 1 to 5(including)
        int randomNumber = (int) (Math.random() * range) + min;
        /*
         * apply the move and assign the direction
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
        } else if (randomNumber == 5) {
            dir = "noMove";
        }

        return board.executeMove(dir, character);
    }

}
