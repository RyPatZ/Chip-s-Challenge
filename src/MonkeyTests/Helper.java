package MonkeyTests;

import java.awt.Color;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import Application.RunnableThread;
import Maze.Board;
import Maze.Tiles.Tile;
import MonkeyTests.strategy.Context;
import MonkeyTests.strategy.MultiMoveStrategySpecificied;
import MonkeyTests.strategy.RandomMoveStrategyBoth;
import MonkeyTests.strategy.RandomMoveStrategyBug;

/**
 * Description: <br/>
 * Test classes need to use lotes of variables frequently, so this class is for me to use these
 * variables easier.
 * 
 * @author Yun Zhou 300442776
 */
public abstract class Helper {
    /**
     * Context for using the Strategy.
     */
    protected Context context;

    /**
     * valid movement args, which is w a s d noMove.
     */
    private List<String> validMovementArguments = new ArrayList<String>();

    /**
     * valid color list for the Key and Locked Door object.
     */
    private List<Color> validColorList = new ArrayList<Color>();

    /**
     * validGameLevelList, which is from level -1 to 2.
     */
    private List<Integer> validGameLevelList = new ArrayList<Integer>();

    /**
     * the 2d array Tiles map which contains all datas.
     */
    protected Tile[][] mapBoardTiles;

    /**
     * When apply this, only specificied character can be used to move.
     * 
     * Easy to use to swap the Strategy.
     */
    protected MultiMoveStrategySpecificied multiMoveStrategySpecificied = new MultiMoveStrategySpecificied();

    /**
     * When apply this, only Chip can be used as argument, bug will be invoked inside.
     * 
     * Easy to use to swap the Strategy.
     */
    protected RandomMoveStrategyBoth randomMoveStrategyBoth = new RandomMoveStrategyBoth();

    /**
     * Easy to use to swap the Strategy.
     */
    protected RandomMoveStrategyBug randomMoveStrategyBug = new RandomMoveStrategyBug();

    /**
     * Entrance thread for the game.
     */
    private RunnableThread gameEntranceThread;

    /**
     * Get the runnable thread.
     * 
     * @param isMainThread
     *            true for main GUI Game window,false for time counting thread
     *
     * @return the runnableThread
     */
    public final RunnableThread getRunnableThread(boolean isMainThread) {
        Preconditions.checkNotNull(isMainThread);
        if (gameEntranceThread == null) {
            String threadNameString;
            if (isMainThread) {
                threadNameString = "Main";
            } else {
                threadNameString = "Time";
            }
            gameEntranceThread = new RunnableThread(threadNameString, null, 0, 0, null);
        }
        return gameEntranceThread;
    }

    /**
     * Description: <br/>
     * A self made main thread.
     * 
     * @author Yun Zhou
     * @return mainThread of the game
     */
    public final Thread mainThread() {
        Thread main = new Thread(getRunnableThread(true));

        return main;
    }

    /**
     * The main purpose is assign the Context, because level 0 board only need to move chip, so
     * use specificied context. Method is for the changes need to update the MazeTest.
     *
     * @return the context
     */
    protected Board getLevel0Board() {
        // if it has not been initialized, then initialized
        if (context == null) {
            context = new Context(multiMoveStrategySpecificied);
        }

        return new Board(0);
    }

    /**
     * Get the validMovementArguments.
     *
     * @return the validMovementArguments
     */
    protected final List<String> getValidMovementArguments() {
        // if it has not been initialized yet, then initialize the list
        if (validMovementArguments.isEmpty()) {
            validMovementArguments.add("up");
            validMovementArguments.add("left");
            validMovementArguments.add("right");
            validMovementArguments.add("down");
            validMovementArguments.add("noMove");
        }
        return validMovementArguments;
    }

    /**
     * Get the validColorList.
     *
     * @return the validColorList
     */
    protected final List<Color> getValidColorList() {
        // if it has not been initialized yet, then initialize the list
        if (validColorList.isEmpty()) {
            validColorList.add(Color.red);
            validColorList.add(Color.green);
            validColorList.add(Color.blue);
            validColorList.add(Color.yellow);
        }
        return validColorList;
    }

    /**
     * Get the validGameLevelList.
     *
     * @return the validGameLevelList
     */
    protected final List<Integer> getValidGameLevelList() {
        // add to the list if it has not been initialized yet
        if (validGameLevelList.isEmpty()) {
            for (int i = -1; i <= 2; i++) {
                validGameLevelList.add(i);
            }
        }
        return validGameLevelList;
    }

    /**
     * A constructor. It construct a new instance of Helper.
     *
     */
    public Helper() {
        super();
    }

    /**
     * Description: <br/>
     * Get and assign all public methods from specified class. For instance, get all public
     * methods from Board/GUI class,easy for me to check whether I have tested or not.
     * 
     * @author Yun Zhou
     * @param className
     *            the class that need to get public methods
     */
    public static void printAllPublicMethods(Class<?> className) {
        System.out.println("----------------------------------------------");
        // Map<ArrayList<String>, Integer> method_testTimesMap = new HashMap<>();
        // print out the Class string
        System.out.println("Public methods for the " + className + " class:");
        int num = 0;

        // get all methods of this Class
        Method[] allMethods = className.getDeclaredMethods();
        for (Method method : allMethods) {
            if (Modifier.isPublic(method.getModifiers())) {
                num++;
                System.out.println(method.getName());

                // method_testTimesMap.put(methodNameList, 0);
            }
        }
        System.out.println("Number of public methods in " + className + " is: " + num);
        System.out.println("----------------------------------------------");
    }

    /**
     * Description: <br/>
     * Debug print board.
     * 
     * @author Yun Zhou
     * @param board
     *            to print
     */
    protected void printBoardString(Board board) {
        System.out.println(board.toString());

    }

}