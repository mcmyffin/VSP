package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class BoardNotFoundException extends Exception {

    public BoardNotFoundException() {
    }

    public BoardNotFoundException(String message) {
        super(message);
    }
}
