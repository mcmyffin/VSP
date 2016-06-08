package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}
