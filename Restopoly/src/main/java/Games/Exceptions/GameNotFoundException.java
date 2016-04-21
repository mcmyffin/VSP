package Games.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class GameNotFoundException extends Exception {

    public GameNotFoundException() {
    }

    public GameNotFoundException(String message) {
        super(message);
    }
}
