package boyd.utils;

import boyd.tasks.Deadline;
import boyd.tasks.Event;
import boyd.tasks.Task;
import boyd.tasks.ToDo;
import boyd.exceptions.BoydException;

import java.time.format.DateTimeParseException;

/**
 * Parses user input strings into commands for the Boyd CLI and applies them to a {@link TaskList}.
 * <p>
 * Supported commands (case-insensitive):
 * <pre>{@code
 * bye
 * list
 * mark <number>
 * delete <number>
 * todo <description>
 * deadline <description> /by <yyyy-MM-dd [HH:mm]>
 * event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>
 * }</pre>
 * Indices shown to users are 1-based.
 * </p>
 */
public class Parser {

    /**
     * Handles a single line of user input by either mutating/printing from the given {@link TaskList}
     * or signaling termination.
     *
     * @param input the raw user input line
     * @param tasks the task list to operate on
     * @return {@code true} if the caller should exit (i.e., user typed {@code bye}); {@code false} otherwise
     * @throws BoydException for unknown commands or invalid arguments
     */
    public static boolean handle(String input, TaskList tasks) {
        String trimmed = input.trim();
        if (trimmed.equalsIgnoreCase("bye")) {
            return true;
        }

        // simple commands
        if (trimmed.equalsIgnoreCase("list")) {
            tasks.list();
            return false;
        }
        if (trimmed.startsWith("mark")) {
            int idx = parseIndex(trimmed, "mark");
            tasks.mark(idx);
            return false;
        }
        if (trimmed.startsWith("delete")) {
            int idx = parseIndex(trimmed, "delete");
            tasks.remove(idx);
            return false;
        }

        // otherwise treat as an "add task" command
        Task t = parseTask(trimmed);
        tasks.add(t);
        return false;
    }

    /**
     * Parses a 1-based index from commands of the form {@code "<cmd> <number>"}.
     *
     * @param line the full command line
     * @param cmd  the command keyword (e.g., {@code "mark"} or {@code "delete"}) used for error messages
     * @return the parsed integer index (1-based)
     * @throws BoydException if the number is missing or not a valid integer
     */
    private static int parseIndex(String line, String cmd) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new BoydException("Command should be: \"" + cmd + " <number>\"");
        }
        try {
            return Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new BoydException("You must enter a valid number after '" + cmd + "'.");
        }
    }

    /**
     * Parses an "add task" command into a concrete {@link Task}.
     * <p>
     * Accepted forms:
     * <ul>
     *   <li>{@code todo <description>}</li>
     *   <li>{@code deadline <description> /by <yyyy-MM-dd HH:mm>}</li>
     *   <li>{@code deadline <description> /by <yyyy-MM-dd>} (time defaults inside {@link Deadline})</li>
     *   <li>{@code event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>}</li>
     * </ul>
     * </p>
     *
     * @param input the full user command (not trimmed further than leading/trailing spaces)
     * @return a newly constructed {@link ToDo}, {@link Deadline}, or {@link Event}
     * @throws BoydException if the command keyword is unknown, required parts are missing,
     *                       or date/time formats are invalid
     */
    public static Task parseTask(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        CommandType commandType;
        try {
            commandType = CommandType.fromString(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new BoydException("Unknown command: " + parts[0]);
        }

        switch (commandType) {
            case TODO -> {
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    throw new BoydException("The description of a todo cannot be empty!");
                }
                return new ToDo(parts[1].trim());
            }
            case DEADLINE -> {
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    throw new BoydException("The description of a deadline cannot be empty!");
                }
                String[] splitDeadline = parts[1].split("\\s+/by\\s+", 2);
                if (splitDeadline.length < 2 || splitDeadline[0].isBlank() || splitDeadline[1].isBlank()) {
                    throw new BoydException("Deadline must have a description and a '/by' date.");
                }
                String desc = splitDeadline[0].trim();
                String by = splitDeadline[1].trim();
                String[] dateTimeChunks = by.split("\\s+", 2);
                try {
                    if (dateTimeChunks.length == 2) {
                        return new Deadline(desc, dateTimeChunks[0], dateTimeChunks[1]); // yyyy-MM-dd HH:mm
                    } else {
                        return new Deadline(desc, by); // yyyy-MM-dd (defaults to 00:00)
                    }
                } catch (DateTimeParseException e) {
                    throw new BoydException("Datetime format must be: yyyy-MM-dd HH:mm or yyyy-MM-dd.");
                }
            }
            case EVENT -> {
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    throw new BoydException("The description of an event cannot be empty!");
                }
                String[] fromSplit = parts[1].split("\\s+/from\\s+", 2);
                if (fromSplit.length < 2 || fromSplit[0].isBlank()) {
                    throw new BoydException("Event must have a description and a '/from' time.");
                }
                String eventDesc = fromSplit[0].trim();
                String[] toSplit = fromSplit[1].split("\\s+/to\\s+", 2);
                if (toSplit.length < 2 || toSplit[0].isBlank() || toSplit[1].isBlank()) {
                    throw new BoydException("Event must have both a '/from' and a '/to' time.");
                }
                String from = toSplit[0].trim(); // "yyyy-MM-dd HH:mm"
                String to   = toSplit[1].trim(); // "yyyy-MM-dd HH:mm"
                return new Event(eventDesc, from, to); // let Event validate/parse
            }
            default -> throw new BoydException("Unknown command: " + parts[0]);
        }
    }
}
