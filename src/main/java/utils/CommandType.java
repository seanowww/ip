package utils;

public enum CommandType {
    TODO,
    DEADLINE,
    EVENT;

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
