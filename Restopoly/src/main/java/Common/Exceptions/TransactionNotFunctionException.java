package Common.Exceptions;

/**
 * Created by sasa on 12.04.16.
 */
public class TransactionNotFunctionException extends Exception {

    public TransactionNotFunctionException() {
    }

    public TransactionNotFunctionException(String message) {
        super(message);
    }
}
