package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class PlayerNotFoundException extends Exception {

    public PlayerNotFoundException() {
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
