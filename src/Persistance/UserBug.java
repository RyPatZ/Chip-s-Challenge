package Persistance;

import Maze.UserGeneratedCharacter;

/**
 * An example method of a simple character that a 
 * fan could add into the game if they wanted to. In order
 * to do this, simple make sure the user defined bug
 * has the methods move() and getLetter().
 * @author kainsamu
 *     
 */
public class UserBug extends UserGeneratedCharacter {

  /** 
 * Creator for the character.
 * @param row int for row of character
 * @param col int for col of character
 */
  public UserBug(int row, int col) {
    super(row, col);
  }

  @Override
  public String move() {
    Double rand = Math.random() * 8;
    int num = (int) Math.round(rand);
    if (num == 0 || num == 1) {
      return "up";
    } else if (num == 2 || num == 3) {
      return "left";
    } else if (num == 4 || num == 5) {
      return "right";
    } else if (num == 6 || num == 7) {
      return "down";
    } else {
      return "noMove";
    }
  }

  @Override
  public String getLetter() {
    return "U";
  }

}
