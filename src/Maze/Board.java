package Maze;

import Maze.Items.ExitLock;
import Maze.Items.Item;
import Maze.Items.Key;
import Maze.Items.Treasure;
import Maze.Tiles.*;
import Persistance.JsonParser;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the board used in the game and defines the core game logic
 * @author Oscar Sykes 300486149
 */
public class Board {

    /**
     * A 2-d array storing all the tiles on the board
     */
    private Tile[][] tiles;

    /**
     * The character that is controlled by the player
     */
    private Chip chip;

    /**
     * A list of user generated characters defined in the Persistence module
     */
    private List<UserGeneratedCharacter> userGenerateCharacters = new ArrayList<>();

    /**
     * The amount of treasure collected so far, the amount of treasure remaining and the amount of treasure there was
     * initially
     */
    private int treasureRemainingAmount = 0, treasureCollectedAmount = 0;

    /**
     * The amount of user generated characters on the board and the initial number of treasure
     */
    private final int initialTreasureAmount;

    /**
     * The total amount of user generated characters
     */
    private final int characterAmount;

    /**
     * The number of columns and rows on the board respectively
     */
    private int boardWidth, boardHeight;

    /**
     * Records whether treasure or a key was collected last turn so application knows when to play item related music.
     * These are reset after each move.
     */
    private boolean treasureCollected = false, keyCollected = false;

    /**
     * Indicates whether the user has completed the level
     */
    private boolean levelFinished = false;

    /**
     * Indicates whether the character is standing on the InfoField tile
     */
    private boolean onInfoTile = false;

    /**
     * A list of all the moves chip has completed in the game so far
     */
    private List<String> chipMoves = new ArrayList<>();

    /**
     * A list of all the moves the other user defined characters have done.
     */
    private List<List<String>> otherCharactersMoves = new ArrayList<>();


    /**
     * Represents whether chip is still alive. Chip dies when a bug lands on the same tile as him.
     */
    private boolean chipAlive = true;

