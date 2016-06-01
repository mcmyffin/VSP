package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class ServiceNotAvaibleException extends Exception {

    public ServiceNotAvaibleException() {
    }

    public ServiceNotAvaibleException(String message) {
        super(message);
    }

    public ServiceNotAvaibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
