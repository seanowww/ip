package boyd.utils;

/**
 * Immutable result object representing the outcome of a parsed command.
 *
 * <p>Encodes a response message and two status flags: whether the program
 * should exit and whether the response represents an error.</p>
 */
public final class BoydResponse {

    /** User-facing message text. */
    private final String message;

    /** True if this response signals program termination. */
    private final boolean exit;

    /** True if this response represents an error. */
    private final boolean error;

    private BoydResponse(String message, boolean exit, boolean error) {
        assert message != null : "Message must not be null";
        this.message = message;
        this.exit = exit;
        this.error = error;
    }

    /**
     * Creates a normal (non-error, non-exit) response.
     *
     * @param message response message
     * @return a success {@code BoydResponse}
     */
    public static BoydResponse ok(String message) {
        return new BoydResponse(message, false, false);
    }

    /**
     * Creates an error response.
     *
     * @param message error message
     * @return an error {@code BoydResponse}
     */
    public static BoydResponse error(String message) {
        return new BoydResponse(message, false, true);
    }

    /**
     * Creates an exit response.
     *
     * @param message farewell message
     * @return an exit {@code BoydResponse}
     */
    public static BoydResponse exit(String message) {
        return new BoydResponse(message, true, false);
    }

    /**
     * Returns the user-facing message text.
     *
     * @return message string
     */
    public String message() {
        return this.message;
    }

    /**
     * Returns true if this response signals program termination.
     *
     * @return true if exit, false otherwise
     */
    public boolean isExit() {
        return this.exit;
    }

    /**
     * Returns true if this response represents an error.
     *
     * @return true if error, false otherwise
     */
    public boolean isError() {
        return this.error;
    }
}
