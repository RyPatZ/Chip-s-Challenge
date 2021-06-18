package Maze;

import Maze.Items.Key;
import com.google.common.base.Preconditions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chip is the character the user controls
 *
 * @author Oscar Sykes 300486149
 */
public class Chip extends Character{
    /**
     * A list of the keys currently in chips inventory
     */
    private List<Key> keys;

    /**
     * Constructs a chip object and initialises chips inventory of keys
     *
     * @param row the row of the tile chip starts in
     * @param col the column of the tile chip starts in
     */
    public Chip(int row, int col) {
        super(row, col);
        this.keys = new ArrayList<>();
    }

    /**
     * @param key adds this key to chips inventory
     */
    public void addKey(Key key) {
        Preconditions.checkNotNull(key);
        Color c = key.getColour();
        Preconditions.checkArgument(c == Color.red || c == Color.blue || c == Color.green
                || c == Color.yellow);
        int inventorySize = keys.size();
        keys.add(key);
        assert keys.size() == inventorySize + 1;
    }

    /**
     * @param colour colour of key
     * @return whether chip contains a key of the given colour
     */
    public boolean containsKey(Color colour) {
        Preconditions.checkArgument(colour == Color.red || colour == Color.blue || colour == Color.green
                || colour == Color.yellow);
        for (Key key : keys) {
            if (key.getColour() == colour) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param colour returns the first key found of this colour
     */
    public void removeKey(Color colour) {
        Preconditions.checkArgument(colour == Color.red || colour == Color.blue || colour == Color.green
                || colour == Color.yellow);
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).getColour() == colour) {
                keys.remove(keys.get(i));
                return;
            }
        }
    }

    /**
     * @return chips inventory of keys
     */
    public List<Key> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    /**
     * @return the value chip is referred to as in the boards toString method
     */
    public String getLetter(){
        return "H";
    }
}
