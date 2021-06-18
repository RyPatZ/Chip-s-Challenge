package Maze;

import Maze.Items.ExitLock;
import Maze.Items.Key;
import Maze.Items.Treasure;
import Maze.Tiles.*;
import org.junit.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A set of unit tests that testing the maze module
 * All lines of code in the module are covered except for lines which are intentionally unreachable
 *
 * @author Oscar Sykes 300486149
 */
public class MazeTests {

    /**
     * Tests that chip can move in all directions and lands back on the square it started on
     */
    @Test
    public void testBasicMoves() {
        Board board = new Board(-1);
        Chip chip = board.getChip();
        Tile initialTile = board.getTileArray()[chip.getRow()][chip.getCol()];
        multipleMoves(board, "ULRD");
        assertEquals(board.getTileArray()[chip.getRow()][chip.getCol()], initialTile);
    }

    /**
     * Test that there is an assertion error if invalid input is given to the move chip method
     */
    @Test
    public void testInvalidMove() {
        Board board = new Board(-1);
        try {
            board.moveChip("invalid input");
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    /**
     * Simulates a full game from start to finish
     * It tests that fields related to keys and treasure update correctly
     * Checks that you can't move after you have finished a game
     */
    @Test
    public void testWin() {
        Board board = new Board(-1);
        Chip chip = board.getChip();
        multipleMoves(board, "RRR");
        assertEquals(Color.green, chip.getKeys().get(0).getColour());
        multipleMoves(board, "LLD");
        assertEquals(0, chip.getKeys().size());
        multipleMoves(board, "DDRR");
        assertEquals(4, board.getTreasureRemainingAmount());
        assertEquals(1, board.getTreasureCollectedAmount());
        multipleMoves(board, "LLUUUURUURRLLDDDLLLLLRRDDDLRUUUULUULRDDRR");
        multipleMoves(board, "UU");
        assertTrue(board.isLevelFinished());
        assertFalse(board.moveChip("down"));
    }

    /**
     * Check that the onInfo tile is true when chip is standing on an InfoTile and false otherwise
     */
    @Test
    public void testOnInfo(){
        Board board = new Board(-1);
        assertFalse(board.onInfoTile());
        board.moveChip("up");
        assertTrue(board.onInfoTile());
        board.moveChip("down");
        assertFalse(board.onInfoTile());
    }

    /**
     * Check that LockedDoor tiles turn to FreeTiles when stood on
     */
    @Test
    public void lockedToUnlocked(){
        Board board = new Board(-1);
        Chip chip = board.getChip();
        multipleMoves(board, "RRRLL");
        assertTrue(board.getTileArray()[chip.getRow() + 1][chip.getCol()] instanceof LockedDoor);
        board.moveChip("down");
        assertTrue(board.getTileArray()[chip.getRow() - 1][chip.getCol()] instanceof FreeTile);
    }

    /**
     * Checks that you can't move on to a tile with an ExitLock if you haven't collected all the treasure
     */
    @Test
    public void invalidExitLock() {
        Board board = new Board(-1);
        board.moveChip("up");
        assertFalse(board.moveChip("up"));
    }

    /**
     * Checks that you can't walk onto a wall
     */
    @Test
    public void walkIntoWall() {
        Board board = new Board(-1);
        multipleMoves(board, "RRRR");
        assertFalse(board.moveChip("right"));
    }

    /**
     * Checks that you can't walk off the board using moveChip method
     */
    @Test
    public void attemptToGoOffBoard() {
        Board board = new Board(0);
        String moves = "LDDDD";
        multipleMoves(board, moves);
        assertFalse(board.moveChip("down"));
    }

    /**
     * Checks that you can't walk off the board using moveByMouse method
     */
    @Test
    public void offBoardByMouse() {
        Board board = new Board(0);
        Chip chip = board.getChip();
        board.moveByMouse(chip.getRow(), chip.getCol() - 1);
        for (int i = 0; i < 4; i++) {
            board.moveByMouse(chip.getRow() + 1, chip.getCol());
        }
        assertFalse(board.moveByMouse(chip.getRow() + 1, chip.getCol()));
    }

    /**
     * Checks that you can't move more than you can't move two squares in one turn
     */
    @Test
    public void invalidMouseMovement() {
        Board board = new Board(0);
        Chip chip = board.getChip();
        assertFalse(board.moveByMouse(chip.getRow(), chip.getCol() - 2));
    }

    /**
     * Checks that you can't move more than you can't move no squares in one turn
     */
    @Test
    public void invalidMouseMovement2() {
        Board board = new Board(0);
        Chip chip = board.getChip();
        assertFalse(board.moveByMouse(chip.getRow(), chip.getCol()));
    }

    /**
     * Checks that moveByMouse method works correctly
     */
    @Test
    public void moveByMouse() {
        Board board = new Board(-1);
        Chip chip = board.getChip();
        int row = chip.getRow();
        int col = chip.getCol();
        board.moveByMouse(row, col + 1);
        board.moveByMouse(row, col);
        board.moveByMouse(row - 1, col);
        assertTrue(board.getTileArray()[row - 1][col].containsChip());
    }

    /**
     * Check that SingleUseTiles turn into WallTiles after chip moves off them
     */
    @Test
    public void testSingleUseTiles(){
        Board board = new Board(2);
        String moves = "RRRRRDDUULLLLLDDDDDDDDDDDL";
        multipleMoves(board, moves);
        Chip chip = board.getChip();
        assertTrue(board.getTileArray()[chip.getRow()][chip.getCol() - 1] instanceof SingleUseTile);
        multipleMoves(board, "LL");
        assertTrue(board.getTileArray()[chip.getRow()][chip.getCol() + 1] instanceof WallTile);
    }

    /**
     * Check that the bug has moved to the correct tile
     */
    @Test
    public void testBugMovement(){
        Board board = new Board(2);
        Character bug = board.getCharacters().get(0);
        int row = bug.getRow();
        int col = bug.getCol();
        multipleMoves(board, "RRRRRRR");
        for(String move: board.getOtherCharacterMoves().get(0)){
            if(move.equals("left")){
                col--;
            }else if(move.equals("right")){
                col++;
            }else if(move.equals("up")){
                row--;
            }else if(move.equals("down")){
                row++;
            }
        }
        assertTrue(board.getTileArray()[row][col].getCharacter() instanceof UserGeneratedCharacter);
    }

    /**
     * Check that chip dies when a bug walks on top of him.
     * The bugs movements are random so the test continually moves chip left and right until the bug is on him
     */
    @Test
    public void testChipDead(){
        Board board = new Board(2);
        Chip chip = board.getChip();
        UserGeneratedCharacter bug = board.getCharacters().get(0);
        int chipRow = chip.getRow();
        int chipCol = chip.getCol();
        int bugRow = bug.getRow();
        int bugCol = bug.getCol();
        assertTrue(board.isChipAlive());
        String moves = "RRRRRDDUULLLLLLL";
        multipleMoves(board, moves);
        while(chipRow != bugRow || chipCol != bugCol){
            board.moveChip("left");
            chipRow = chip.getRow();
            chipCol = chip.getCol();
            bugRow = bug.getRow();
            bugCol = bug.getCol();
            if(chipRow == bugRow && chipCol == bugCol){
                break;
            }
            board.moveChip("right");
            chipRow = chip.getRow();
            chipCol = chip.getCol();
            bugRow = bug.getRow();
            bugCol = bug.getCol();
        }
        assertFalse(board.isChipAlive());
    }

    /**
     * Checks that add and remove key methods function properly
     */
    @Test
    public void checkKeys() {
        Chip chip = new Chip(0, 0);
        chip.addKey(new Key(Color.blue));
        assertTrue(chip.containsKey(Color.blue));
        chip.removeKey(Color.blue);
        assertFalse(chip.containsKey(Color.blue));
        assertEquals(chip.getKeys().size(), 0);
    }

    /**
     * Make sure chip can't enter a door with without a key
     */
    @Test
    public void invalidLockedDoor(){
        Board board = new Board(-1);
        board.moveChip("right");
        assertFalse(board.moveChip("down"));
    }

    /**
     * Checks that the getColour method works correctly
     */
    @Test
    public void checkColour() {
        assertEquals(new Key(Color.blue).getColour(), Color.blue);
        assertEquals(new LockedDoor(Color.blue).getColour(), Color.blue);
    }

    /**
     * Checks that the getChipMoves method returns a list with moves completed
     */
    @Test
    public void checkMoves(){
        Board board = new Board(2);
        multipleMoves(board, "RU");
        List<String> moves = board.getChipMoves();
        assertEquals("right", moves.get(0));
        assertEquals("up", moves.get(1));
        List<List<String>> otherCharactersMoves = board.getOtherCharacterMoves();
        assertEquals(2, otherCharactersMoves.get(0).size());
        board.clearMoves();
        assertEquals(0, board.getChipMoves().size());
        List<List<String>> otherCharacters = board.getOtherCharacterMoves();
        for(int i = 0; i < otherCharacters.size(); i++) {
            assertEquals(0, otherCharacters.get(i).size());
        }
    }

    /**
     * Check that the keyCollected and treasureCollected fields are true when treasure and keys are collected and false
     * otherwise
     */
    @Test
    public void itemsCollected(){
        Board board = new Board(-1);
        multipleMoves(board, "RRR");
        assertTrue(board.isKeyCollected());
        board.moveChip("left");
        assertFalse(board.isKeyCollected());
        multipleMoves(board, "LLLLL");
        assertTrue(board.isTreasureCollected());
        board.moveChip("right");
        assertFalse(board.isTreasureCollected());
    }

    /**
     * Checks that the toString method in the board class works correctly
     */
    @Test
    public void checkBoardToString() {
        Board board = new Board(-1);
        board.moveChip("right");
        board.moveChip("right");
        String expected = "|W|W|W|W|W|W|W|W|W|W|\n" +
                "|W|C| |W|E|W| |b|C|W|\n" +
                "|W|W|R|W|X|W|R|W|W|W|\n" +
                "|W| | | |I| | | | |W|\n" +
                "|W|C| | | | |H|g| |W|\n" +
                "|W|W|W|B|W|G|W|W|W|W|\n" +
                "|W| | |r|W| | | | |W|\n" +
                "|W| |C| |W|r| |C| |W|\n" +
                "|W| | | |W| | | | |W|\n" +
                "|W|W|W|W|W|W|W|W|W|W|\n";
        assertEquals(expected, board.toString());
        // test getLetter for objects not in map
        assertEquals("y", new Key(Color.yellow).getLetter());
        assertEquals("Y", new LockedDoor(Color.yellow).getLetter());
    }

    /**
     * Check that the dimensions of the board are correct
     */
    @Test
    public void checkDimensions(){
        Board board = new Board(2);
        assertEquals(20, board.getBoardWidth());
        assertEquals(19, board.getBoardHeight());
    }

    /**
     * Checks that the toString methods in the Item and Tile classes work correctly
     */
    @Test
    public void checkItemToString() {
        assertEquals(new Exit().toString(), "ExitTile");
        assertEquals(new FreeTile().toString(), "FreeTile");
        assertEquals(new InfoField().toString(), "InfoField");
        assertEquals(new LockedDoor(Color.blue).toString(), "LockedDoorBlue");
        assertEquals(new LockedDoor(Color.red).toString(), "LockedDoorRed");
        assertEquals(new LockedDoor(Color.green).toString(), "LockedDoorGreen");
        assertEquals(new LockedDoor(Color.yellow).toString(), "LockedDoorYellow");
        assertEquals(new WallTile().toString(), "WallTile");
        assertEquals(new Key(Color.blue).toString(), "KeyBlue");
        assertEquals(new Key(Color.red).toString(), "KeyRed");
        assertEquals(new Key(Color.green).toString(), "KeyGreen");
        assertEquals(new Key(Color.yellow).toString(), "KeyYellow");
        assertEquals(new ExitLock().toString(), "ExitLock");
        assertEquals(new Treasure().toString(), "Treasure");
        assertEquals(new SingleUseTile().toString(), "SingleUseTile");
    }

    /**
     * This method moves chip according to some string of letters.
     * If one move is invalid it throws an assertion exception.
     * @param board the board the moves will be done on
     * @param moves the sequence of moves to be executed
     */
    public void multipleMoves(Board board, String moves) {
        for (int i = 0; i < moves.length(); i++) {
            char c = moves.charAt(i);
            String move = "";
            if (c == 'L') {
                move = "left";
            } else if (c == 'R') {
                move = "right";
            } else if (c == 'U') {
                move = "up";
            } else if (c == 'D') {
                move = "down";
            }
            assert board.moveChip(move);
        }
    }
}
