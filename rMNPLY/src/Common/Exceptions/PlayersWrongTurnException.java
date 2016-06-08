package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class PlayersWrongTurnException extends Exception {

    public PlayersWrongTurnException() {
    }

    public PlayersWrongTurnException(String message) {
        super(message);
    }
}
