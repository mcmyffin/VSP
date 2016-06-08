package Common.Exceptions;

/**
 * Created by sasa on 11.05.16.
 */
public class GameDeckNotFoundException extends Exception {

        public GameDeckNotFoundException() {
        }

        public GameDeckNotFoundException(String message) {
            super(message);
        }
}
