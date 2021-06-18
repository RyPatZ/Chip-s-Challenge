package Render;

import Maze.*;
import Maze.Character;
import Maze.Items.*;
import Maze.Tiles.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**Class to draw and render the game board
 * @author Daniel Marshall
 */
public class MazeRenderer {

    /* FIELDS */

    /** This constants tells the renderer how many tiles it needs to display,
     *  assuming the displayed portion is always a square. */
    public static final int NUMBER_OF_VISIBLE_SQUARES = 9;

    /** Map to store all the images */
    private final Map<String, Image> images;

    /** Column and row in the tile array of the tile drawn in the top left of the panel */
    private int currentAbsoluteLeftRow; //the current row position of the top left corner in the tile array
    private int currentAbsoluteTopColumn; //the previous column position of the top left corner in the tile array
    private int previousAbsoluteLeftRow; //the current row position of the top left corner in the tile array
    private int previousAbsoluteTopColumn; //the previous column position of the top left corner in the tile array

    /** Height and width of the tile
     * This gets determined by the redraw method based on how wide and tall the
     * board panel is, and how many tiles are being displayed */
    int tileWidth, tileHeight;

    /** These fields are very important coordinates to remember
     * Stored to enable movement, as we need to know what the last tile was in
     * order to do the moving to the current tile, and need to know relative positions as well */
    int previousAbsoluteChipRow; //The previous row position of chip in the tile array
    int previousAbsoluteChipColumn; //The previous column position of chip in the tile array
    int previousRelativeChipRow; //The previous row position of chip on the screen
    int previousRelativeChipColumn; //The previous row position of chip on the screen
    int currentAbsoluteChipRow; //The current row position of chip in the tile array
    int currentAbsoluteChipColumn; //The current row position of chip in the tile array
    int currentRelativeChipRow; //The current row position of chip on the screen
    int currentRelativeChipColumn; //The current row position of chip on the screen

    /**Map to store the above 8 fields, but for each character that isn't chip*/
    Map<Character, CharacterHistory> userGeneratedCharacters;


    /** Map to list all locations of items and treasure to help determine what version of chip to draw */
    private final Map<Tile, Item> itemLocations;

    /**This tells the method what to draw, as if chip is in the center he needs to
     * walk in place while the board moves behind him, but if he is off to the side
     * he needs to walk between tiles */
    private boolean chipInCenterRow, previousChipInCenterRow;
    private boolean chipInCenterColumn, previousChipInCenterColumn;

    /** The direction chip just moved in */
    private String direction = "none";

    /** First time flag to determine if this is the first time the board is rendered */
    boolean firstTime;

    /**Last drawn Chip image*/
    String lastChipImage;

    /** Flag to check whether chip has moved */
    boolean chipMoved;

    /** Amount of time to sleep for between movements */
    int movementCharacterSleepTime = 75;
    int movementAllSleepTime = 35;

    /** Amount of time to sleep for between pickup animation */
    int pickupSleepTime = 80;

    /** The sequence of pictures to draw when Chip wins */
    private final String[] exitSequence;

    /** Graphics object associated with the board pane */
    Graphics graphics;



    /**Constructor for the MazeRenderer. This loads all the .png file names stored in the
     * assets folder into a list which holds every image name so each image can be quickly found and drawn
     * @param g Graphics
     */
    @SuppressWarnings("exports")
	public MazeRenderer(Graphics g){
        this.firstTime = true;
        this.lastChipImage = "Chip";
        this.chipMoved = false;
        this.graphics = g;
        this.itemLocations = new HashMap<>();
        this.exitSequence = new String[8];
        this.exitSequence[0] = "Chip";
        this.exitSequence[1] = "ChipBothHandsRaised";
        this.exitSequence[2] = "Chip";
        this.exitSequence[3] = "ChipBothHandsRaised";
        this.exitSequence[4] = "Chip";
        this.exitSequence[5] = "ChipBothHandsRaised";
        this.exitSequence[6] = "Chip";
        this.exitSequence[7] = "ChipBothHandsRaised";
        //Loading in all images to a map for efficiency in drawing them
        this.images = loadImages();
        this.userGeneratedCharacters = new HashMap<>();
    }


