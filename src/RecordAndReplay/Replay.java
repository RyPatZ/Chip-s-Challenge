package RecordAndReplay;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.json.*;
import Maze.Board;
import Maze.UserGeneratedCharacter;
import Maze.Character;

import java.nio.charset.StandardCharsets;


/**
 * The type Replay.
 *
 * @author Ruiyang Zhang
 */
public class Replay {
    /**
     * A list of string stores all the Movements for Chip.
     */
    List<String>  movement;

    /**
     * The Other character's movement, in this case , bug movement.
     */
    List<String> otherMovement;

    /**
     * The Bug character.
     */
    UserGeneratedCharacter bugCharacter;

    /**
     * The Level of the replay board.
     */
    int level;

    /**
     * The Time left for the game.
     */
    int time;

    /**
     * The Count for replay by step.
     */
    int count;

    /**
     * The Replay speed.
     */
    int replaySpeed = 0;

    /**
     * Instantiates a new Replay.
     */
    public Replay(){
        this.movement = new ArrayList<>();
        this.otherMovement = new ArrayList<>();
        this.level =0;
        this.time =101;
        this.count =0;
    }


    /**
     * Load the details of saved game from a json file.
     * First read the file with filereader and read the file as a json project
     * get information of the saved game and store them into the field in replay class
     * for perform replay
     */
    public void load() {
    	
        try {
            boolean fileFound = Files.exists(Paths.get("SavedGame/record" +".json"));

            if (fileFound) {

                FileReader reader = new FileReader(new File("SavedGame/record" + ".json"));
                JsonReader jsonReader = Json.createReader(reader);
                JsonObject object = jsonReader.readObject();
                jsonReader.close();
                String move;
                int numOfMove = object.getInt("steps");
                int numOfOMove = object.getInt("steps2");
                this.level = object.getInt("level");
                this.time = object.getInt("time");
                for (int i = 0; i < numOfMove; i++) {
                    move = object.getString("chipMovement" + i);
                    this.movement.add(move);
                }
                if (level == 2) {
                    int row = object.getInt("rowcharacter");
                    int col = object.getInt("colcharacter");

                    for (int i = 0; i < numOfOMove; i++) {
                        move = object.getString("otherMovement" + i);
                        this.otherMovement.add(move);
                    }
                }


            }

        }
        catch ( IOException ex) {
            ex.printStackTrace();
        }
        
    }

    /**
     * Apply auto replay to the board and return a board object after the movement
     *
     * @return the board
     */
    public Board autoReplay() {
        Board b = new Board(level);
        for(int i =0; i< movement.size(); i++) {
            b.executeMove(movement.get(i),b.getChip());
            if(level ==2) {
                bugCharacter = b.getCharacters().get(0);
                b.executeMove(otherMovement.get(i), bugCharacter);
            }
        }
        movement.clear();
        return b;
        }

    /**
     * Apply replay by step to the board and return a board after the movement
     *
     * @param board the board
     * @return the board
     */
    public Board replayByStep(Board board){
        Board b =board;
        b.executeMove(movement.get(count),b.getChip());
        if(level ==2) {
            bugCharacter = b.getCharacters().get(0);
            b.executeMove(otherMovement.get(count), bugCharacter);
        }
        count ++;
        return b;
        }

    /**
     * Select replay speed.
     */
    public void selectReplaySpeed(){
            try {
                Thread.sleep(replaySpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    /**
     * Get time left of the game .
     *
     * @return the int
     */
    public int getTime(){return time;}

    /**
     * Get level of the game.
     *
     * @return the int
     */
    public int getLevel(){return level;}

    /**
     * Get movement list of game.
     *
     * @return the list
     */
    public List<String> getMovement(){return movement;}

    /**
     * Set replay speed of replay.
     *
     * @param i the
     */
    public void setReplaySpeed(int i){this.replaySpeed=i;}


    /**
     * Clear all the movements.
     */
    public void clearMovement(){
        this.movement.clear();
        this.otherMovement.clear();
        this.count =0;
    }
    }

