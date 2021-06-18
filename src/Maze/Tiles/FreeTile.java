package Maze.Tiles;

import Maze.Items.Item;

/**
 * FreeTile objects used in game
 *
 * @author Oscar Sykes 300486149
 */
public class FreeTile extends Tile {
    /**
     * the item contained in the tile. If the tile doesn't contain an item this is null.
     */
    Item item;

    /**
     * @return the item contained in the tile. If the tile doesn't contain an item this is null.
     */
    public Item getItem() {
        return item;
    }

    /**
     * @param item the item contained in the tile. If the tile doesn't contain an item this is null.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * @return the name of the FreeTile image
     */
    public String toString() {
        return "FreeTile";
    }
}
