package Application;

/**
 * This class includes the main method for the program.
 *
 * @author Qing (Cecilia) Lu (Student ID: 300363602)
 */
public class Main {
    /**
     * Main method.
     * @param args arguments for main method.
     */
    public static void main(String[] args) {
        new Thread(new RunnableThread("GUI", null, 0, 0, null)).start();
    }
}
