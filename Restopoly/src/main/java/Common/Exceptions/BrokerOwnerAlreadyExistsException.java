package Common.Exceptions;

/**
 * Created by sasa on 12.04.16.
 */
public class BrokerOwnerAlreadyExistsException extends Exception {

    public BrokerOwnerAlreadyExistsException() {
    }

    public BrokerOwnerAlreadyExistsException(String message) {
        super(message);
    }
}
