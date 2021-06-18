package Maze;

import com.google.common.base.Preconditions;

/**
 * This is an abstract class that both chip and UserGeneratedCharacter extend
 * This defines basic getters and setters for characters
 * @author Oscar Sykes 300486149
 */
public abstract class Character{
    /**
     * The row and col of the tile the character is currently in
     */
    private int row, col;

    /**
     * The constructor for a character object
     * @param row the row the character starts on
     * @param col the column the character starts on
     */
    public Character(int row, int col){
        Preconditions.checkArgument(row >= 0 && col >= 0);
        this.row = row;
        this.col = col;
    }

    /**
     * @return the row of the tile the character is currently in
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row the row of the tile the character is currently in
     */
    public void setRow(int row) {
        Preconditions.checkArgument(row >= 0);
        this.row = row;
    }

    /**
     * @return the column of the tile the character is currently in
     */
    public int getCol() {
        return col;
    }

    /**
     * @param col sets the col of the tile character is currently in
     */
    public void setCol(int col) {
        Preconditions.checkArgument(col >= 0);
        this.col = col;
    }

    /**
     * @return this is what the character is referred to as in the boards toString method
     * This should be the same as what the character is referred to as in the JSON objects
     */
    public abstract String getLetter();
}
