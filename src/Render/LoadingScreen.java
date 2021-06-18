package Render;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**Class to draw and render the game board
 * @author Daniel Marshall
 */
public class LoadingScreen {

    /* FIELDS */
    /** Map to store all the images */
    private final Map<String, Image> images;
    private final String[] loadingSequence;

    /** Graphics object associated with the board pane */
    Graphics graphics;

    /**Constructor for the LoadingScreen. This loads all the file names stored in the
     * loading screen folder into a list which holds every image name so each image can be quickly found and drawn
     * @param g Graphics object
     */
	@SuppressWarnings("exports")
	public LoadingScreen(Graphics g){
        this.graphics = g;
        this.loadingSequence = new String[6];
        this.loadingSequence[0] = "LoadingScreen0";
        this.loadingSequence[1] = "LoadingScreen1";
        this.loadingSequence[2] = "LoadingScreen2";
        this.loadingSequence[3] = "LoadingScreen3";
        this.loadingSequence[4] = "LoadingScreen4";
        this.loadingSequence[5] = "LoadingScreen5";
        //Loading in all images to a map for efficiency in drawing them
        this.images = loadImages();
    }


    /** This method is the base redrawing method of the LoadingScreen
     * @param width - the width of the board panel
     * @param height - the height of the board panel
     */
    public void redraw(int width, int height) {
        if(width<0 || height <0){
            throw new IllegalArgumentException("Error: width or height was negative");
        }
            try{
                for (String imageName : loadingSequence) {
                    Thread.sleep(300);
                    this.graphics.drawImage(this.images.get(imageName), 0, 0, width, height, null);
                }
            }catch(InterruptedException e){System.out.println("Issue with Thread.sleep in the loading screen class");}
    }

    /** Load all the assets into a map for quicker access of them */
    private Map<String, Image> loadImages(){
        Map<String, Image> images = new HashMap<>();
        try{
            images.put("LoadingScreen0", ImageIO.read(new File("./assets/LoadingScreen/LoadingScreen0.png")));
            images.put("LoadingScreen1", ImageIO.read(new File("./assets/LoadingScreen/LoadingScreen1.png")));
            images.put("LoadingScreen2", ImageIO.read(new File("./assets/LoadingScreen/LoadingScreen2.png")));
            images.put("LoadingScreen3", ImageIO.read(new File("./assets/LoadingScreen/LoadingScreen3.png")));
            images.put("LoadingScreen4", ImageIO.read(new File("./assets/LoadingScreen/LoadingScreen4.png")));
            images.put("LoadingScreen5", ImageIO.read(new File("./assets/LoadingScreen/LoadingScreen5.png")));
        }catch(IOException e){System.out.println("IO Exception reading LoadingScreen images");}
        return images;
    }


}
