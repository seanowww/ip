package boyd.utils;

/**
 * Enumerates the supported top-level user commands understood by the application.
 * <p>
 * Values are matched case-insensitively from user input tokens.
 * </p>
 */
public enum CommandType {
    /** Create a plain to-do task. */
    TODO,
    /** Create a deadline task (with a due date/time). */
    DEADLINE,
    /** Create an event task (with a start and end time). */
    EVENT;

    /**
     * Parses a user-facing command token into a {@link CommandType}.
     *
     * @param command the command token (e.g., {@code "todo"}, {@code "deadline"}, {@code "event"}); must be non-null
     * @return the corresponding {@code CommandType}
     * @throws IllegalArgumentException if {@code command} does not map to a supported command type
     */
    public static CommandType fromString(String command) {
        switch (command.toLowerCase()) {
        case "todo":
            return TODO;
        case "deadline":
            return DEADLINE;
        case "event":
            return EVENT;
        default:
            throw new IllegalArgumentException("Unknown command: " + command);
        }
    }
}