    /**
     * Creates the tiles array using the JsonParser module
     * Initialises relevant fields
     * @param levelNum number of the level the user wants to play. The first two levels are used for testing and the
     *                 rest are actual levels
     */
    public Board(int levelNum) {
        Preconditions.checkArgument(levelNum == -1 || levelNum == 0 || levelNum == 1 || levelNum == 2);
        // make a temporary board in case it doesn't pass preconditions checks
        Tile[][] tempBoard = Preconditions.checkNotNull(JsonParser.getLvl(levelNum));
        Preconditions.checkArgument(tempBoard[0].length > 0);
        Preconditions.checkArgument(tempBoard.length > 0);
        tiles = JsonParser.getLvl(levelNum);
        boardWidth = tiles[0].length;
        boardHeight = tiles.length;
        int characters = 0;
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                if (tiles[i][j] instanceof FreeTile && ((FreeTile) tiles[i][j]).getItem() instanceof Treasure) {
                    treasureRemainingAmount++;
                }
                Character character = tiles[i][j].getCharacter();
                if (character instanceof Chip) {
                    chip = (Chip) tiles[i][j].getCharacter();
                } else if (character instanceof UserGeneratedCharacter){
                    userGenerateCharacters.add((UserGeneratedCharacter) character);
                    characters++;
                }
            }
        }
        for(int i = 0; i < characters; i++){
            otherCharactersMoves.add(new ArrayList<>());
        }
        characterAmount = characters;
        initialTreasureAmount = treasureRemainingAmount;
        assert !tilesContainsNull();
        assert tilesContainsOneChip();
        assert checkCharacterAmount();
    }

    /**
     * Returns the tile array. It creates a duplicate object for encapsulation.
     * @return the tile array used in the game
     */
    public Tile[][] getTileArray() {
        Tile[][] tilesCopy = new Tile[boardHeight][boardWidth];
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                tilesCopy[i][j] = tiles[i][j];
            }
        }
        return tilesCopy;
    }

    /**
     * @return the chip object
     */
    public Chip getChip() {
        assert chip != null;
        return chip;
    }

    /**
     * @return the characters added by the persistence module
     */
    public List<UserGeneratedCharacter> getCharacters(){
        return userGenerateCharacters;
    }

    /**
     * @return the number of columns of tiles
     */
    public int getBoardWidth(){ return boardWidth; }

    /**
     * @return the number of rows of tiles
     */
    public int getBoardHeight(){ return boardHeight; }

    /**
     * @return the list of moves done by chip so far
     */
    public List<String> getChipMoves(){
        return chipMoves;
    }

    /**
     * @return the list of moves done by all non-chip characters so far
     */
    public List<List<String>> getOtherCharacterMoves(){
        return otherCharactersMoves;
    }

    /**
     * Clears the list of chips moves and the other characters moves
     */
    public void clearMoves(){
        chipMoves = new ArrayList<>();
        for(int i = 0; i < otherCharactersMoves.size(); i++){
            otherCharactersMoves.set(i, new ArrayList<>());
        }
    }

    /**
     * @return whether treasure was collected last turn
     */
    public boolean isTreasureCollected(){
        return treasureCollected;
    }

    /**
     * @return whether a key was collected last turn
     */
    public boolean isKeyCollected(){
        return keyCollected;
    }

    /**
     * @return the amount of treasure remaining on the board
     */
    public int getTreasureRemainingAmount() {
        return treasureRemainingAmount;
    }

    /**
     * @return the amount of treasure chip has collected so far
     */
    public int getTreasureCollectedAmount() {
        return treasureCollectedAmount;
    }

    /**
     * @return whether the user has completed the current level
     */
    public boolean isLevelFinished() {
        return levelFinished;
    }

    /**
     * @return whether the character is standing on an InfoField tile
     */
    public boolean onInfoTile() {
        return onInfoTile;
    }

    /**
     * @return whether chip is alive
     */
    public boolean isChipAlive(){
        return chipAlive;
    }

    /**
     * Moves chip and then moves all the other characters in the game
     *
     * @param dir direction to move chap in (left, right, up, down)
     * @return whether or not the move was valid
     */
    public boolean moveChip(String dir) {
        Preconditions.checkNotNull(dir);
        Preconditions.checkArgument(dir.equals("left") || dir.equals("right")
                || dir.equals("up") || dir.equals("down"));
        if (levelFinished || !chipAlive) {
            return false; // don't allow players to move once level is finished
        }
        boolean chipMoveSuccessful = executeMove(dir, chip);
        if(!chipMoveSuccessful){
            return false;
        }
        for(int i = 0; i < userGenerateCharacters.size(); i++){
            // repeatedly call the characters move method until you get a valid move
            boolean successfulMove = false;
            UserGeneratedCharacter character = userGenerateCharacters.get(i);
            while(!successfulMove){
                successfulMove = executeMove(character.move(), character);
            }
        }

        assert tilesContainsOneChip();
        assert !tilesContainsNull();
        assert checkCharacterAmount();
        return true;
    }

    /**
     * This method is used to allow the user to move by clicking the board. It
     * checks if the tile pressed is one tile away from chips current tile and
     * if so calls the moveChip method.
     *
     * @param row row of the tile that the user clicked
     * @param col column of the tile the user clicked
     * @return true if the movement is a success
     */
    public boolean moveByMouse(int row, int col) {
        // make sure chip isn't moving off the board
        if (row < 0 || row >= boardHeight || col < 0 || col >= boardWidth) {
            return false;
        }
        int dy = row - chip.getRow();
        int dx = col - chip.getCol();
        if (!((Math.abs(dx) == 1 && Math.abs(dy) == 0) || (Math.abs(dx) == 0 && Math.abs(dy) == 1))) {
            return false; // only allow chip to move one square
        }
        String dir = null;
        if (dx == -1) {
            dir = "left";
        } else if (dx == 1) {
            dir = "right";
        } else if (dy == -1) {
            dir = "up";
        } else if (dy == 1) {
            dir = "down";
        }
        assert dir != null;
        return moveChip(dir);
    }

    /**
     * This method converts the direction to the row and column the character is trying to move to
     * It then checks whether the character is not trying to move off the board
     * It then calls either executeChipMove or executeOtherCharacterMove depending on the type of character
     * If the move was successful then the move is stored
     * @param dir the direction the character is trying to move in (left, right, up, down)
     * @param character the character trying to move
     * @return whether the move was valid or not
     */
    public boolean executeMove(String dir, Character character){
        Preconditions.checkNotNull(dir);
        Preconditions.checkArgument(dir.equals("left") || dir.equals("right") || dir.equals("up")
                || dir.equals("down") || dir.equals("noMove"));
        Preconditions.checkNotNull(character);

        int row = character.getRow();
        int col = character.getCol();
        int newRow = row;
        int newCol = col;
        if (dir.equals("left")) {
            newCol = col - 1;
        } else if (dir.equals("right")) {
            newCol = col + 1;
        } else if (dir.equals("up")) {
            newRow = row - 1;
        } else if (dir.equals("down")) {
            newRow = row + 1;
        }
        if (newCol < 0 || newCol >= boardWidth || newRow < 0 || newRow >= boardHeight) {
            return false;
        }


        boolean successfulMove = false;
        if(character instanceof Chip){
            successfulMove = executeChipMove(newRow, newCol);
            if(successfulMove){
                chipMoves.add(dir);
            }

        }else if(character instanceof UserGeneratedCharacter){
            successfulMove = executeOtherCharacterMove(character, newRow, newCol);
            if(successfulMove) {
                int index = userGenerateCharacters.indexOf(character);
                otherCharactersMoves.get(index).add(dir);
            }
        }

        // make sure chip has only moved one square or is not moving
        int totalChange = Math.abs(row + col - newRow - newCol);
        assert totalChange == 1 || (totalChange == 0 && dir.equals("noMove"));
        return successfulMove;
    }

    /**
     * Moves chip one tile on the board
     * It removes him from the tile he was previously on and adds him to the new tile
     * It also updates the row and column fields inside the chip object
     * Updates key and treasure fields if chip lands on them
     * If chip moves to a tile with an item the item gets removed from the tile
     * Updates levelFinished, chipAlive and onInfoTile game state changes
     *
     * @param newCol the column of the tile chip is trying to move to
     * @param newRow the row of the tile chip is trying to move to
     *
     * @return whether or not the move was valid
     */
    private boolean executeChipMove(int newRow, int newCol){
        Preconditions.checkArgument(newRow >= 0 && newRow < boardHeight &&newCol >= 0 && newCol < boardWidth);
        Preconditions.checkNotNull(chip);
        Tile oldTile = tiles[chip.getRow()][chip.getCol()];
        Tile newTile = tiles[newRow][newCol];
        onInfoTile = false;
        treasureCollected = false;
        keyCollected = false;

        // after chip moves off a SingleUseTile it turns into a WallTile
        if(oldTile instanceof SingleUseTile){
            tiles[chip.getRow()][chip.getCol()] = new WallTile();
            oldTile = tiles[chip.getRow()][chip.getCol()]; // update pointer
        }

        if (newTile instanceof FreeTile) {
            Item item = ((FreeTile) newTile).getItem();
            if (item instanceof ExitLock && treasureRemainingAmount > 0) {
                return false;
            } else if (item instanceof Key) {
                List<Key> inventory = chip.getKeys();
                int initialSize = inventory.size();
                chip.addKey((Key) Preconditions.checkNotNull(item));
                keyCollected = true;
                assert inventory.size() - initialSize == 1;
                assert inventory.get(inventory.size() - 1).getColour() == ((Key) item).getColour();
            } else if (item instanceof Treasure) {
                treasureCollectedAmount++;
                treasureRemainingAmount--;
                treasureCollected = true;
            }
            ((FreeTile) newTile).setItem(null); // remove item from tile
        } else if (newTile instanceof LockedDoor) {
            // don't allow chip to move onto a locked door if he doesn't have the corresponding key
            if (!chip.containsKey(((LockedDoor) newTile).getColour())) {
                return false;
            }
            int keySize = chip.getKeys().size();
            chip.removeKey(((LockedDoor) newTile).getColour()); // remove key from inventory
            assert keySize - chip.getKeys().size() == 1;
            tiles[newRow][newCol] = new FreeTile(); // moving to a LockedDoor changes it to a FreeTile
            newTile = tiles[newRow][newCol]; // update pointer
            assert newTile instanceof FreeTile;
        } else if (newTile instanceof Exit) {
            assert treasureRemainingAmount == 0; // this should be 0 or else they wouldn't be able to get through lock
            levelFinished = true;
        } else if (newTile instanceof WallTile) {
            return false; // can't move to the WallTile or move on top of bug
        } else if (newTile instanceof InfoField) {
            onInfoTile = true;
        } else if (!(newTile instanceof SingleUseTile)){
            assert false; // no other possible square types to move to
        }
        if(newTile.getCharacter() instanceof UserGeneratedCharacter) {
            return false; // can't move on top of bugs
        }

        // move chip
        oldTile.setCharacter(null);
        newTile.setCharacter(chip);
        chip.setRow(newRow);
        chip.setCol(newCol);

        // post conditions
        assert tilesContainsOneChip();
        assert !tilesContainsNull();
        assert checkCharacterAmount();
        assert initialTreasureAmount == treasureCollectedAmount + treasureRemainingAmount;
        assert treasureRemainingAmount >= 0;
        assert !(newTile instanceof LockedDoor) && !(newTile instanceof WallTile);
        return true;
    }

    /**
     * Method that moves any character except chip. This only allows them to move onto free tiles
     * If moves them out of the tile they were previously in and into their new tile
     * It also updates the row and column inside the character
     * If they move on top of chip chip dies
     * @param character the character being moved
     * @param newRow the row the character is moving to
     * @param newCol the column the character is moving to
     * @return whether the move was successful
     */
    private boolean executeOtherCharacterMove(Character character, int newRow, int newCol){
        Preconditions.checkNotNull(character);
        Preconditions.checkArgument(character instanceof UserGeneratedCharacter);
        Preconditions.checkArgument(newRow >= 0 && newRow < boardHeight &&newCol >= 0 && newCol < boardWidth);
        int row = character.getRow();
        int col = character.getCol();
        Tile oldTile = tiles[row][col];
        Tile newTile = tiles[newRow][newCol];
        if(!(newTile instanceof FreeTile) || (((FreeTile) newTile).getItem()) != null){
            return false;
        }
        if(newTile.getCharacter() instanceof Chip){
            chipAlive = false; // if a character walks on top of chip then he dies and has to start again
        }

        // move character
        oldTile.setCharacter(null);
        newTile.setCharacter(character);
        character.setRow(newRow);
        character.setCol(newCol);

        // post conditions
        assert tilesContainsOneChip();
        assert !tilesContainsNull();
        assert checkCharacterAmount();
        return true;
    }

    /**
     * @return converts the board to a string
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < boardHeight; i++) {
            s += "|";
            for (int j = 0; j < boardWidth; j++) {
                s += tiles[i][j].getLetter() + "|";
            }
            s += "\n";
        }
        return s;
    }

    /**
     * @return whether any tiles in the tile array are null objects
     */
    private boolean tilesContainsNull(){
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if(tiles[i][j] == null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the tile array contains exactly one chip and if it doesn't whether he is dead as if a bug goes on
     * top of chip he dies
     * @return whether the tile array contains chip
     */
    private boolean tilesContainsOneChip(){
        int chipNum = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if(tiles[i][j].getCharacter() instanceof Chip){
                    chipNum++;
                }
            }
        }
        return chipNum == 1 || (chipNum == 0 && !chipAlive);
    }

    /**
     * @return whether the amount of non-chip characters on the board is the same as the starting amount
     */
    private boolean checkCharacterAmount(){
        int characterNum = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if(tiles[i][j].getCharacter() instanceof UserGeneratedCharacter){
                    characterNum++;
                }
            }
        }
        return characterNum == characterAmount;
    }
}
