package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class PawnNotFoundException extends Exception {

    public PawnNotFoundException() {
    }

    public PawnNotFoundException(String message) {
        super(message);
    }
}
