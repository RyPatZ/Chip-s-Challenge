package Render;

import Maze.Character;
import Maze.Tiles.Tile;

/** This class is used to record the history of all enemies 
 * in order to make redrawing them easier
 * @author Daniel Marshall
 *
 */
public class CharacterHistory {

    private int previousAbsoluteRow; //The previous row position of the character in the tile array
    private int previousAbsoluteColumn; //The previous column position of the character in the tile array
    private int previousRelativeRow; //The previous row position of the character on the screen
    private int previousRelativeColumn; //The previous row position of the character on the screen
    private int currentAbsoluteRow; //The current row position of the character in the tile array
    private int currentAbsoluteColumn; //The current row position of the character in the tile array
    private int currentRelativeRow; //The current row position of the character on the screen
    private int currentRelativeColumn; //The current row position of the character on the screen
    private boolean hasMoved; //Whether the character moved since last turn
    private String lastImage; //The last image of the character
    private Tile previousTile; //the tile the character was on last turn
    private Tile currentTile; // The tile the character is on this turn
    private final Character character; //The character
    private String direction; //The direction of the current movement

    /**
     * The constructor for a character history object
     *
     * @param car         - current absolute row of the character
     * @param cac         - current absolute column of the character
     * @param crr         - current relative row of the character
     * @param crc         - current relative column of the character
     * @param lastImage   - last image of the character
     * @param character   - the character
     * @param currentTile - the tile the character is currently on
     */
    public CharacterHistory(int car, int cac, int crr, int crc, String lastImage, Character character, Tile currentTile) {
        this.currentAbsoluteRow = car;
        this.previousAbsoluteRow = car;
        this.currentAbsoluteColumn = cac;
        this.previousAbsoluteColumn = cac;
        this.currentRelativeRow = crr;
        this.previousRelativeRow = crr;
        this.currentRelativeColumn = crc;
        this.previousRelativeColumn = crc;
        this.lastImage = lastImage;
        this.character = character;
        this.hasMoved = false;
        this.direction = "none";
        this.currentTile = currentTile;
        this.previousTile = currentTile;

    }

    /**
     * Update whether this character has moved since last time
     */
    public void updateHasMoved() {
        this.hasMoved = (this.currentAbsoluteColumn != previousAbsoluteColumn || this.currentAbsoluteRow != previousAbsoluteRow);
    }

    /**
     * Update all the previous fields, done at the end of a turn to help draw the next turn
     */
    public void updatePrevious() {
        this.previousRelativeColumn = this.currentRelativeColumn;
        this.previousRelativeRow = this.currentRelativeRow;
        this.previousAbsoluteColumn = this.currentAbsoluteColumn;
        this.previousAbsoluteRow = this.currentAbsoluteRow;
        this.previousTile = this.currentTile;
    }

    /**
     * Update the current position of the character
     * @param currentAbsoluteLeftRow - Top Left Row
     * @param currentAbsoluteTopColumn - Top Left Column
     * @param tileArray - The board
     */
    public void updatePosition(int currentAbsoluteLeftRow, int currentAbsoluteTopColumn, Tile[][] tileArray) {
        this.currentAbsoluteRow = character.getRow();
        this.currentAbsoluteColumn = character.getCol();
        this.currentRelativeRow = this.currentAbsoluteRow - currentAbsoluteLeftRow;
        this.currentRelativeColumn = this.currentAbsoluteColumn - currentAbsoluteTopColumn;
        this.currentTile = tileArray[this.currentAbsoluteRow][this.currentAbsoluteColumn];
    }

    /**
     * This method works out which direction the character just moved in,
     * so it knows what pictures to draw
     */
    private String getMovementDirection() {
        if (this.currentAbsoluteRow - this.previousAbsoluteRow > 0) {
            return "down";
        } else if (this.currentAbsoluteRow - this.previousAbsoluteRow < 0) {
            return "up";
        } else if (this.currentAbsoluteColumn - this.previousAbsoluteColumn > 0) {
            return "right";
        } else if (this.currentAbsoluteColumn - this.previousAbsoluteColumn < 0) {
            return "left";
        } else {
            throw new IllegalStateException("Cannot be in the getMovementDirection method if there was no movement");
        }
    }


    /**
     * Method to set all the relevant rows and columns, and the direction and whether the object has moved
     * called at the start of each redraw to set the current attributes
     */
    public void setRelativeRowsAndColumns() {
        updateHasMoved();
        if (this.hasMoved) {
            this.direction = getMovementDirection();
            switch (direction) {
                case "left":
                    this.previousRelativeColumn = this.currentRelativeColumn + 1;
                    this.previousRelativeRow = this.currentRelativeRow;
                    break;
                case "right":
                    this.previousRelativeColumn = this.currentRelativeColumn - 1;
                    this.previousRelativeRow = this.currentRelativeRow;
                    break;
                case "up":
                    this.previousRelativeRow = this.currentRelativeRow + 1;
                    this.previousRelativeColumn = this.currentRelativeColumn;
                    break;
                case "down":
                    this.previousRelativeRow = this.currentRelativeRow - 1;
                    this.previousRelativeColumn = this.currentRelativeColumn;
                    break;
            }
        } else {
            this.previousRelativeRow = this.currentRelativeRow;
            this.previousRelativeColumn = this.currentRelativeColumn;
            direction = "none";
        }

        //Determine what the last image would have been
        switch (this.direction) {
            case "left":
                this.lastImage = "ActorLeft1";
                break;
            case "right":
                this.lastImage = "ActorRight1";
                break;
            case "up":
                this.lastImage = "ActorUp1";
                break;
            case "down":
                this.lastImage = "ActorDown1";
                break;
        }
    }


    /*Beneath this point are getters and setters for all of the fields in the class*/
    
    
    

    /** Gets the previous relative row
     * @return previous relative row
     */
    public int getPreviousRelativeRow() {
        return previousRelativeRow;
    }


    /** Gets the previous relative column
     * @return previous relative column
     */
    public int getPreviousRelativeColumn() {
        return previousRelativeColumn;
    }


    /** Gets the current relative row
     * @return current relative row
     */
    public int getCurrentRelativeRow() {
        return currentRelativeRow;
    }


    /** Gets the current relative column
     * @return current relative column
     */
    public int getCurrentRelativeColumn() {
        return currentRelativeColumn;
    }


    /** Gets the previous tile
     * @return previous tile
     */
    public Tile getPreviousTile() {
        return previousTile;
    }


    /** Gets the current tile
     * @return current tile
     */
    public Tile getCurrentTile() {
        return currentTile;
    }


    /** Gets the last drawn image
     * @return last drawn image
     */
    public String getLastImage() {
        return lastImage;
    }

    
    /** Sets the last drawn image of this enemy
     * @param lastImage the last image
     */
    public void setLastImage(String lastImage) {
        this.lastImage = lastImage;
    }


    /** Gets the direction
     * @return direction
     */
    public String getDirection() {
        return direction;
    }
}
