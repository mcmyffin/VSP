package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class TransferFailedException extends Exception {

    public TransferFailedException() {
    }

    public TransferFailedException(String message) {
        super(message);
    }
}