    /** This method is the base redrawing method of the renderer.
     * This should be the only entry into this class aside from the constructor
     * It uses its inputs, and iterates through every tile in the
     * array to determine what type of tile to draw, and then checks if it needs to draw any
     * items on each tile, and then finally draws the man himself, Chip
     * @param board - the game board
     * @param width - the width of the board panel
     * @param height - the height of the board panel
     */
    public void redraw(Board board, int width, int height) {
        /* The following if statements check for errant inputs */
        if(board == null){
            throw new NullPointerException("Error: redraw was passed a null argument");
        }
        if(width<0 || height <0){
            throw new IllegalArgumentException("Error: width or height was negative");
        }
        if(board.getTileArray() == null || board.getChip() == null){
            throw new IllegalArgumentException("Error: board contained null values (Chip or TileArray");
        }

        // This section of the code initialises a few items for the drawing
        Tile[][] tileArray = board.getTileArray();
        Chip chip = board.getChip();
        //Calculate how large each tile is
        this.tileWidth = width / NUMBER_OF_VISIBLE_SQUARES;
        this.tileHeight = height / NUMBER_OF_VISIBLE_SQUARES;
        //Set all the coordinates for this drawing
        this.currentAbsoluteChipRow = chip.getRow();
        this.currentAbsoluteChipColumn = chip.getCol();
        centerChip(tileArray);
        this.currentRelativeChipRow = this.currentAbsoluteChipRow - this.currentAbsoluteLeftRow;
        this.currentRelativeChipColumn = this.currentAbsoluteChipColumn - this.currentAbsoluteTopColumn;

        for(Map.Entry<Character, CharacterHistory> entry : this.userGeneratedCharacters.entrySet()){
            entry.getValue().updatePosition(this.currentAbsoluteLeftRow, this.currentAbsoluteTopColumn, tileArray);
        }

        //Special case of the first time drawing, need to initialise a few things so the methods can work
        if (firstTime) {
            firstTime = false;
            firstTimeSetup(board);
        }

        this.chipMoved = (this.currentAbsoluteChipColumn != previousAbsoluteChipColumn || this.currentAbsoluteChipRow != previousAbsoluteChipRow);
        //set the previous relative coordinates
        if(this.chipMoved){
            setRelativeRowsAndColumns(getMovementDirection());
        }
        else{
            this.previousRelativeChipRow = this.currentRelativeChipRow;
            this.previousRelativeChipColumn = this.currentRelativeChipColumn;
        }

        for(Map.Entry<Character, CharacterHistory> entry : this.userGeneratedCharacters.entrySet()){
            entry.getValue().setRelativeRowsAndColumns();
        }

        /* Need to check if the redraw is occurring because Chip has actually moved,
            or because the window was resized or it is the initial drawing, as this
            requires a different method of drawing
         */
        if(this.chipMoved) {
            redrawMovement(chip, tileArray);
        }
        else{
            redrawNoMovement(tileArray);
        }

        //Update all the previous fields for next time
        this.previousAbsoluteChipColumn = this.currentAbsoluteChipColumn;
        this.previousAbsoluteChipRow = this.currentAbsoluteChipRow;
        this.previousChipInCenterRow =this.chipInCenterRow;
        this.previousChipInCenterColumn =this.chipInCenterColumn;
        this.previousAbsoluteLeftRow = this.currentAbsoluteLeftRow;
        this.previousAbsoluteTopColumn = this.currentAbsoluteTopColumn;
        for(Map.Entry<Character, CharacterHistory> entry : this.userGeneratedCharacters.entrySet()){
            entry.getValue().updatePrevious();
        }
    }


    /**Method to set all the relevant rows and columns*/
    private void setRelativeRowsAndColumns(String direction){
        switch (direction) {
            case "left":
                this.previousRelativeChipColumn = this.currentRelativeChipColumn + 1;
                this.previousRelativeChipRow = this.currentRelativeChipRow;
                break;
            case "right":
                this.previousRelativeChipColumn = this.currentRelativeChipColumn - 1;
                this.previousRelativeChipRow = this.currentRelativeChipRow;
                break;
            case "up":
                this.previousRelativeChipRow = this.currentRelativeChipRow + 1;
                this.previousRelativeChipColumn = this.currentRelativeChipColumn;
                break;
            case "down":
                this.previousRelativeChipRow = this.currentRelativeChipRow - 1;
                this.previousRelativeChipColumn = this.currentRelativeChipColumn;
                break;
        }
    }


    /** Redraw method to redraw the board after Chip has moved
     * @param chip - Chip
     * @param tileArray - The board
     */
    private void redrawMovement(Chip chip, Tile[][] tileArray){
        if(chip == null || tileArray == null){throw new IllegalArgumentException("Null argument passed to redrawMovement");}

        Item toBePickedUp = null;
        String item = "";
        String[] pickupSequence = null;
        if (itemLocations.get(tileArray[this.currentAbsoluteChipRow][this.currentAbsoluteChipColumn]) != null) {
            toBePickedUp = itemLocations.get(tileArray[this.currentAbsoluteChipRow][this.currentAbsoluteChipColumn]);
            itemLocations.remove(tileArray[this.currentAbsoluteChipRow][this.currentAbsoluteChipColumn]);
            item = toBePickedUp.toString();
            pickupSequence = generatePickupSequence(item);
        }

        this.direction = getMovementDirection();
        centerChip(tileArray);
        this.chipInCenterRow = isChipInCenterRow();
        this.chipInCenterColumn = isChipInCenterColumn();

        if (((this.previousChipInCenterRow && this.chipInCenterRow) && (this.previousChipInCenterColumn && this.chipInCenterColumn))
                || ((this.previousChipInCenterRow && this.chipInCenterRow) && (this.direction.equals("up") || this.direction.equals("down")))
                || ((this.previousChipInCenterColumn && this.chipInCenterColumn) && (this.direction.equals("left") || this.direction.equals("right")))){
            redrawMovementAll(tileArray);
            redrawMovementNPCActors(tileArray);
        }
        else if((!(this.previousChipInCenterRow && this.chipInCenterRow) && (this.direction.equals("up") || this.direction.equals("down")))
                || (!(this.previousChipInCenterColumn && this.chipInCenterColumn) && (this.direction.equals("left") || this.direction.equals("right")))){
            redrawMovementChip(tileArray, toBePickedUp, item);
            redrawMovementNPCActors(tileArray);
        }

        if (pickupSequence != null) { //If chip is picking up an item
            drawAnimationSequence(pickupSequence, tileArray);
        }


        if(tileArray[this.currentAbsoluteChipRow][this.currentAbsoluteChipColumn] instanceof Exit){
            drawAnimationSequence(this.exitSequence, tileArray);
        }


    }

