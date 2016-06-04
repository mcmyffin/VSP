package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class PlayerAlreadyExistsException extends Exception {

    public PlayerAlreadyExistsException() {
    }

    public PlayerAlreadyExistsException(String message) {
        super(message);
    }
}
