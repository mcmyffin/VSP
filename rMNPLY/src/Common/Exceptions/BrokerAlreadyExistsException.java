package Common.Exceptions;

/**
 * Created by sasa on 11.05.16.
 */
public class BrokerAlreadyExistsException extends Exception {

        public BrokerAlreadyExistsException() {
        }

        public BrokerAlreadyExistsException(String message) {
            super(message);
        }
}
