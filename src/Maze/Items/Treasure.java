package Maze.Items;

/**
 * Treasure objects used in game
 *
 * @author Oscar Sykes 300486149
 */
public class Treasure implements Item {

    /**
     * @return the name of the Treasure image
     */
    public String toString() {
        return "Treasure";
    }

    /**
     * @return the letter used to represent Treasure in the boards toString method
     */
    @Override
    public String getLetter() {
        return "C";
    }
}
