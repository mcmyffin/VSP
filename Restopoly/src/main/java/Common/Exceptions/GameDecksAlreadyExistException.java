package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class GameDecksAlreadyExistException extends Exception {

    public GameDecksAlreadyExistException() {
    }

    public GameDecksAlreadyExistException(String message) {
        super(message);
    }
}
