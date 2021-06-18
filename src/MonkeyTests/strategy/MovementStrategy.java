package MonkeyTests.strategy;

import Maze.Board;
import Maze.Character;

/**
 * Description: <br/>
 * Different movement strategys to use to move the character (bug/chip) on the Game Board.
 * 
 * @author Yun Zhou 300442776
 */
public interface MovementStrategy {
    /**
     * Description: <br/>
     * Movement strategys, move the specified character (bug/chip) on board with using the
     * specificied strategy.
     * 
     * @author Yun Zhou
     * @param board
     *            the game board
     * @param character
     *            character need to move
     * @param directions
     *            multi movements directions
     * @return true if the last movement is successful
     */
    public boolean applyMoves(Board board, Character character, String directions);
}
