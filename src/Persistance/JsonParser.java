package Persistance;

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
import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * This is a Json Parser that can read Json files where the levels are stored.
 * @author kainsamu
 *
 */
public class JsonParser {

  /**
   * This is a method that will return an array of tiles, ready for a game to be played.
   * @param s int for level number
   * @return Tile array
   */
  public static Tile[][] getLvl(int s) {

    Tile[][] tiles;

    InputStream fis;
    try {
      fis = new FileInputStream("levels/level" + s + ".json");
      JsonReader reader = Json.createReader(fis);
      JsonObject lvlObject = reader.readObject();
      reader.close();

      JsonArray lvldimens = lvlObject.getJsonArray("lvlWandH");
      String width = lvldimens.getString(0);
      String height = lvldimens.getString(1);

      int wid = Integer.parseInt(height);
      int hei = Integer.parseInt(width);

      tiles = new Tile[hei][wid];

      for (int i = 0; i < hei; i++) {
        for (int j = 0; j < wid; j++) {
          char c = lvlObject.getString("lvlTiles").charAt(i * wid + j);
          if (c == 'W') {
            tiles[i][j] = new WallTile();
          } else if (c == 'I') {
            tiles[i][j] = new InfoField();
          } else if (c == 'R') {
            tiles[i][j] = new LockedDoor(Color.red);
          } else if (c == 'G') {
            tiles[i][j] = new LockedDoor(Color.green);
          } else if (c == 'B') {
            tiles[i][j] = new LockedDoor(Color.blue);
          } else if (c == 'E') {
            tiles[i][j] = new Exit();
          } else if (c == 'Y') {
            tiles[i][j] = new LockedDoor(Color.yellow);
          } else if (c == 'S') {
            tiles[i][j] = new SingleUseTile();
          } else {
            tiles[i][j] = new FreeTile();
            FreeTile tile = (FreeTile) tiles[i][j];
            if (c == 'r') {
              tile.setItem(new Key(Color.red));
            } else if (c == 'g') {
              tile.setItem(new Key(Color.green));
            } else if (c == 'b') {
              tile.setItem(new Key(Color.blue));
            } else if (c == 'y') {
              tile.setItem(new Key(Color.yellow));
            } else if (c == 'C') {
              tile.setItem(new Treasure());
            } else if (c == 'X') {
              tile.setItem(new ExitLock());
            } else if (c == 'H') {
              Chip chip = new Chip(i, j);
              tile.setCharacter(chip);
            } else if (c == 'U') {
              UserBug b = new UserBug(i, j);
              tile.setCharacter(b);
            } else if (c != 'F') {
              System.out.println("Corrupted file");
               assert false;
            }
          }
        }
      }

      return tiles;

    } catch (FileNotFoundException e) {
      System.out.println("No such file");
    }

    return null;

    }

}