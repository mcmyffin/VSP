package Common.Exceptions;

/**
 * Created by sasa on 12.04.16.
 */
public class BrokerNotFoundException extends Exception {

    public BrokerNotFoundException() {
    }

    public BrokerNotFoundException(String message) {
        super(message);
    }
}
