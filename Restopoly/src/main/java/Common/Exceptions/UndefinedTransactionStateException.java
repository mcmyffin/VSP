package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class UndefinedTransactionStateException extends Exception {

    public UndefinedTransactionStateException() {
    }

    public UndefinedTransactionStateException(String message) {
        super(message);
    }
}
