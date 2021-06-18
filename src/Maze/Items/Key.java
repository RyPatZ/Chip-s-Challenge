package Maze.Items;

import com.google.common.base.Preconditions;

import java.awt.*;

/**
 * Key objects used in game
 *
 * @author Oscar Sykes 300486149
 */
public class Key implements Item {
    /**
     * the colour of the key
     */
    private final Color colour;

    /**
     * Checks whether a key uses a valid colour and then makes the key
     *
     * @param colour the colour of the key
     */
    public Key(Color colour) {
        Preconditions.checkArgument(colour == Color.red || colour == Color.blue
                || colour == Color.green || colour == Color.yellow);
        this.colour = colour;
    }

    /**
     * @return the colour of the key
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @return the image name corresponding to a key with a given colour
     */
    public String toString() {
        if (colour == Color.red) {
            return "KeyRed";
        }
        if (colour == Color.blue) {
            return "KeyBlue";
        }
        if (colour == Color.green) {
            return "KeyGreen";
        }
        if (colour == Color.yellow) {
            return "KeyYellow";
        }
        assert false; // this should be unreachable
        return null;
    }

    /**
     * @return the letters used to represent Keys in the boards toString method
     */
    public String getLetter() {
        if (colour == Color.red) {
            return "r";
        }
        if (colour == Color.blue) {
            return "b";
        }
        if (colour == Color.green) {
            return "g";
        }
        if (colour == Color.yellow) {
            return "y";
        }
        return null;
    }
}
