package Common.Exceptions;

/**
 * Created by sasa on 12.04.16.
 */
public class TransactionFailedException extends Exception {

    public TransactionFailedException() {
    }

    public TransactionFailedException(String message) {
        super(message);
    }
}
