package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class BankNotFoundException extends Exception {

    public BankNotFoundException() {
    }

    public BankNotFoundException(String message) {
        super(message);
    }
}
