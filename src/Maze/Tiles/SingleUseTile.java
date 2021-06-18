package Maze.Tiles;

/**
 * A tile that can only be used once. When chip walks on to it it turns into a wall tile
 * @author Oscar Sykes 300486149
 */
public class SingleUseTile extends Tile{
    /**
     * @return the name of the image that renderer uses to draw
     */
    public String toString(){
        return "SingleUseTile";
    }
}
