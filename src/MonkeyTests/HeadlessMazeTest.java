package MonkeyTests;

import static org.junit.Assert.assertFalse;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import Maze.Board;
import Maze.Character;
import Maze.Chip;
import Maze.UserGeneratedCharacter;
import Maze.Items.Key;
import Maze.Tiles.LockedDoor;
import Maze.Tiles.Tile;
import MonkeyTests.strategy.Context;

/** Description: <br/>
 *
 * @author Yun Zhou 300442776
 * @version 
 */
/**
 * Description: <br/>
 * Headless tests, which means try the best to not use assert method to check arguments.
 * Several movement strategys are used to move the Character to check and check args are
 * correct. Purpose is to detect violations of the game logic which means find a general
 * programming error or postcondition or invariant violation.
 * <p>
 * Precondition violations, such as IllegalArgumentException.
 * <p>
 * General programming: errors, such as NullPointerException.
 * <p>
 * Postcondition or Invariant violations caused by asserts. i.e. AssertionErrors.
 * 
 * @author Yun Zhou 300442776
 */
public class HeadlessMazeTest extends Helper {
    /**
     * Description: <br/>
     * Random move characters on all level of BOARDS in order to triag the
     * exception/error.
     * <p>
     * All 3 movements stratgies are applied on this method. A pretty long test rather
     * than seprate tests for each level board.
     * 
     * @author Yun Zhou
     */
    @Test
    public void randomMoveCharactersOnBoard() {
        /*
         * Do the movement test for all different levels of boards
         */
        for (Integer level : getValidGameLevelList()) {
            Board board = new Board(level);
            /*
             * Strategy 1: specificied mult moves for the specifieied character(chip/bug)
             * 
             */
            context = new Context(multiMoveStrategySpecificied);
            context.executeMovements(board, board.getChip(),
                    "wdasdasdwdasdawdsdasdaawsdawasssadddsdwdd");// for chip

            // apply the specificied bug movement strategy on LEVEL 2
            // as only this level got a UserGenerateCharacter(i.e.bug).
            if (level.equals(2)) {
                // apply movements on each userGenerateCharacter which is Userbug
                for (UserGeneratedCharacter bug : board.getCharacters()) {
                    // use strategy 1: apply specified movements on Bug
                    context.executeMovements(board, bug,
                            "wdnansndnansndnwndnansndnanwndnsndnansndnananwnsndawasdwdd");

                    /*
                     * Strategy 2: randomMove for bug
                     * 
                     * Only apply random moves on Bug
                     */
                    context = new Context(randomMoveStrategyBug);
                    // do 999 random movements, more is better
                    for (int i = 0; i < 999; i++) {
                        context.executeMovements(board, bug, "");
                    }
                }

            }
            /*
             * Strategy 3: random move on all characters in order to find
             * exceptions/errors/assertError etc.
             * 
             */
            context = new Context(randomMoveStrategyBoth);
            // do 999 random movements, more is better
            for (int i = 0; i < 999; i++) {
                context.executeMovements(board, board.getChip(), "");
            }

        }

        // this line SHOULD BE EXECUTED as error should not occur
        assert true;

    }

