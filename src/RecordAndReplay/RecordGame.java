package RecordAndReplay;
import Maze.UserGeneratedCharacter;
import Application.RunnableThread;
import Maze.Board;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * The type Record game.
 * This class is for saving currant game into a json file to for record and replay
 *
 * @author Ruiyang Zhang 300490953
 */
@SuppressWarnings("unused")
public class  RecordGame {

    /**
     * The File count
     */
    public int fileCount;

    /**
     * write actions recorded into a JSON object
     */
    public RecordGame(){
        fileCount =1;
    }

    /**
     * Create a empty json file to store information of the game and return the file Path
     *
     * @return the string of the File Path
     */
    public String createFile(){
        String filePath ="";
    try {
        File myObj = new File("SavedGame/record" +".json");
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
            filePath =myObj.getPath();
            fileCount+=1;
        } else {
            System.out.println("File already exists.");
        }
    } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
    return filePath;
}

    /**
     * Write the information of the board into the json file if the file exist
     * if the json file is not exist then create one and write information of the game into the file
     *
     * @param board the Board
     * @param r     the RunnableThread
     * @param level the level of the board
     */
    public void write(Board board, RunnableThread r,int level) {
     	try {
            String filePath="SavedGame/record.json";
     	    if(!new File("SavedGame/record" +".json").exists()) {
                filePath = createFile();
            }
     	    JSONObject obj = new JSONObject();
     	    int count = 0;
     	    obj.put("time",r.getSecond());
     	    obj.put("level",level);
     	    obj.put("chips left",board.getTreasureRemainingAmount());
            List<List<String>> otherMoves = board.getOtherCharacterMoves();

            for(int i = 0; i < board.getChipMoves().size(); i++) {
                String m ="chipMovement"+count;
                obj.put(m, board.getChipMoves().get(i));
                count ++;
            }
            int count2 = 0;
            if(level ==2) {
                for (int i = 0; i < otherMoves.size(); i++) {
                    List<String> Moves = otherMoves.get(i);
                    List<UserGeneratedCharacter> characters = board.getCharacters();
                    obj.put("rowcharacter",characters.get(0).getRow());
                    obj.put("colcharacter",characters.get(0).getCol());
                    for (int j = 0; j < Moves.size(); j++) {
                        String om = "otherMovement" + count2;
                        obj.put(om, Moves.get(j));
                        count2 ++;
                    }
                }
            }
             obj.put("steps", count);
             obj.put("steps2",count2);
         Files.write(Paths.get(filePath), obj.toJSONString().getBytes(Charset.forName("UTF-8")));
     	}
     	catch (IOException ex) {
             ex.printStackTrace();
         }
     }
}
