package Maze.Tiles;

import com.google.common.base.Preconditions;

import java.awt.*;

/**
 * LockedDoor objects used in game
 *
 * @author Oscar Sykes 300486149
 */
public class LockedDoor extends Tile {
    /**
     * the colour of the key needed to unlock the door
     */
    private final Color colour;

    /**
     * checks whether the provided colour is valid and then constructs the LockedDoor object
     *
     * @param colour the colour of the key needed to unlock the door
     */
    public LockedDoor(Color colour) {
        Preconditions.checkArgument(colour == Color.red || colour == Color.blue || colour == Color.green
                || colour == Color.yellow);
        this.colour = colour;
    }

    /**
     * @return the colour of the key needed to unlock the door
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @return the name of the LockedDoor images with a given colour
     */
    public String toString() {
        if (colour == Color.red) {
            return "LockedDoorRed";
        }
        if (colour == Color.blue) {
            return "LockedDoorBlue";
        }
        if (colour == Color.green) {
            return "LockedDoorGreen";
        }
        if (colour == Color.yellow) {
            return "LockedDoorYellow";
        }
        assert false; // this should be unreachable
        return null;
    }
}
