package Maze.Tiles;

import Maze.Character;
import Maze.Chip;
import Maze.Items.Item;

import java.awt.*;

/**
 * A superclass that the tiles in the game implement
 *
 * @author Oscar Sykes 300486149
 */
public class Tile {
    /**
     * The character that the user controls. If the tile doesn't contain chip this is null.
     */
    Character character;

    /**
     * @return whether this tile contains the chip character
     */
    public boolean containsChip(){
        return character instanceof Chip;
    }

    /**
     * @return the character in the tile. If the tile doesn't contain a character this is null.
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * @param character the character in the tile. If the tile doesn't contain a character this is null.
     */
    public void setCharacter(Character character) {
        this.character = character;
    }

    /**
     * This method is implemented in Tile subclasses
     *
     * @return The name of a tile object.
     **/
    public String toString() {
        assert false; // this should never be reached
        return null;
    }

    /**
     * This method is used by the board toString method
     * This returns a letter representing the character if one is standing on the tile
     * Otherwise it returns letter representing an item if it contains one
     * Otherwise it contains a letter corresponding to the tile type
     *
     * @return a letter representing the tile
     */
    public String getLetter() {
        if(character != null){
            return character.getLetter();
        } else if (this instanceof FreeTile) {
            Item item = ((FreeTile) this).getItem();
            if (item != null) {
                return item.getLetter();
            }
            return " ";
        } else if (this instanceof Exit) {
            return "E";
        } else if (this instanceof InfoField) {
            return "I";
        } else if (this instanceof LockedDoor) {
            LockedDoor l = (LockedDoor) this;
            if (l.getColour() == Color.red) {
                return "R";
            }
            if (l.getColour() == Color.green) {
                return "G";
            }
            if (l.getColour() == Color.blue) {
                return "B";
            }
            if (l.getColour() == Color.yellow) {
                return "Y";
            }
        } else if (this instanceof WallTile) {
            return "W";
        } else if (this instanceof SingleUseTile){
            return "S";
        }
        assert false; // this should be unreachable
        return null;
    }

}
