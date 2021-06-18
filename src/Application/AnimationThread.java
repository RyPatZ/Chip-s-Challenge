package Application;

import Render.LoadingScreen;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class defines the thread for loading the animation on the board before a game starts.
 * When this thread is running, all buttons and JMenuItems in the GUI thread can be clicked.
 * Once the game get started, this thread will stop.
 *
 * @author Qing (Cecilia) Lu (Student ID: 300363602)
 */


public class AnimationThread implements Runnable {
    /** Width of the board. */
    private int width;

    /** Height of the board. */
    private int height;

    /** LoadingScreen object for redrawing. */
    LoadingScreen loadscreen;

    /** Running thread. */
    private Thread worker;

    /** AtomicBoolean to show if the thread is running. */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Constructor.
     * @param g the graphics by which to draw the animation picture.
     * @param width width of the board
     * @param height height of the board
     */
    public AnimationThread(Graphics g, int width, int height){
        this.width = width;
        this.height = height;
        loadscreen = new LoadingScreen(g);
    }

    /**
     * Start the thread and draw the animation.
     */
    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    /**
     * Stop the thread and stop playing the animation.
     */
    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            loadscreen.redraw(width, height);
        }
    }
}
