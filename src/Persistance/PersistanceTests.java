package Persistance;

import org.junit.Test;

import Maze.Board;
import Maze.Chip;
import Maze.Items.ExitLock;
import Maze.Items.Key;
import Maze.Items.Treasure;
import Maze.Tiles.Exit;
import Maze.Tiles.FreeTile;
import Maze.Tiles.InfoField;
import Maze.Tiles.LockedDoor;
import Maze.Tiles.SingleUseTile;
import Maze.Tiles.Tile;
import Maze.Tiles.WallTile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.*;


/**
 * A set of tests to make sure the parser is working correctly.
 *
 * @author kainsamu
 */
public class PersistanceTests {
	
	/**
	 * Test to make sure there aren't any errors.
	 */
	@Test
	public void testreader() {
		Tile[][] loaded = JsonParser.getLvl(0);
	}
	
	 /**
	 * Test for basic reading.
	 */
	@Test
	    public void testbasicreading() {
	        Tile[][] loaded = JsonParser.getLvl(-1);
	        String result = "";
	        for (int row = 0; row < loaded.length; row++) {
	            for (int col = 0; col < loaded[row].length; col++) {
	                result = result + tileConverter(loaded[row][col]);
	            }
	        }
	        String expected = "WWWWWWWWWWWFFWEWFFFWWWRWFWRWWWWFFFIFFFFWWFFFFFFFFWWWWBWGWWWWWFFFWFFFFWWFFFWFFFFWWFFFWFFFFWWWWWWWWWWW";
	        assertTrue(result.equals(expected));
	    }
	 
	 	/**
	 	 * Test the correct error is thrown
	 	 */
	 	@Test
	 	public void testError() {
	 		Tile[][] loaded = JsonParser.getLvl(66);
	 	}
	 	
	 	/**
	 	 * Test loading a level with multiple elements.
	 	 */
	 	@Test
	 	public void loadAdvancedLvl() {
	 		Tile[][] loaded = JsonParser.getLvl(2);
	        String result = "";
	        for (int row = 0; row < loaded.length; row++) {
	            for (int col = 0; col < loaded[row].length; col++) {
	                result = result + tileConverter(loaded[row][col]);
	            }
	        }
	        String expected = "WWWWWWWWWWWWWWWWWWWWWFFFFWFFFWFFFWFFFFFWWFWWWWFWFWFFFWFFFFFWWFFFFFFWFWFFFWFFFFFWWFWFWWWWFWFFFWFFFFFWWFWFFFFWFRFFFFFFFFFWWFWFWWWWFWFFFWFFFFFWWFFFFFFWFWFFFWFFFFFWWFWWWWFWFWFFFWFFFFFWWFFFFWFFFWFFFWFFFFFWWWWWWWWWWWWRWWWWWWWWWFFFSFFWFWFFFWFFFFFWWFWWWWFFFBFFFRFFFFFWWFSFFWFWFWFFFWWWWWWWWFWFFWWWSWFFFFFFFEWWWFWFFSFWFWFFFWWWWWWWWSWWWWFSFSFFFBFFFFFWWFFFFFFWFWFFFWFFFFFWWWWWWWWWWWWWWWWWWWWW";
	        assertTrue(result.equals(expected));
	 	}
	 
	 /**
	   * This is a helper method for the tests, it converts tiles to their string state.
	 * @param t returns the tile array.
	 * @return the tiles character
	   */
	 public char tileConverter(Tile t) {
	          if (t instanceof WallTile) {
	            return 'W';
	          } else if (t instanceof InfoField) {
		            return 'I';
	          } else if (t instanceof LockedDoor) {
		            if(((LockedDoor) t).getColour() == Color.red) {
		            	return 'R';
		            } else if(((LockedDoor) t).getColour() == Color.green) {
		            	return 'G';
		            } else if(((LockedDoor) t).getColour() == Color.blue) {
		            	return 'B';
		            } else {
		            	return 'Y';
		            }
	          } else if (t instanceof Exit) {
	        	  return 'E';
	          } else if (t instanceof SingleUseTile) {
	        	  return 'S';
	          } else {
	        	  return 'F';
	          }
	        }

}