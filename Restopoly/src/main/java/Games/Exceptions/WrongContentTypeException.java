package Games.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class WrongContentTypeException extends Exception {

    public WrongContentTypeException() {
    }

    public WrongContentTypeException(String message) {
        super(message);
    }
}