    /**Redraw every actor if there was no movement since the last drawing
     *
     * @param tileArray the board
     */
    private void redrawNoMovementNPCActors(Tile[][] tileArray) {
        if(tileArray == null){
            throw new NullPointerException("Null tileArray passed into redrawChipMovement");
        }
        for(Map.Entry<Character, CharacterHistory> entry : this.userGeneratedCharacters.entrySet()){
            CharacterHistory charHist = entry.getValue();
            drawTile(charHist.getPreviousTile(), charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn());
            drawImage(charHist.getLastImage(), charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn());
        }

    }

    /**Redraw every actor if there was movement since the last drawing
     *
     * @param tileArray the board
     */
    private void redrawMovementNPCActors(Tile[][] tileArray) {
        if(tileArray == null){
            throw new NullPointerException("Null tileArray passed into redrawChipMovement");
        }
        for(Map.Entry<Character, CharacterHistory> entry : this.userGeneratedCharacters.entrySet()){
            if((entry.getValue().getCurrentRelativeColumn() >= 0
                    && entry.getValue().getCurrentRelativeColumn() < NUMBER_OF_VISIBLE_SQUARES
                    && entry.getValue().getCurrentRelativeRow() >= 0
                    && entry.getValue().getCurrentRelativeRow() < NUMBER_OF_VISIBLE_SQUARES)
                    ||(entry.getValue().getPreviousRelativeColumn() >= 0
                    && entry.getValue().getPreviousRelativeColumn() < NUMBER_OF_VISIBLE_SQUARES
                    && entry.getValue().getPreviousRelativeRow() >= 0
                    && entry.getValue().getPreviousRelativeRow() < NUMBER_OF_VISIBLE_SQUARES)
            ){
                redrawMovementActor(entry.getValue());
            }
        }


    }
    /** This method is for when all the actors move
     * @param charHist The character history object
     */
    public void redrawMovementActor(CharacterHistory charHist){
        if(charHist == null){
            throw new NullPointerException("Null character history passed into redrawChipMovement");
        }
        try {
            redrawActorSquares(charHist);
            switch (charHist.getDirection()) {
                case "up":
                    drawImage("ActorUp1", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorUp2", charHist.getPreviousRelativeRow() - 0.33, charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorUp1", charHist.getPreviousRelativeRow() - 0.66, charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorUp2", charHist.getCurrentRelativeRow(), charHist.getCurrentRelativeColumn());
                    charHist.setLastImage("ActorUp2");
                    Thread.sleep(movementCharacterSleepTime);
                    break;
                case "down":
                    drawImage("ActorDown1", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorDown2", charHist.getPreviousRelativeRow() + 0.33, charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorDown1", charHist.getPreviousRelativeRow() + 0.66, charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorDown2", charHist.getCurrentRelativeRow(), charHist.getCurrentRelativeColumn());
                    charHist.setLastImage("ActorDown2");
                    Thread.sleep(movementCharacterSleepTime);
                    break;
                case "right":
                    drawImage("ActorRight1", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorRight2", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn() + 0.33);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorRight1", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn() + 0.66);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorRight2", charHist.getCurrentRelativeRow(), charHist.getCurrentRelativeColumn());
                    charHist.setLastImage("ActorRight2");
                    Thread.sleep(movementCharacterSleepTime);
                    break;
                case "left":
                    drawImage("ActorLeft1", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorLeft2", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn() - 0.33);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorLeft1", charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn() - 0.66);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawActorSquares(charHist);
                    drawImage("ActorLeft2", charHist.getCurrentRelativeRow(), charHist.getCurrentRelativeColumn());
                    charHist.setLastImage("ActorLeft2");
                    Thread.sleep(movementCharacterSleepTime);
                    break;
                case "none":
                    redrawActorSquares(charHist);
                    drawImage(charHist.getLastImage(), charHist.getCurrentRelativeRow(), charHist.getCurrentRelativeColumn());
                    Thread.sleep(movementCharacterSleepTime);
                    break;
            }
        }catch(InterruptedException e){System.out.println("Issue with Thread.sleep in the redrawMovementActor method");}

    }

    /** Redraw the two squares that the Actor is moving from and to
     * @param charHist The character history object
     */
    public void redrawActorSquares(CharacterHistory charHist) {
        drawTile(charHist.getPreviousTile(), charHist.getPreviousRelativeRow(), charHist.getPreviousRelativeColumn());
        drawTile(charHist.getCurrentTile(), charHist.getCurrentRelativeRow(), charHist.getCurrentRelativeColumn());
    }


    /** This method is for when Chip moves over the tiles, as opposed to the tiles moving under Chip
     * This behaviour occurs when the map is small, or chip is near the edge of the map
     * @param tileArray - The board
     * @param toBePickedUp - The potential item chip is about to pick up
     * @param item - The name of the item
     */
    public void redrawMovementChip(Tile[][] tileArray, Item toBePickedUp, String item){
        if(tileArray == null){
            throw new NullPointerException("Null tileArray passed into redrawChipMovement");
        }
        Tile previousTile = tileArray[this.previousAbsoluteChipRow][this.previousAbsoluteChipColumn];
        Tile currentTile = tileArray[this.currentAbsoluteChipRow][this.currentAbsoluteChipColumn];

        try {
            redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
            switch (this.direction) {
                case "up":
                    drawImage("ChipBack", this.previousRelativeChipRow, this.currentRelativeChipColumn);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipBackLeftLegUp", this.previousRelativeChipRow - 0.33, this.currentRelativeChipColumn);
                    //redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipBackRightLegUp", this.previousRelativeChipRow - 0.66, this.currentRelativeChipColumn);
                    //redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipBack", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                    //redrawNoMovementNPCActors(tileArray);
                    this.lastChipImage = "ChipBack";
                    Thread.sleep(movementCharacterSleepTime);
                    break;
                case "down":
                    drawImage("Chip", this.previousRelativeChipRow, this.currentRelativeChipColumn);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipFrontLeftLegUp", this.previousRelativeChipRow + 0.33, this.currentRelativeChipColumn);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipFrontRightLegUp", this.previousRelativeChipRow + 0.66, this.currentRelativeChipColumn);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("Chip", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                   // redrawNoMovementNPCActors(tileArray);
                    this.lastChipImage = "Chip";
                    Thread.sleep(movementCharacterSleepTime);
                    break;
                case "right":
                    drawImage("ChipRight", this.currentRelativeChipRow, this.previousRelativeChipColumn);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipRightWalking", this.currentRelativeChipRow, this.previousRelativeChipColumn + 0.25);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipRight", this.currentRelativeChipRow, this.previousRelativeChipColumn + 0.5);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipRightWalking", this.currentRelativeChipRow, this.previousRelativeChipColumn + 0.75);
                   // redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipRight", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                   // redrawNoMovementNPCActors(tileArray);
                    this.lastChipImage = "ChipRight";
                    Thread.sleep(movementCharacterSleepTime);
                    break;
                case "left":
                    drawImage("ChipLeft", this.currentRelativeChipRow, this.previousRelativeChipColumn);
                    //redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipLeftWalking", this.currentRelativeChipRow, this.previousRelativeChipColumn - 0.25);
                    //redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipLeft", this.currentRelativeChipRow, this.previousRelativeChipColumn - 0.5);
                    //redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipLeftWalking", this.currentRelativeChipRow, this.previousRelativeChipColumn - 0.75);
                    //redrawNoMovementNPCActors(tileArray);
                    Thread.sleep(movementCharacterSleepTime);
                    redrawChipSquares(previousTile, currentTile, toBePickedUp, item);
                    drawImage("ChipLeft", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                    //redrawNoMovementNPCActors(tileArray);
                    this.lastChipImage = "ChipLeft";
                    Thread.sleep(movementCharacterSleepTime);
                    break;
            }
        }catch(InterruptedException e){System.out.println("Issue with Thread.sleep in the redrawMovementChip method");}

    }

    /** Redraw the two squares that chip is moving from and to
     * @param previousTile The previous tile
     * @param currentTile The current tile
     * @param toBePickedUp The item about to be picked up, null if nothing
     * @param item The name of the item
     */
    public void redrawChipSquares(Tile previousTile, Tile currentTile, Item toBePickedUp, String item) {
        drawTile(previousTile, this.previousRelativeChipRow, this.previousRelativeChipColumn);
        drawTile(currentTile, this.currentRelativeChipRow, this.currentRelativeChipColumn);
        if (toBePickedUp != null) {
            drawImage(item, this.currentRelativeChipRow, this.currentRelativeChipColumn);
        }
    }

    /** Redraw the entire board after Chip has moved
     * This method is for when the board needs to move behind Chip, as opposed to chip moving
     * on the board, which occurs when chip is near the edge of the map
     * In this method chip stays in the same point on the screen
     * @param tileArray - The board of tiles
     */
    public void redrawMovementAll(Tile[][] tileArray) {
        if (tileArray == null) {
            throw new IllegalArgumentException("Null tileArray passed into redrawChipMovement");
        }

        try {
            redrawMovementAllTiles(tileArray, 0, 0);
            if (this.direction.equals("up")) {
                drawImage("ChipBack", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipBack";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0.33, 0);
                drawImage("ChipBackLeftLegUp", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipBackLeftLegUp";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0.66, 0);
                drawImage("ChipBackRightLegUp", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipBackRightLegUp";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 1, 0);
                drawImage("ChipBack", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipBack";
                Thread.sleep(movementAllSleepTime);
            }

            if (this.direction.equals("down")) {
                drawImage("Chip", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "Chip";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, -0.33, 0);
                drawImage("ChipFrontLeftLegUp", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipFrontLeftLegUp";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, -0.66, 0);
                drawImage("ChipFrontRightLegUp", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipFrontRightLegUp";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, -1, 0);
                drawImage("Chip", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "Chip";
                Thread.sleep(movementAllSleepTime);
            }

            if (this.direction.equals("right")) {
                drawImage("ChipRight", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipRight";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, -0.25);
                drawImage("ChipRightWalking", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipRightWalking";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, -0.5);
                drawImage("ChipRight", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipRight";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, -0.75);
                drawImage("ChipRightWalking", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipRightWalking";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, -1);
                drawImage("ChipRight", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipRight";
                Thread.sleep(movementAllSleepTime);
            }

            if (this.direction.equals("left")) {
                drawImage("ChipLeft", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipLeft";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, 0.25);
                drawImage("ChipLeftWalking", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipLeftWalking";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, 0.5);
                drawImage("ChipLeft", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipLeft";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, 0.75);
                drawImage("ChipLeftWalking", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipLeftWalking";
                Thread.sleep(movementAllSleepTime);
                redrawMovementAllTiles(tileArray, 0, 1);
                drawImage("ChipLeft", this.currentRelativeChipRow, this.currentRelativeChipColumn);
                this.lastChipImage = "ChipLeft";
                Thread.sleep(movementAllSleepTime);
            }
        }catch(InterruptedException e){System.out.println("Issue with Thread.sleep in the redrawMovementAll method");}
    }

    /** Redraw the entire board with a specified offset
     */
    private void redrawMovementAllTiles(Tile[][] tileArray, double rowOffset, double columnOffset){
        //Without this list, chip will be invisible once the tile he is standing on are drawn,
        //until the end of the method, which makes him flicker on the screen, this list and its subsequent uses fixes that
        List<CenterTile> centerTiles = new ArrayList<>();
        int rowStart = 0;
        int columnStart = 0;
        int rowEnd = NUMBER_OF_VISIBLE_SQUARES;
        int columnEnd = NUMBER_OF_VISIBLE_SQUARES;

        //This makes sure that the extra row that is needed to be drawn is drawn
        if(rowOffset < 0){rowEnd = rowEnd+1;}
        if(columnOffset < 0){columnEnd = columnEnd+1;}
        if(rowOffset > 0){rowStart = -1;}
        if(columnOffset > 0){columnStart = -1;}

        for(int row = rowStart; row < rowEnd; row++){
            for(int column = columnStart; column < columnEnd; column++){
                Tile currentTile = tileArray[this.previousAbsoluteLeftRow+row][this.previousAbsoluteTopColumn+column];
                if(currentTile == null){throw new NullPointerException("Error: Tile pulled from tile array was null in redrawMovementAllTiles");}
                //if tile will be drawn under chip add to list
                if((this.previousAbsoluteLeftRow + row == this.previousAbsoluteChipRow && this.previousAbsoluteTopColumn + column == this.previousAbsoluteChipColumn)
                        || (this.previousAbsoluteLeftRow + row == this.currentAbsoluteChipRow && this.previousAbsoluteTopColumn + column == this.currentAbsoluteChipColumn)){
                    centerTiles.add(new CenterTile(row+rowOffset,column+columnOffset,currentTile));
                }
                drawTile(currentTile, row+rowOffset, column+columnOffset);

                for(Map.Entry<Character, CharacterHistory> entry : this.userGeneratedCharacters.entrySet()){
                    if(currentTile == entry.getValue().getPreviousTile()) {
                        drawImage(entry.getValue().getLastImage(), row + rowOffset, column + columnOffset);
                    }
                }
                //when both tiles underneath chip have been redrawn, chip will disappear as he has been drawn over so draw him again
                if(centerTiles.size() == 2){
                    drawImage(lastChipImage, this.currentRelativeChipRow, this.currentRelativeChipColumn);
                }
            }
        }

        //get rid of the existing chip image so the next chip drawing is not drawn onto a previous chip
        if(centerTiles.size() == 2){
            drawTile(centerTiles.get(0).getTile(), centerTiles.get(0).getRow(), centerTiles.get(0).getCol());
            drawTile(centerTiles.get(1).getTile(), centerTiles.get(1).getRow(), centerTiles.get(1).getCol());
            centerTiles.clear();
        }
    }

    /** Redraw method to redraw the board when no movement has occurred
     * @param tileArray - The board
     */
    private void redrawNoMovement(Tile[][] tileArray){
        drawAllTiles(tileArray);
        drawImage(this.lastChipImage, this.currentRelativeChipRow, this.currentRelativeChipColumn);
        redrawNoMovementNPCActors(tileArray);

    }

    /** Draws the animation of chip picking up an item
     * @param sequence - list of images to draw
     * @param tileArray - The Board
     */
    public void drawAnimationSequence(String[] sequence, Tile[][] tileArray){
        Tile currentTile = tileArray[currentAbsoluteChipRow][currentAbsoluteChipColumn];
        try{
            for (String string : sequence) {
                Thread.sleep(this.pickupSleepTime);
                drawTile(currentTile, this.currentRelativeChipRow, this.currentRelativeChipColumn);
                drawImage(string, this.currentRelativeChipRow, this.currentRelativeChipColumn);
            }
            Thread.sleep(this.pickupSleepTime);
        }catch(InterruptedException e){System.out.println("Issue with Thread.sleep in the drawAnimationSequence method");}
        this.lastChipImage = sequence[sequence.length-1];
    }

    /** This method loads the image sequence to play when chip picks up the item
     * that he is about to land on
     * @param item The item to pick up
     * @return list of images to show
     */
    public String[] generatePickupSequence(String item){
        String[] sequence = new String[4];
        sequence[0] = "Chip";
        sequence[1] = "ChipHandHalfRaised";
        sequence[2] = "ChipHandRaised";
        sequence[3] = "ChipHolding" + item;
        return sequence;
    }

    /** This method works out which direction chip just moved in,
     * so it knows what pictures to draw
     */
    private String getMovementDirection(){
        if(this.currentAbsoluteChipRow - this.previousAbsoluteChipRow > 0){return "down";}
        else if(this.currentAbsoluteChipRow - this.previousAbsoluteChipRow < 0 ){return "up";}
        else if(this.currentAbsoluteChipColumn - this.previousAbsoluteChipColumn > 0 ){return "right";}
        else if(this.currentAbsoluteChipColumn - this.previousAbsoluteChipColumn < 0 ){return "left";}
        else{throw new IllegalStateException("Cannot be in the getMovementDirection method if there was no movement");}
    }

    /**This method checks whether or not chip is actually in the center row of the screen, as this determines
     * the type of movement animation that is performed */
    private boolean isChipInCenterRow(){
        return(this.currentAbsoluteChipRow - this.currentAbsoluteLeftRow == NUMBER_OF_VISIBLE_SQUARES / 2);
    }

    /**This method checks whether or not chip is actually in the center column of the screen, as this determines
     * the type of movement animation that is performed */
    private boolean isChipInCenterColumn(){
        return(this.currentAbsoluteChipColumn - this.currentAbsoluteTopColumn == NUMBER_OF_VISIBLE_SQUARES / 2);
    }

    /**This method makes sure that if possible, chip is in the center of the screen
     * If chip is near the edge of the screen, it draws the screen so that chip is
     * as close to the center of the screen as possible whilst still
     * displaying a full screen
     * @param tileArray - the array of tiles on the board
     */
    private void centerChip(Tile[][] tileArray){
        if (this.currentAbsoluteChipColumn - NUMBER_OF_VISIBLE_SQUARES/2 < 0) {
            this.currentAbsoluteTopColumn = 0;
        } else if (this.currentAbsoluteChipColumn + NUMBER_OF_VISIBLE_SQUARES/2 > tileArray[0].length - 1) {
            this.currentAbsoluteTopColumn = tileArray[0].length - NUMBER_OF_VISIBLE_SQUARES;
        } else {
            this.currentAbsoluteTopColumn = this.currentAbsoluteChipColumn - NUMBER_OF_VISIBLE_SQUARES/2;
        }
        if (this.currentAbsoluteChipRow - NUMBER_OF_VISIBLE_SQUARES/2 < 0) {
            this.currentAbsoluteLeftRow = 0;
        } else if (this.currentAbsoluteChipRow + NUMBER_OF_VISIBLE_SQUARES/2 > tileArray.length - 1) {
            this.currentAbsoluteLeftRow = tileArray.length - NUMBER_OF_VISIBLE_SQUARES;
        } else {
            this.currentAbsoluteLeftRow = this.currentAbsoluteChipRow - NUMBER_OF_VISIBLE_SQUARES/2;
        }
    }

    /** The most important method of the class, this loops through each tile in the displayed
     * part of the array and draws each tile
     */
    private void drawAllTiles(Tile[][] tileArray){
        for(int row = 0; row < NUMBER_OF_VISIBLE_SQUARES; row++){
            for(int column = 0; column < NUMBER_OF_VISIBLE_SQUARES; column++){
                Tile currentTile = tileArray[this.currentAbsoluteLeftRow+row][this.currentAbsoluteTopColumn+column];
                if(currentTile == null){
                    throw new NullPointerException("Error: Tile pulled from tile array was null");
                }
                drawTile(currentTile, row, column);
                for(Map.Entry<Character, CharacterHistory> entry : this.userGeneratedCharacters.entrySet()){
                    if(currentTile == entry.getValue().getPreviousTile()) {
                        drawImage(entry.getValue().getLastImage(), row, column);
                    }
                }
            }
        }
    }

    /** This method takes in a tile, the current row, and the current column,
     * and uses an if statement to decide firstly what type of tile to draw, then what needs to be drawn
     * on top of the tile if there is anything, then finally it draws Chip
     *
     * @param tile - Tile to draw
     * @param row - row in the output to be drawing at
     * @param column - column in the output to be drawing at
     */
    private void drawTile(Tile tile, double row, double column){
        if(tile == null){
            throw new IllegalArgumentException("Null tile passed to drawTile method");
        }
        if(tile instanceof FreeTile){
            drawImage("FreeTile", row, column);
            Item currentItem = ((FreeTile) tile).getItem();
            if(currentItem != null) {
                drawImage(currentItem.toString(), row, column);
            }
        }
        else{
            drawImage(tile.toString(), row, column);
        }
    }

    /**This method calls the graphics object and draws a tile to the screen
     *
     * @param imageName the image to draw
     * @param row the row to draw it on
     * @param column the column to draw it on
     */
    private void drawImage(String imageName, double row, double column){
        this.graphics.drawImage(this.images.get(imageName), (int)(column*this.tileWidth), (int)(row*this.tileHeight), this.tileWidth, this.tileHeight, null);
    }

    /**Method to do all the first time initialisation */
    private void firstTimeSetup(Board board){
        this.lastChipImage = "Chip";
        this.previousAbsoluteChipRow = this.currentAbsoluteChipRow;
        this.previousAbsoluteChipColumn = this.currentAbsoluteChipColumn;
        this.previousRelativeChipRow = this.currentRelativeChipRow;
        this.previousRelativeChipColumn = this.currentRelativeChipColumn;
        this.chipInCenterRow = isChipInCenterRow();
        this.previousChipInCenterRow = this.chipInCenterRow;
        this.chipInCenterColumn = isChipInCenterColumn();
        this.previousChipInCenterColumn = this.chipInCenterColumn;
        this.previousAbsoluteLeftRow = this.currentAbsoluteLeftRow;
        this.previousAbsoluteTopColumn = this.currentAbsoluteTopColumn;
        initialiseItemLocations(board.getTileArray());
        initialiseActorObjects(board.getCharacters(), board.getTileArray());
    }

    /** This method finds all items in the board and adds them to a map
     * This is useful later on, as when chip moves to a tile that previously
     * had an item on it this will help show that, in order to draw the right images
     * @param tileArray the array of the board
     */
    private void initialiseItemLocations(Tile[][] tileArray) {
        if(tileArray == null){throw new IllegalArgumentException("Error: Passing initialiseItemLocations a null TileArray");}
        for (Tile[] tiles : tileArray) {
            for (int column = 0; column < tileArray[0].length; column++) {
                Tile currentTile = tiles[column];
                if (currentTile == null) {
                    throw new NullPointerException("Error: Tile pulled from tile array was null");
                }
                if (currentTile instanceof FreeTile && ((FreeTile) currentTile).getItem() != null && !(((FreeTile) currentTile).getItem() instanceof ExitLock)) {
                    this.itemLocations.put(currentTile, ((FreeTile) currentTile).getItem());
                }
            }
        }
    }

    /** This method finds all actors in the board and adds them to a map
     * It also initialises their locations
     * This is useful later on, to determine where each character is for movement
     * @param characters list of characters
     * @param tileArray the array of tiles
     */
    private void initialiseActorObjects(List<UserGeneratedCharacter> characters, Tile[][] tileArray) {
        for(UserGeneratedCharacter character: characters){
            int car = character.getRow();
            int cac = character.getCol();
            int crr = car-this.currentAbsoluteLeftRow;
            int crc = car-this.currentAbsoluteTopColumn;
            Tile currentTile = tileArray[car][cac];
            CharacterHistory newUGC = new CharacterHistory(car, cac, crr, crc, "ActorUp1", character, currentTile);
            this.userGeneratedCharacters.put(character, newUGC);
        }
    }

    /** Load all the assets into a map for quicker access of them */
    private Map<String, Image> loadImages(){
        Map<String, Image> images = new HashMap<>();
        try{
            images.put("ExitTile", ImageIO.read(new File("./assets/Tiles/Exit.png")));
            images.put("FreeTile", ImageIO.read(new File("./assets/Tiles/FreeTile.png")));
            images.put("SingleUseTile", ImageIO.read(new File("./assets/Tiles/SingleUseTile.png")));
            images.put("InfoField", ImageIO.read(new File("./assets/Tiles/InfoField.png")));
            images.put("LockedDoorRed", ImageIO.read(new File("./assets/Tiles/LockedDoorRed.png")));
            images.put("LockedDoorGreen", ImageIO.read(new File("./assets/Tiles/LockedDoorGreen.png")));
            images.put("LockedDoorBlue", ImageIO.read(new File("./assets/Tiles/LockedDoorBlue.png")));
            images.put("LockedDoorYellow", ImageIO.read(new File("./assets/Tiles/LockedDoorYellow.png")));
            images.put("WallTile", ImageIO.read(new File("./assets/Tiles/WallTile.png")));
        }catch(IOException e){System.out.println("IO Exception reading tile images");}
        try{
            images.put("ExitLock", ImageIO.read(new File("./assets/Items/ExitLock.png")));
            images.put("KeyBlue", ImageIO.read(new File("./assets/Items/KeyBlue.png")));
            images.put("KeyRed", ImageIO.read(new File("./assets/Items/KeyRed.png")));
            images.put("KeyGreen", ImageIO.read(new File("./assets/Items/KeyGreen.png")));
            images.put("KeyYellow", ImageIO.read(new File("./assets/Items/KeyYellow.png")));
            images.put("Treasure", ImageIO.read(new File("./assets/Items/Treasure.png")));
        }catch(IOException e){System.out.println("IO Exception reading item images");}
        try{
            images.put("Chip", ImageIO.read(new File("./assets/Chip/Chip.png")));
            images.put("ChipBack", ImageIO.read(new File("./assets/Chip/ChipBack.png")));
            images.put("ChipBackLeftLegUp", ImageIO.read(new File("./assets/Chip/ChipBackLeftLegUp.png")));
            images.put("ChipBackRightLegUp", ImageIO.read(new File("./assets/Chip/ChipBackRightLegUp.png")));
            images.put("ChipBothHandsRaised", ImageIO.read(new File("./assets/Chip/ChipBothHandsRaised.png")));
            images.put("ChipFrontLeftLegUp", ImageIO.read(new File("./assets/Chip/ChipFrontLeftLegUp.png")));
            images.put("ChipFrontRightLegUp", ImageIO.read(new File("./assets/Chip/ChipFrontRightLegUp.png")));
            images.put("ChipHandHalfRaised", ImageIO.read(new File("./assets/Chip/ChipHandHalfRaised.png")));
            images.put("ChipHandRaised", ImageIO.read(new File("./assets/Chip/ChipHandRaised.png")));
            images.put("ChipHoldingKeyBlue", ImageIO.read(new File("./assets/Chip/ChipHoldingKeyBlue.png")));
            images.put("ChipHoldingKeyGreen", ImageIO.read(new File("./assets/Chip/ChipHoldingKeyGreen.png")));
            images.put("ChipHoldingKeyRed", ImageIO.read(new File("./assets/Chip/ChipHoldingKeyRed.png")));
            images.put("ChipHoldingTreasure", ImageIO.read(new File("./assets/Chip/ChipHoldingTreasure.png")));
            images.put("ChipLeft", ImageIO.read(new File("./assets/Chip/ChipLeft.png")));
            images.put("ChipLeftWalking", ImageIO.read(new File("./assets/Chip/ChipLeftWalking.png")));
            images.put("ChipRight", ImageIO.read(new File("./assets/Chip/ChipRight.png")));
            images.put("ChipRightWalking", ImageIO.read(new File("./assets/Chip/ChipRightWalking.png")));
        }catch(IOException e){System.out.println("IO Exception reading Chip images");}
        try{
            images.put("ActorDown1", ImageIO.read(new File("./assets/Actor/ActorDown1.png")));
            images.put("ActorDown2", ImageIO.read(new File("./assets/Actor/ActorDown2.png")));
            images.put("ActorUp1", ImageIO.read(new File("./assets/Actor/ActorUp1.png")));
            images.put("ActorUp2", ImageIO.read(new File("./assets/Actor/ActorUp2.png")));
            images.put("ActorLeft1", ImageIO.read(new File("./assets/Actor/ActorLeft1.png")));
            images.put("ActorLeft2", ImageIO.read(new File("./assets/Actor/ActorLeft2.png")));
            images.put("ActorRight1", ImageIO.read(new File("./assets/Actor/ActorRight1.png")));
            images.put("ActorRight2", ImageIO.read(new File("./assets/Actor/ActorRight2.png")));
        }catch(IOException e){System.out.println("IO Exception reading actor images");}
        return images;
    }

    /**Get the absolute x coordinate of the top left square in the BoardCanvas
     * @return the absolute left row*/
    public int getCurrentAbsoluteLeftRow(){return this.currentAbsoluteLeftRow;}

    /**Get the absolute y coordinate of the top left square in the BoardCanvas
     * @return the absolute top column*/
    public int getCurrentAbsoluteTopColumn(){return this.currentAbsoluteTopColumn;}

}
