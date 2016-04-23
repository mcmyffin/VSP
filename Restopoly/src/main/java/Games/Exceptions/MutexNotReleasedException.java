package Games.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class MutexNotReleasedException extends Exception {

    public MutexNotReleasedException() {
    }

    public MutexNotReleasedException(String message) {
        super(message);
    }
}
