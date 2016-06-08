package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class IllegalTransactionStateException extends Exception {

    public IllegalTransactionStateException() {
    }

    public IllegalTransactionStateException(String message) {
        super(message);
    }
}
