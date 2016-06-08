package Common.Exceptions;

/**
 * Created by dima on 12.04.16.
 */
public class RequiredJsonParamsNotFoundException extends Exception {

    public RequiredJsonParamsNotFoundException() {
    }

    public RequiredJsonParamsNotFoundException(String message) {
        super(message);
    }
}
