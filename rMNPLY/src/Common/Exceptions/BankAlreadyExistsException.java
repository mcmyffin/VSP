package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class BankAlreadyExistsException extends Exception {

    public BankAlreadyExistsException() {
    }

    public BankAlreadyExistsException(String message) {
        super(message);
    }
}