    /**
     * Description: <br/>
     * Check the LockedDoor.toString() should all work and not thow exceptions/assertion
     * error.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testLockDoor_toString() {
        /*
         * check all toString method is work for all valid LockedDoor object. It SHOULD
         * NOT throw any exceptions/assertions
         */
        for (Color validColor : getValidColorList()) {
            new LockedDoor(validColor).toString();
        }
    }

    /**
     * Description: <br/>
     * Test that the all valid game Board.toString can be invoked and not throw exceptions
     * etc.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_board_toStringMethod() {
        for (Integer validLevelInteger : getValidGameLevelList()) {
            new Board(validLevelInteger).toString();
        }

    }

    /**
     * Description: <br/>
     * A test for testing the Key Constructor argument, which is to test whether invalid
     * args will throw the Exceptions/error and valid args should all pass.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testKeyArgument() {

        /*
         * test for the valid args first, it should pass and not throw any exceptions
         */
        for (Color validColor : getValidColorList()) {
            new Key(validColor);
        }

        /*
         * test for the invalid color, in this case, it should throw the IllegalArgs
         * exceptions
         */
        try {
            new Key(Color.pink);
            new Key(Color.ORANGE);
        } catch (IllegalArgumentException e) {
            return;
        }
        // should not execute the line below
        // if it happens then something went wrong and need to add Preconfdition check
        assert false;
    }

    /**
     * Description: <br/>
     * Test whether the Chip argument is correct.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testValidChipArgument() {
        try {
            new Chip(-1, -1);
        } catch (IllegalArgumentException e) {
            return;// should stop here
        }
        // should not execute the line below
        // if it occur, then something went wrong
        assert false;
    }

    /**
     * Description: <br/>
     * Test whether it will throw nullPointer.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_nullChip() {

        try {
            new Board(1).moveChip(null);
        } catch (NullPointerException e) {
            return;
        }
        // should not execute the line below
        // if it occur, then something went wrong
        assert false;

    }

    /**
     * Description: <br/>
     * Test invalid input string.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testInvalidMovementInput() {
        Board board = new Board(0);
        boolean isValid = false;
        try {
            board.moveChip("NORTH");
            board.moveChip("NORTH");
            board.moveChip("W");
            board.moveChip("A");
            board.moveChip("S");
            board.moveChip("D");
            isValid = true;

        } catch (IllegalArgumentException e) {
            assertFalse(isValid);
            return;
        }
        // should not excute the line below
        // if it occur, then something went wrong
        System.err.println("Invalid Arguments for movement");
        assert false;
    }

    /**
     * Description: <br/>
     * Test whether the invalid Game level will throw the invalid arguments or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_validGameLevel() {
        /*
         * first test the board will be constructed if it is valid.
         */
        for (Integer level : getValidGameLevelList()) {
            new Board(level);
        }

        /*
         * then test the invalid board which means it should throw illegal args exception
         */
        try {
            new Board(999);
        } catch (IllegalArgumentException e) {
            return;
        }
        // should not excute the line below
        // if it occur, then something went wrong
        System.err.println("Invalid Arguments for level");
        assert false;

    }

    /**
     * Description: <br/>
     * Go to the corner to pick the treasure and do random moves in order to find
     * exceptions/errors if there is.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_playGame_randomMoves_Chip() {
        Board board = new Board(1);
        context = new Context(multiMoveStrategySpecificied);
        context.executeMovements(board, board.getChip(), "s");
        assert board.onInfoTile();// check if on InfoTile
        // applyMultiMoves(board, board.getChip(), "s");

        // get the treasure in the corner and check it is collected
        context.executeMovements(board, board.getChip(), "aaaaaawwwwwwwdddd");
        // applyMultiMoves(board, board.getChip(), "aaaaaawwwwwwwdddd");
        assert board.isTreasureCollected();// check it is collected

        /*
         * apply random direction multi times in order to find exceptions/errors in this
         * case, repeat 99 time random movements
         */
        for (int i = 0; i < 99; i++) {
            // swap the strategy and execute movements
            context = new Context(randomMoveStrategyBoth);
            // excute moves
            context.executeMovements(board, board.getChip(), "");
        }

    }

    /**
     * Description: <br/>
     * Test the method executeMoves(), check whether it will throw the IllegalArgument
     * exception or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_executeMoves_nullPointer() {
        Board board = new Board(1);
        boolean isInvoked = false;
        try {
            // check whether null pointer is thrown when check 1st args
            board.executeMove(null, board.getChip());
            isInvoked = true;
        } catch (NullPointerException e) {
            // assert should be true, if not then the method need the precondition check
            assert !isInvoked;
        }
        try {
            // check whether null pointer will be thrown when check 2nd args
            board.executeMove("up", null);
            isInvoked = true;
        } catch (NullPointerException e) {
            // assert should be true, if not then the method need the precondition check
            assert !isInvoked;
            return;
        }

        // should not execute the line
        // if happen, then something wrong
        assert false;

    }

    /**
     * Description: <br/>
     * Test the method executeMoves(), check whether it will throw the IllegalArgument
     * exception or not.
     * 
     * @author Yun Zhou
     */
    @Test
    public void check_executeMoves_arguments() {
        Board board = new Board(2);
        /*
         * First check whether the valid args can be invoked or not. It should not occur
         * any error or exception.
         */
        for (String validArgString : getValidMovementArguments()) {
            // check chip
            board.executeMove(validArgString, board.getChip());
            // check each bug
            for (UserGeneratedCharacter bugs : board.getCharacters()) {
                board.executeMove(validArgString, bugs);
            }
        }

        boolean isInvoked = false;// for checking whether the method is Invoked
        try {
            // check whether Illegal is thrown when check the first args
            board.executeMove("????", board.getChip());
            isInvoked = true;
        } catch (IllegalArgumentException e) {
            // assert should be true, if not then the method need the precondition check
            assert !isInvoked;
        }
        try {
            // check whether Illegal is thrown when check the second args
            board.executeMove("up", new Chip(999, -999));
            isInvoked = true;
        } catch (IllegalArgumentException e) {
            // assert should be true, if not then the method need the precondition check
            assert !isInvoked;
            return;
        }

        // should not execute the line
        // if happen, then something wrong
        assert false;

    }

    /**
     * Description: <br/>
     * Check the SingleUse tile.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_singleUseTile() {
        Board board = new Board(2);
        context = new Context(multiMoveStrategySpecificied);
        // get the treasure and move to one step away from SingleUse tile
        context.executeMovements(board, board.getChip(),
                "dddddsswwwwssaaaaasssssssssssssssssswa");
        // check if the singleUse tile can be moved to
        boolean canMoveTo = context.executeMovements(board, board.getChip(), "a");
        assert canMoveTo;
        // check that the SingleUse tile has become to Wall which is not able to move to
        canMoveTo = context.executeMovements(board, board.getChip(), "ad");
        assert canMoveTo == false;

        // printBoardString(board);
    }

    /**
     * Description: <br/>
     * Check whether the IllegalargsExceptions will be thrown for Character.setter method.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_setCharacterArgs() {
        Character chip = new Chip(1, 1);
        // first check valid args
        for (int i = 0; i < 33; i++) {
            chip.setCol(i);
            chip.setRow(i);
        }

        try {
            // test for the setCol arg
            chip.setCol(-1);

        } catch (IllegalArgumentException e) {
            // TODO: handle exception
        }
        try {
            // test for the setRow arg
            chip.setRow(-1);

        } catch (IllegalArgumentException e) {
            return;
        }
        // should noy excute the line below
        // if happens then something went wrong
        assert false;

    }

    /**
     * Description: <br/>
     * Test for the removeKey() method argument.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_ChipAddKey_arguments() {
        Board board = new Board(1);
        Chip chip = board.getChip();
        // should run correctly.
        for (Color validColor : getValidColorList()) {
            Key key = new Key(validColor);
            chip.addKey(key);
        }
        // should throw exceptions
        try {
            chip.addKey(new Key(Color.pink));
        } catch (IllegalArgumentException e) {
            return;
        }
        // unreacheable, should not execute the line below
        // it it happen then something went wrong
        assert false;
    }

    /**
     * Description: <br/>
     * Test for the removeKey() method argument.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testContainsKeyArgs() {
        Chip chip = new Chip(3, 3);
        // should pass,check valid key
        for (Color validColor : getValidColorList()) {
            chip.containsKey(validColor);
        }

        // invalid args
        try {
            chip.containsKey(Color.pink);
        } catch (IllegalArgumentException e) {
            return;
        }
        // unreacheable, should not execute the line below
        // it it happen then something went wrong
        assert false;
    }

    /**
     * Description: <br/>
     * Test for the removeKey() method argument.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testRemoveKeyArgs() {
        Chip chip = new Chip(3, 3);
        // should pass,check valid key
        for (Color validColor : getValidColorList()) {
            chip.removeKey(validColor);
        }

        // invalid args
        try {
            chip.removeKey(Color.pink);
        } catch (IllegalArgumentException e) {
            return;
        }
        // unreacheable, should not execute the line below
        // it it happen then something went wrong
        assert false;
    }

    /**
     * Description: <br/>
     * Check the tile.toString() should throw the assertsException.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testTile_toString() {

        /*
         * first check that the Tile obj can not invoke toString
         */
        try {
            // toString method for Tile is not accessible.
            // should throw AssertionError
            new Tile().toString();
        } catch (AssertionError e) {
        }

        /*
         * then, check every valid tiles toString() for each game level, which means that
         * it should not throw any exceptions/errors in this case
         */
        // valid game level
        for (Integer validLevel : getValidGameLevelList()) {
            // update the boardTiles data within each gameLevel
            mapBoardTiles = new Board(validLevel).getTileArray();
            // check each toSTring can be invoked
            for (int i = 0; i < mapBoardTiles.length; i++) {
                for (int j = 0; j < mapBoardTiles[i].length; j++) {
                    mapBoardTiles[i][j].toString();
                }
            }
        }

    }

    /**
     * Description: <br/>
     * Check the tile.toString() should throw the assertsException.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testTile_getLetter() {
        /*
         * first check the empty Tile letter is not accessable
         */
        try {
            new Tile().getLetter();
        } catch (AssertionError e) {
        }

        /*
         * then, check the valid tiles.getLetter() for each game level, which means that
         * it should not throw any exceptions/errors in this case
         */
        // valid game level
        for (Integer validLevel : getValidGameLevelList()) {
            // update the boardTiles data within each gameLevel
            mapBoardTiles = new Board(validLevel).getTileArray();
            // check each letter
            for (int i = 0; i < mapBoardTiles.length; i++) {
                for (int j = 0; j < mapBoardTiles[i].length; j++) {
                    mapBoardTiles[i][j].getLetter();
                }
            }
        }

    }

    /**
     * Description: <br/>
     * Test for the tile.containsChip method.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testTile_containsChip() {
        /*
         * first check the empty Tile do not contains chip
         */
        try {
            assert new Tile().containsChip();
        } catch (AssertionError e) {
        }

        /*
         * then, check the valid tiles for each game level, which means that it should not
         * throw any exceptions/errors in this case
         */
        // valid game level
        for (Integer validLevel : getValidGameLevelList()) {
            // update the boardTiles data within each gameLevel
            mapBoardTiles = new Board(validLevel).getTileArray();
            // check each letter
            for (int i = 0; i < mapBoardTiles.length; i++) {
                for (int j = 0; j < mapBoardTiles[i].length; j++) {
                    mapBoardTiles[i][j].getLetter();
                }
            }
        }

    }

    /**
     * Description: <br/>
     * Test for the LockedDoor constructor argument.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testLockDoorConstructorArg() {

        /*
         * first,test that the valid color Locked door can all be constructed and not
         * throw any exceptions
         */
        for (Color validColor : getValidColorList()) {
            new LockedDoor(validColor);
        }

        /*
         * then check the invalid color which is not in the validColor list As it is
         * invalid, so it should throw IllegalArgs exception
         */
        try {
            new LockedDoor(Color.pink);
            new LockedDoor(Color.orange);
        } catch (IllegalArgumentException e) {
            return;
        }
        // unreacheable, should not execute the line below
        // if it happen then something went wrong
        assert false;

    }

    /**
     * Description: <br/>
     * Test that if the valid movement by mouse can be made.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testMovementByMouse_valid() {
        boolean isSuccess = false;// for marking whether movements are made
                                  // successfully
        try {
            Board board = new Board(0);
            Chip chip = board.getChip();
            board.moveByMouse(chip.getRow() + 1, chip.getCol());
            board.moveByMouse(chip.getRow() - 1, chip.getCol());
            board.moveByMouse(chip.getRow(), chip.getCol() + 1);
            board.moveByMouse(chip.getRow(), chip.getCol() - 1);
            isSuccess = true;
        } catch (Exception e) {
            System.err.println("Failed.");
            assert false;
        } finally {
            assert isSuccess;
        }
    }

    /**
     * Description: <br/>
     * Test for the Board.clearMove() method, apply all 3 strategies.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_clearMoves() {
        Board board = new Board(2);
        /*
         * multiMoveStrategySpecificied Stratgy is used. use this to control chip and bug
         * in order to find any bug.
         */
        context = new Context(multiMoveStrategySpecificied);
        String movements = "ddaaddaawswsddaaddaawsws";
        // move the chip
        context.executeMovements(board, board.getChip(), movements);
        // move the bug
        movements = "ddaaddaawswsddaaddaawsws";
        UserGeneratedCharacter bugs = board.getCharacters().get(0);
        context.executeMovements(board, bugs, movements);

        // check they are all have some movements
        assert board.getChipMoves().isEmpty() == false
                && board.getOtherCharacterMoves().get(0).isEmpty() == false;

        // clear Move and check whether they are empty
        board.clearMoves();
        assert board.getChipMoves().isEmpty()
                && board.getOtherCharacterMoves().get(0).isEmpty();

        /*
         * randomMoveStrategy for Bug is used.Only move the bug and check the bug
         * movements.
         */
        context = new Context(randomMoveStrategyBug);
        // apply 99 movements on Bug
        for (int i = 0; i < 99; i++) {
            context.executeMovements(board, bugs, "");
        }
        // check only bugs got some movements
        assert board.getChipMoves().isEmpty()
                && board.getOtherCharacterMoves().get(0).isEmpty() == false;
        // clear Move and check whether they are empty
        board.clearMoves();
        assert board.getOtherCharacterMoves().get(0).isEmpty();

        /*
         * randomMoveStrategy for both is used.Both characters will be moved randomly and
         * check both movements has been cleared.
         */
        context = new Context(randomMoveStrategyBoth);// swap strategy
        // apply 99 movements on both bug and character
        for (int i = 0; i < 99; i++) {
            context.executeMovements(board, board.getChip(), "");
        }
        // check they are all have some movements
        assert board.getChipMoves().isEmpty() == false
                && board.getOtherCharacterMoves().get(0).isEmpty() == false;

        // clear Move and check whether they are empty
        board.clearMoves();
        assert board.getChipMoves().isEmpty()
                && board.getOtherCharacterMoves().get(0).isEmpty();

    }

}