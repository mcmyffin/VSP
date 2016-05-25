package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class BoardAlreadyExistsException extends Exception {

    public BoardAlreadyExistsException() {
    }

    public BoardAlreadyExistsException(String message) {
        super(message);
    }
}
