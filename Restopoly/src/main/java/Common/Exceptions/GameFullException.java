package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class GameFullException extends Exception {

    public GameFullException() {
    }

    public GameFullException(String message) {
        super(message);
    }
}
