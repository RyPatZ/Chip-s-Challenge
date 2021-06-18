package MonkeyTests;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import Maze.Board;
import Maze.Chip;
import Maze.UserGeneratedCharacter;
import Maze.Items.ExitLock;
import Maze.Items.Item;
import Maze.Items.Key;
import Maze.Items.Treasure;
import Maze.Tiles.Exit;
import Maze.Tiles.FreeTile;
import Maze.Tiles.InfoField;
import Maze.Tiles.LockedDoor;
import Maze.Tiles.Tile;
import MonkeyTests.strategy.Context;

/**
 * Description: <br/>
 * Test for the Maze packet, use assert methods to check.
 * 
 * @author Yun Zhou 300442776
 */
public class MazeTest extends Helper {

    /**
     * Description: <br/>
     * Test that the chip can move to the infofield tile.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_moveInfoField() {
        Board board = getLevel0Board();//
        mapBoardTiles = board.getTileArray();
        InfoField infoFreeTile = (InfoField) mapBoardTiles[3][4];
        assertFalse(infoFreeTile.containsChip());
        // the context has already assigned in getLevel0Board method
        context.executeMovements(board, board.getChip(), "w");
        assertTrue(infoFreeTile.containsChip());
    }

    /**
     * Description: <br/>
     * A test for checking the colorLetter string/letter is matched.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_checkItemStrings() {

        // check getLetter()
        for (Color validColor : getValidColorList()) {
            Item keyItem = new Key(validColor);
            String colorLetter = "";
            if (validColor.equals(Color.red)) {
                colorLetter = "r";
            } else if (validColor.equals(Color.green)) {
                colorLetter = "g";
            } else if (validColor.equals(Color.blue)) {
                colorLetter = "b";
            } else if (validColor.equals(Color.yellow)) {
                colorLetter = "y";
            }
            assert keyItem.getLetter().equals(colorLetter);
        }
        // check toString()
        for (Color validColor : getValidColorList()) {
            Key key = new Key(validColor);
            String keyString = "";
            if (validColor.equals(Color.red)) {
                keyString = "KeyRed";
            } else if (validColor.equals(Color.green)) {
                keyString = "KeyGreen";
            } else if (validColor.equals(Color.blue)) {
                keyString = "KeyBlue";
            } else if (validColor.equals(Color.yellow)) {
                keyString = "KeyYellow";
            }
            assert key.toString().equals(keyString);

        }

    }

    /**
     * Description: <br/>
     * Test valid movement of the Chip. That is, check that location has changed.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testChipMovement_valid() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        // test whether chip is on the correct position of the Board
        assertTrue(mapBoardTiles[4][4].containsChip());
        // test whether chip can move to the InfoField Tile
        context.executeMovements(board, board.getChip(), "a");
        assertTrue(mapBoardTiles[4][3].containsChip() && !mapBoardTiles[4][4].containsChip());
    }

    /**
     * Description: <br/>
     * Test invalid movement of the Chip. That is, check that the location should remain the
     * same as the destination is invalid.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testChipMovement_invalid() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        context.executeMovements(board, board.getChip(), "aa");
        assertTrue(mapBoardTiles[4][2].containsChip());
        context.executeMovements(board, board.getChip(), "a");// try to go to the WallTile
        assertTrue(
                mapBoardTiles[4][2].containsChip() && mapBoardTiles[4][1].containsChip() == false);
    }

    /**
     * Description: <br/>
     * Test that if the valid movement by mouse can be made. AND check whether it drives the
     * chip go to the correct position.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testMovementByMouse_isSucess() {
        Board board = getLevel0Board();
        Chip chip = board.getChip();
        assertTrue(chip.getRow() == 4 && chip.getCol() == 4);

        board.moveByMouse(chip.getRow() + 1, chip.getCol());
        assertTrue(chip.getRow() == 4 + 1 && chip.getCol() == 4);

        board.moveByMouse(chip.getRow() - 1, chip.getCol());
        assertTrue(chip.getRow() == 4 + 1 - 1 && chip.getCol() == 4);

        board.moveByMouse(chip.getRow(), chip.getCol() + 1);
        assertTrue(chip.getRow() == 4 + 1 - 1 && chip.getCol() == 4 + 1);

        board.moveByMouse(chip.getRow(), chip.getCol() - 1);
        assertTrue(chip.getRow() == 4 + 1 - 1 && chip.getCol() == 4 + 1 - 1);
    }

    /**
     * Description: <br/>
     * Test that the invalid movement by mouse cannot be made.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testMovementByMouse_invalid() {
        Board board = getLevel0Board();
        Chip chip = board.getChip();
        boolean isSucess = false;
        try {
            assertFalse(board.moveByMouse(chip.getRow() + 2, chip.getCol()));
            assertFalse(board.moveByMouse(chip.getRow() - 2, chip.getCol()));
            assertFalse(board.moveByMouse(chip.getRow(), chip.getCol() + 2));
            assertFalse(board.moveByMouse(chip.getRow(), chip.getCol() - 2));
            assertFalse(board.moveByMouse(chip.getRow() + 1, chip.getCol() + 1));
            assertFalse(board.moveByMouse(chip.getRow() + 1, chip.getCol() - 1));
            assertFalse(board.moveByMouse(chip.getRow() - 1, chip.getCol() + 1));
            assertFalse(board.moveByMouse(chip.getRow() - 1, chip.getCol() - 1));
            isSucess = true;
        } catch (IllegalArgumentException e) {
            /*
             * the index is invalid, so it should throw the IllegalArgumentException.
             */
            assert false;
        } finally {
            assert isSucess;
        }
    }

    /**
     * Description: <br/>
     * Test whether it will throw the IndexOutOfBoundsException when the player click out of
     * the Bounds index.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testMovementByMouse_OutOfBounds() {
        Board board = getLevel0Board();
        try {
            assertFalse(board.moveByMouse(-1, 0));
            assertFalse(board.moveByMouse(0, -1));
            assertFalse(board.moveByMouse(9, 0));
            assertFalse(board.moveByMouse(0, 9));
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Index out of Bounds exception!");
            assert false;
        }
    }

    /**
     * Description: <br/>
     * Test the boundry of the map. Check whether the chip is still on the Board or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testBoundsMovementSouth() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();

        context.executeMovements(board, board.getChip(), "sss");
        assertTrue(mapBoardTiles[7][4].containsChip());
        context.executeMovements(board, board.getChip(), "assss");
        assertTrue(mapBoardTiles[8][3].containsChip());

    }

    /**
     * Description: <br/>
     * Test the boundry of the map. Check whether the chip is still on the Board or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testBoundsMovementNorth() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        board.getChip().setCol(0);
        board.getChip().setRow(0);
        boolean isValid = context.executeMovements(board, board.getChip(), "w");
        assertFalse(isValid);

    }

    /**
     * Description: <br/>
     * Test the boundry of the map. Check whether the chip is still on the Board or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testBoundsMovementLeft() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        board.getChip().setCol(0);
        board.getChip().setRow(0);
        boolean isValid = context.executeMovements(board, board.getChip(), "a");
        assertFalse(isValid);

    }

    /**
     * Description: <br/>
     * Test the boundry of the map. Check whether the chip is still on the Board or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testBoundsMovementRight() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        board.getChip().setCol(8);
        board.getChip().setRow(8);
        boolean isValid = context.executeMovements(board, board.getChip(), "d");
        assertFalse(isValid);

    }

    /**
     * Description: <br/>
     * After several movements, check whether there is only one Chip on the Board.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testOneChipOnBoard() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        context.executeMovements(board, board.getChip(), "awad");
        board.getChip().setCol(0);
        board.getChip().setRow(0);
        int chip_number = 0;
        for (int i = 0; i < mapBoardTiles.length; i++) {
            for (int j = 0; j < mapBoardTiles[i].length; j++) {
                if (mapBoardTiles[i][j].containsChip()) {
                    // System.out.println(
                    // "hi:" + board.getChip().getCol() + "\t" +
                    // board.getChip().getRow());
                    chip_number++;
                }
            }
        }
        assertTrue(chip_number == 1);
    }

    /**
     * Description: <br/>
     * Test that the treasure is collected by the player successfully.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testCollectTreasure() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        FreeTile freeTile = (FreeTile) mapBoardTiles[6][4];

        // check that the treasure is here
        assertTrue(freeTile.getItem() instanceof Treasure);
        // move down to pick up the Treasure
        context.executeMovements(board, board.getChip(), "sss");
        // check that the treasure is GONE
        assertFalse(freeTile.getItem() instanceof Treasure);

    }

    /**
     * Description: <br/>
     * Test that the treasure numbers are correct.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testTreasureNumber() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        FreeTile freeTile = (FreeTile) mapBoardTiles[6][4];
        int origTreasureNumber = board.getTreasureRemainingAmount();
        assert origTreasureNumber > 0;// treasure must be positive!

        // check that the treasure is here
        assertTrue(freeTile.getItem() instanceof Treasure);
        // move down to pick up the Treasure
        context.executeMovements(board, board.getChip(), "sss");
        // check that the treasure is GONE
        assertFalse(freeTile.getItem() instanceof Treasure);
        /*
         * check that the number of treasures are positive and be subtracted from the Board
         */
        if (origTreasureNumber > board.getTreasureRemainingAmount()
                && origTreasureNumber == board.getTreasureRemainingAmount() + 1
                && board.getTreasureRemainingAmount() >= 0) {
            assert true;
        } else {
            // something went wrong, assert false
            assert false;
            System.err.println("treasure number is incorrect, need to fix.");
        }

    }

    /**
     * Description: <br/>
     * Test whether the key is picked up or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testPickUpValidKey() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        Chip chip = board.getChip();
        assertFalse(chip.containsKey(Color.red));
        assertTrue(chip.getKeys().isEmpty());// check that the key list is empty

        FreeTile keyTile = (FreeTile) mapBoardTiles[5][6];
        assertTrue(keyTile.getItem() instanceof Key
                && ((Key) keyTile.getItem()).getColour().equals(Color.red));
        context.executeMovements(board, board.getChip(), "sdd");// pick the red key
        context.executeMovements(board, board.getChip(), "s");
        /* check whether the key has be picked up or not */
        // check if it's still on Board or not
        assertFalse(keyTile.getItem() instanceof Key);
        // check if the Chip got the Key in his keyList
        assertTrue(chip.containsKey(Color.red));
        assertTrue(chip.getKeys().size() == 1);// check that the key list is 1

    }

    /**
     * Description: <br/>
     * Test that it is invalid for using the invalid Color key to unlock the door.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testInvalidKey_unlockDoor() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        Chip chip = board.getChip();
        assertFalse(chip.containsKey(Color.red));

        FreeTile keyTile = (FreeTile) mapBoardTiles[5][6];
        assertTrue(keyTile.getItem() instanceof Key
                && ((Key) keyTile.getItem()).getColour().equals(Color.red));
        context.executeMovements(board, board.getChip(), "sdd");// pick the red key
        context.executeMovements(board, board.getChip(), "ds");
        /* check whether the key has be picked up or not */
        // check if it's still on Board or not
        assertFalse(keyTile.getItem() instanceof Key);
        // check if the Chip got the Key in his keyList
        assertTrue(chip.containsKey(Color.red));
        LockedDoor lockedDoor = (LockedDoor) mapBoardTiles[6][7];
        assertTrue(lockedDoor instanceof LockedDoor && lockedDoor.getColour().equals(Color.blue));
        // trying to unlock the Blue Door with red Key
        context.executeMovements(board, board.getChip(), "d");
        assertTrue(lockedDoor instanceof LockedDoor);
    }

    /**
     * Description: <br/>
     * Test using the valid Color key to unlock the specified door. This test will check
     * whether the key has been collected by the player and whether the LockedDoor Tile has
     * changed to FreeTile.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testValidKey_unlockDoor() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        Chip chip = board.getChip();
        assertFalse(chip.containsKey(Color.red));

        FreeTile keyTile = (FreeTile) mapBoardTiles[5][6];
        assertTrue(keyTile.getItem() instanceof Key
                && ((Key) keyTile.getItem()).getColour().equals(Color.red));
        context.executeMovements(board, board.getChip(), "sdd");// pick the red key
        context.executeMovements(board, board.getChip(), "s");
        /*
         * check whether the key has be picked up or not
         */
        // check if it's still on Board or not
        assertFalse(keyTile.getItem() instanceof Key);
        // check if the Chip got the Key in his keyList
        assertTrue(chip.containsKey(Color.red));
        assert chip.getKeys().size() == 1;// check the size of list

        LockedDoor lockedDoor = (LockedDoor) mapBoardTiles[6][1];
        Color lockedDoorColor = lockedDoor.getColour();
        assertTrue(lockedDoor instanceof LockedDoor && lockedDoorColor.equals(Color.red));

        // move left to unlock the red color door
        for (int i = 0; i < 8; i++) {
            context.executeMovements(board, board.getChip(), "a");
        }
        mapBoardTiles = board.getTileArray();// update the pointer
        assertTrue(mapBoardTiles[6][1] instanceof FreeTile);// should be freeTile
        assertFalse(chip.containsKey(Color.red));// the key should gone
        assertTrue(chip.getKeys().isEmpty());// check the keyList size

    }

    /**
     * Description: <br/>
     * A test for checking that the key has gone and chip can not unlock the same color door
     * again.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testNoMultiTimesToUnlockDoor() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();

        // check if they are all locked doors first
        boolean allAreLockedDoor = false;
        if (mapBoardTiles[2][1] instanceof LockedDoor
                && mapBoardTiles[1][2] instanceof LockedDoor) {
            allAreLockedDoor = true;
        }
        assertTrue(allAreLockedDoor);

        context.executeMovements(board, board.getChip(), "AAWWW");// one door should be
                                                                  // unlocked now

        context.executeMovements(board, board.getChip(), "SD");// try to unlock another door

        mapBoardTiles = board.getTileArray();// update the pointer

        // check agagin
        allAreLockedDoor = false;
        if (mapBoardTiles[2][1] instanceof LockedDoor
                && mapBoardTiles[1][2] instanceof LockedDoor) {
            allAreLockedDoor = true;
        }
        // System.out.println(board.toString() + allAreLockedDoor);
        assertFalse(allAreLockedDoor);

    }

    /**
     * Description: <br/>
     * Try to break the ExitLock before collect all treasures. Test that the ExitLock is still
     * there.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testInvalidExitLock() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        FreeTile exitLock = (FreeTile) mapBoardTiles[1][4];

        // check the status of the ExitLock
        boolean isExitLock = (exitLock.getItem() instanceof ExitLock);
        assertTrue(isExitLock); // check if the ExitLock is still here

        // go to the ExitLock
        context.executeMovements(board, board.getChip(), "AWWdww");
        // check the status again
        isExitLock = (exitLock.getItem() instanceof ExitLock);
        assertTrue(isExitLock); // check if the ExitLock is still here

    }

    /**
     * Description: <br/>
     * Collect all treasures in the current level and break the exitlock.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testExitLock() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        FreeTile exitLock = (FreeTile) mapBoardTiles[1][4];

        boolean isExitLock = (exitLock.getItem() instanceof ExitLock);
        assertTrue(isExitLock);// check if the ExitLock is still here

        /*
         * DO all the movements to get all treasures and try to break the ExitLock
         */
        context.executeMovements(board, board.getChip(), "AAsddSddwwwwwwwdAss");
        for (int i = 0; i < 5; i++) {
            context.executeMovements(board, board.getChip(), "a");
        }
        context.executeMovements(board, board.getChip(), "swwwadssddw");

        // check that the ExitLock is gone
        isExitLock = (exitLock.getItem() instanceof ExitLock);

        assertFalse(isExitLock);// check that the ExitLock is gone

    }

    /**
     * Description: <br/>
     * Check whether the level is finished after all treasures has been collected and Chip is
     * on the ExitTile.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testFinishLevel() {
        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        FreeTile exitLock = (FreeTile) mapBoardTiles[1][4];

        boolean isExitLock = (exitLock.getItem() instanceof ExitLock);
        assertTrue(isExitLock);// check if the ExitLock is still here
        /*
         * DO all the movements to get all treasures and try to break the ExitLock
         */
        context.executeMovements(board, board.getChip(), "AAsddSddwwwwwwwdAss");
        for (int i = 0; i < 5; i++) {
            context.executeMovements(board, board.getChip(), "a");
        }
        context.executeMovements(board, board.getChip(), "swwwadssddw");

        assertFalse(board.isLevelFinished());// check it's not finished yet

        boolean isLevelOver = context.executeMovements(board, board.getChip(), "w");
        isExitLock = (exitLock.getItem() instanceof ExitLock);

        /*
         * Check that the ExitLock has gone and the Level is finished as all treasures has gone
         */
        assertFalse(isExitLock);// check the exitlock has gone
        assertTrue(isLevelOver && board.isLevelFinished());// check it's
                                                           // finished

        Exit exit = (Exit) mapBoardTiles[0][4];
        boolean isAtExit = exit.containsChip();
        assertTrue(isAtExit);

        /*
         * the reason why do NOT use stratgy here is because that the stratgy use executeMove()
         * which will not check whether the level is finished, but for the real game,
         * executeMove() does not be invoked directly and only be invoked by board.moveChip(),
         * so it is NOT A BUG.
         */
        // further check that the player can NOT move as the level is finished.
        board.moveChip("down");
        board.moveChip("down");

        exit = (Exit) mapBoardTiles[0][4];// update the pointer
        isAtExit = exit.containsChip();// update the pointer
        // printBoardString(board);
        assertTrue(isAtExit);// check if the chip is still on the Exit

    }

    /**
     * Description: <br/>
     * Test the level number is correct after the current level is finished.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testLevelNumber() {

        Board board = getLevel0Board();
        mapBoardTiles = board.getTileArray();
        FreeTile exitLock = (FreeTile) mapBoardTiles[1][4];

        boolean isExitLock = (exitLock.getItem() instanceof ExitLock);
        assertTrue(isExitLock);// check if the ExitLock is still here

        /*
         * DO all the movements to get all treasures and try to break the ExitLock to go to the
         * Exit
         */
        context.executeMovements(board, board.getChip(), "AAsddSddwwwwwwwdAss");
        for (int i = 0; i < 5; i++) {
            context.executeMovements(board, board.getChip(), "a");
        }
        context.executeMovements(board, board.getChip(), "swwwadssddw");

        assertFalse(board.isLevelFinished());// check it's not finished yet

        boolean isLevelOver = context.executeMovements(board, board.getChip(), "w");
        isExitLock = (exitLock.getItem() instanceof ExitLock);

        /*
         * Check that the ExitLock has gone and the Level is finished as all treasures has gone
         */
        assertFalse(isExitLock);// check the exitlock has gone
        assertTrue(isLevelOver && board.isLevelFinished());// check it's
                                                           // finished

    }

    /**
     * Description: <br/>
     * Test for the number of the User Generate Character which is the bug in the game.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_getCharacters() {
        Board board = getLevel0Board();
        boolean noBug = board.getCharacters().isEmpty();
        assertTrue(noBug);

        board = new Board(2);
        noBug = board.getCharacters().isEmpty();
        assertFalse(noBug);

        int bugNumber = board.getCharacters().size();
        assertTrue(bugNumber > 0);

    }

    /**
     * Description: <br/>
     * Test that the dimention of each game level Board is correct and matched.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testBoardDimention() {
        // loop throug all the game level first
        for (Integer validLevelInteger : getValidGameLevelList()) {
            // then generate the tiles[][]
            Board board = new Board(validLevelInteger);
            Tile[][] tiles = board.getTileArray();
            // apply the check
            int tileArrayWidth = tiles[0].length;
            int tileArrayHeight = tiles.length;

            assert board.getBoardWidth() == tileArrayWidth;
            assert board.getBoardHeight() == tileArrayHeight;

        }
    }

    /**
     * Description: <br/>
     * Check that the key is collected successfully.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_isKeyCollected() {
        Board board = new Board(2);
        context = new Context(multiMoveStrategySpecificied);
        assertFalse(board.isKeyCollected());
        context.executeMovements(board, board.getChip(), "dddddss");
        assertTrue(board.isKeyCollected());

    }

    /**
     * Description: <br/>
     * Checking when the chip touch the bug will die.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_chipisDead() {
        Board board = new Board(2);
        UserGeneratedCharacter bug = board.getCharacters().get(0);
        // move the bug close to the locked door
        context = new Context(multiMoveStrategySpecificied);
        context.executeMovements(board, bug, "wwdnddddnwwddnssss");

        // move the chip to pick the key and
        // then go to the pos that 1 step away from the bug
        context.executeMovements(board, board.getChip(), "dddddwwsssswwaaaaaaa");
        // should be alive now
        assertTrue(board.isChipAlive());
        // move the bug to kill the player
        context.executeMovements(board, bug, "d");

        // printBoardString(board);
        // System.out.println(bug.getLetter());
        // System.out.println(board.getChip().getLetter());
        // should be dead
        assertFalse(board.isChipAlive());

    }

    /**
     * Description: <br/>
     * Test whether the chip movement is fully matched so far.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_getChipMoves() {
        Board board = new Board(2);
        List<String> movementString = board.getChipMoves();
        assertTrue(movementString.isEmpty());// check it's empty
        /*
         * apply movements
         */
        context = new Context(multiMoveStrategySpecificied);
        String myMovements = "wswswwwsssddaawswswwwsssddaa";
        context.executeMovements(board, board.getChip(), myMovements);

        /*
         * assign movement strings
         */
        movementString = board.getChipMoves();
        List<String> myMovementList = new ArrayList<String>();
        for (int i = 0; i < myMovements.length(); i++) {
            if (myMovements.charAt(i) == 'w') {
                myMovementList.add("up");
            } else if (myMovements.charAt(i) == 'a') {
                myMovementList.add("left");
            } else if (myMovements.charAt(i) == 's') {
                myMovementList.add("down");
            } else if (myMovements.charAt(i) == 'd') {
                myMovementList.add("right");
            }
        }
        /*
         * compare them finally
         */
        boolean isMatched = movementString.equals(myMovementList);
        assert isMatched;
    }

    /**
     * Description: <br/>
     * Test User Generate CHaracter Movements, which is test Bugs movements.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_otherCharacterMoves() {
        Board board = new Board(2);
        UserGeneratedCharacter bugs = board.getCharacters().get(0);
        List<String> bugsMovements = board.getOtherCharacterMoves().get(0);

        context = new Context(multiMoveStrategySpecificied);
        String myMovements = "ddaaddaawswsddaaddaawsws";
        context.executeMovements(board, bugs, myMovements);
        /*
         * assign movement strings
         */
        bugsMovements = board.getOtherCharacterMoves().get(0);
        List<String> myMovementList = new ArrayList<String>();
        for (int i = 0; i < myMovements.length(); i++) {
            if (myMovements.charAt(i) == 'w') {
                myMovementList.add("up");
            } else if (myMovements.charAt(i) == 'a') {
                myMovementList.add("left");
            } else if (myMovements.charAt(i) == 's') {
                myMovementList.add("down");
            } else if (myMovements.charAt(i) == 'd') {
                myMovementList.add("right");
            }
        }
        /*
         * compare them finally
         */
        boolean isMatched = bugsMovements.equals(myMovementList);
        assert isMatched;
    }

    /**
     * Description: <br/>
     * Test whether the total treasure numbers is correct after it's been collected.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_total_treasureNumber() {
        Board board = new Board(1);
        int total_amount = board.getTreasureRemainingAmount();
        // get the treasure in the corner and check it is collected
        context = new Context(multiMoveStrategySpecificied);
        context.executeMovements(board, board.getChip(), "aaaaaawwwwwwwdddd");
        assert board.isTreasureCollected();// check it is collected

        // check whether the treasure amount is correct
        assert board.getTreasureRemainingAmount()
                + board.getTreasureCollectedAmount() == total_amount;

        // apply random direction multi times in order to find exceptions/errors
        // in this case, repeat 99 time random movements
        for (int i = 0; i < 99; i++) {
            // swap the strategy and execute movements
            context = new Context(randomMoveStrategyBoth);
            context.executeMovements(board, board.getChip(), "");
        }

    }

}
