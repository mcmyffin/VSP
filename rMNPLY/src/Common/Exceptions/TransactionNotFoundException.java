package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class TransactionNotFoundException extends Exception {

    public TransactionNotFoundException() {
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
