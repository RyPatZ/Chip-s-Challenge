package Application;

import java.awt.*;

/**
 * This class defines the thread to draw the time left.
 * It will not influence the availability of buttons and JMenuItems.
 * The user can play the game while this thread is running to update the time left.
 *
 * @author Qing (Cecilia) Lu (Student ID: 300363602)
 */
public class RunnableThread implements Runnable {
    private Thread t;

    /** Name of the thread. */
    private String threadName;

    /** Graphics to draw the time left number. */
    private Graphics g;

    /** Width of the graphics. */
    private int width;

    /** Height of the graphics. */
    private int height;

    /** Start second. */
    private int second = 100;

    /** If the game is paused, it's true; otherwise, false. */
    private boolean pause = false;

    /** GUI for the game. */
    private GUI gui = null;

    /** If the game has started, it's true; otherwise, it's false. */
    private boolean started = false;

    /** If the game is being played, it should draw the time left, and this would be true; otherwise, false. */
    private boolean drawNumber = true;

    /**
     * Constructor.
     * @param name name of the thread.
     * @param g graphics for number drawing
     * @param wid width of the side bar
     * @param hei height of the side bar
     * @param gui gui for the game
     */
    public RunnableThread(String name, Graphics g, int wid, int hei, GUI gui) {
        this.threadName = name;
        this.g = g;
        this.width = wid;
        this.height = hei;
        this.gui = gui;
    }

    @Override
    public void run() {
        if (this.threadName.equals("GUI")) {
            new GUI();
        } else if (this.threadName.equals("Time")) {
            drawNumber();
        }
    }

    /**
     * Draw the time left, which is a number.
     */
    public void drawNumber() {
        while(true) {
            if (this.started == true && this.pause == false && second > 0) {
                second--;
            }
            int startX = width / 3 + 4;
            int startY = height / 10 * 3 + 10;
            g.setColor(Color.black);
            Font f = new Font("Font.PLAIN", Font.BOLD, height / 12);
            g.setFont(f);
            g.fillRect(startX, startY, width / 3 - 8, height / 10 - 8);

            if (this.drawNumber) {
                g.setColor(Color.green);
                int stringX = startX + 2 * (width / 3 - 8) / 3;
                if (this.second < 10) {
                    stringX = startX + (width / 3 - 8) / 3 * 2;
                } else if (this.second >= 10 && this.second < 100) {
                    stringX = startX + (width / 3 - 8) / 3;
                } else if (this.second >= 100) {
                    Font ft = new Font("Font.PLAIN", Font.BOLD, height / 13);
                    g.setFont(ft);
                    stringX = startX + 3;
                }
                g.drawString(String.valueOf(second), stringX, startY + height / 10 - 18);
            }

            if (second == 0) {
                gui.ReplayCurrentLevel();
            }

            try {
                if (second != 101) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get time left.
     * @return how many seconds left in the current game.
     */
    public int getSecond() {
        return this.second;
    }

    /**
     * Set the seconds left.
     * @param i number of seconds left.
     */
    public void setSecond(int i) {
        this.second = i;
    }

    /**
     * Set the pause status.
     * @param b if set the game to pause, true; otherwise, false.
     */
    public void setPause(boolean b) {
        this.pause = b;
    }

    /**
     * Set if the game has started.
     * @param b if the game has started, true; otherwise, false.
     */
    public void setGameStart(boolean b) {
        this.started = b;
    }

    /**
     * Set the seconds left for drawing.
     * @param b the number of seconds to draw.
     */
    public void setDrawNumber(boolean b) {
        this.drawNumber = b;
    }
}



