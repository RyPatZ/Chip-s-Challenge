package Maze.Items;

/**
 * ExitLock objects used in game
 *
 * @author Oscar Sykes 300486149
 */
public class ExitLock implements Item {

    /**
     * @return the name of the ExitLock image
     */
    public String toString() {
        return "ExitLock";
    }

    /**
     * @return the letter used to represent ExitLocks in the boards toString method
     */
    @Override
    public String getLetter() {
        return "X";
    }
}
