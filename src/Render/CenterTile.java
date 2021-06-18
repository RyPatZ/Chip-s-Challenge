package Render;

import Maze.Tiles.Tile;

/** This stores the information about the tiles that chip is standing on so that 
 * when rendering we don't draw over chip
 * @author Daniel Marshall
 *
 */
public class CenterTile {
    private final double row;
    private final double column;
    private final Tile tile;

    /** Constructor for the centertile class
     * @param row - the row
     * @param column - the column
     * @param tile - the tile
     */
    public CenterTile(double row, double column, Tile tile){
        this.row = row;
        this.column = column;
        this.tile = tile;
    }

    /** Get the row of this tile
     * @return row
     */
    public double getRow(){return this.row;}
    /** Get the column of this tile
     * @return column
     */
    public double getCol(){return this.column;}
    /** Get the tile
     * @return tile
     */
    public Tile getTile(){return this.tile;}


}
