package boyd.exceptions;

/**
 * Application-specific unchecked exception for user-facing errors.
 */
public class BoydException extends RuntimeException {

    /**
     * Creates a new exception with a user-friendly message.
     *
     * @param message the error message to display
     */
    public BoydException(String message) {
        super(message);
    }
}
