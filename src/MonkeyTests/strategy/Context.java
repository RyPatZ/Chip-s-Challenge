package MonkeyTests.strategy;

import Maze.Board;
import Maze.Character;

/**
 * Description: <br/>
 * The context for the strategy.This class is designed for only using the strategy, only
 * responsible for this.
 * 
 * @author Yun Zhou 300442776
 */
public class Context {
    /**
     * The strategy to be used.
     */
    private MovementStrategy strategy;

    /**
     * A constructor. It construct a new instance of Context. Within use the constructor,
     * can be able to swap different strategy to be able to move the Character.
     *
     * @param strategy
     *            movement stratgy to be used to move the Character
     */
    public Context(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Description: <br/>
     * Execute the movement on the Character(i.e.Bug/Chip).
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
    public boolean executeMovements(Board board, Character character, String directions) {
        return strategy.applyMoves(board, character, directions);
    }
}
