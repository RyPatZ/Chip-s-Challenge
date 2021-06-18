package Maze;

/**
 * This defines the methods which a user added character must implement
 * This is used by the characters added by the persistence module
 * @author Oscar Sykes 300486149
 */
public abstract class UserGeneratedCharacter extends Character{
    /**
     * @param row row of the character
     * @param col column of the character
     */
    public UserGeneratedCharacter(int row, int col) {
        super(row, col);
    }

    /**
     * This method defines what direction the characters choose to move in
     * @return the direction the character is moving in (left, right, up, down, noMove)
     */
    public abstract String move();
}
