package Common.Exceptions;

/**
 * Created by sasa on 12.04.16.
 */
public class WrongResponsCodeException extends Exception {

    public WrongResponsCodeException() {
    }

    public WrongResponsCodeException(String message) {
        super(message);
    }
}
