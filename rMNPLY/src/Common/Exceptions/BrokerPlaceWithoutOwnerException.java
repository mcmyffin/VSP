package Common.Exceptions;

/**
 * Created by sasa on 12.04.16.
 */
public class BrokerPlaceWithoutOwnerException extends Exception {

    public BrokerPlaceWithoutOwnerException() {
    }

    public BrokerPlaceWithoutOwnerException(String message) {
        super(message);
    }
}
