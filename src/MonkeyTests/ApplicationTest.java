package MonkeyTests;

import org.junit.Test;

import Application.GUI;

/**
 * Description: <br/>
 * Tests for the Application Package.
 * 
 * Should test the game state like: starting new games, loading/saving Games(JSON), moving the
 * player, managing the application window(s), etc.
 * 
 * 
 * @author Yun Zhou 300442776
 */
public class ApplicationTest extends Helper {

    /**
     * Description: <br/>
     * Test that the instruction String from the file is read successfully.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testReadInstruction() {
        assert new GUI().readAllLines().isEmpty() == false;
    }

    /**
     * Description: <br/>
     * Test for invoking getSecond() from GUI directly. It should throw nullPointer as it need
     * RunnableThread.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testGetSecond_nullPointer() {
        try {
            new GUI().getSecond();
        } catch (NullPointerException e) {
            return;
        }
        // should not excute the line below
        assert false;
    }

    /**
     * Description: <br/>
     * Test for invoking getSecond() from RunnableThread class.
     * 
     * @author Yun Zhou
     */
    @Test
    public void testGetSecond() {
        int time = getRunnableThread(true).getSecond();
        assert time == 100;

    }

    /**
     * Description: <br/>
     * Test the drawNumber() in RunnableThread class.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_drawNumber() {
        try {
            getRunnableThread(false).drawNumber();

        } catch (NullPointerException e) {
            return;
        }
        // should not excute the line below!
        assert false;

    }

    /**
     * Description: <br/>
     * Can't test key as it is empty and inside the GUI constructor.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_keyPressed() {

    }

    /**
     * Description: <br/>
     * Can't test key as it is empty and inside the GUI constructor.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_keyReleased() {

    }

    /**
     * Description: <br/>
     * Can't test key as it is empty and inside the GUI constructor.
     * 
     * @author Yun Zhou
     */
    @Test
    public void test_keyTyped() {

    }

}
