package boyd.utils;

import java.time.format.DateTimeParseException;
import java.util.List;

import boyd.exceptions.BoydException;
import boyd.tasks.Deadline;
import boyd.tasks.Event;
import boyd.tasks.Task;
import boyd.tasks.ToDo;

/**
 * Parses user input into commands and applies them to a {@link TaskList}.
 * <p>
 * This class is UI-agnostic: it returns {@link BoydResponse} values that
 * encode the message and status (OK/ERROR/EXIT) instead of printing.
 * </p>
 *
 * <h2>Supported commands (case-insensitive)</h2>
 * <pre>{@code
 * bye
 * list
 * mark <number>
 * delete <number>
 * find <keyword>
 * todo <description>
 * deadline <description> /by <yyyy-MM-dd [HH:mm]>
 * event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>
 * }</pre>
 * Indices shown to users are 1-based.
 */
public class Parser {

    private Parser() {
        // utility class
    }

    /**
     * Handles a single line of user input by mutating the given {@link TaskList} as needed
     * and returning a {@link BoydResponse} describing the outcome.
     *
     * @param input raw user input
     * @param tasks task list to operate on
     * @return a {@link BoydResponse} with the formatted message and status flags
     */
    public static BoydResponse handle(String input, TaskList tasks) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return BoydResponse.error("Command cannot be empty.");
        }
        if (trimmed.equalsIgnoreCase("bye")) {
            return BoydResponse.exit("Bye. Hope to see you again soon!");
        }

        try {
            // simple commands
            if (trimmed.equalsIgnoreCase("list")) {
                List<Task> taskList = tasks.getTasks();
                if (taskList.isEmpty()) {
                    return BoydResponse.error("You haven't added any items!");
                }
                String message = formatNumbered(taskList);
                return BoydResponse.ok(message);
            }

            if (trimmed.startsWith("mark")) {
                int idx = parseIndex(trimmed, "mark");
                Task task = tasks.mark(idx);
                String message = String.format(
                        "Nice! I've marked this task as done:%n  %s",
                        task);
                return BoydResponse.ok(message);
            }

            if (trimmed.startsWith("delete")) {
                int idx = parseIndex(trimmed, "delete");
                Task removedTask = tasks.remove(idx);
                String message = String.format(
                        "Noted! I've removed this task:%n  %s%nNow you have %d tasks in this list.",
                        removedTask, tasks.size());
                return BoydResponse.ok(message);
            }

            if (trimmed.startsWith("find")) {
                String[] parts = trimmed.split("\\s+", 2);
                if (parts.length < 2 || parts[1].isBlank()) {
                    throw new BoydException("Command should be: \"find <keyword>\"");
                }
                List<Task> matches = tasks.find(parts[1].trim());
                if (matches.isEmpty()) {
                    return BoydResponse.ok("No matching tasks found.");
                }
                String message = formatNumbered(matches);
                return BoydResponse.ok(message);
            }

            // otherwise treat as an "add task" command
            Task t = parseTask(trimmed);
            Task added = tasks.add(t);
            String message = String.format(
                    "Got it! Added:%n  %s%nNow you have %d tasks in this list.",
                    added, tasks.size());
            return BoydResponse.ok(message);

        } catch (BoydException e) {
            return BoydResponse.error(e.getMessage());
        } catch (Exception e) {
            // Guard against unexpected errors without exposing internals
            return BoydResponse.error("Something went wrong. Please try again.");
        }
    }

    /**
     * Formats tasks as a numbered list (1-based), one per line.
     *
     * @param items tasks to format
     * @return numbered list string
     */
    private static String formatNumbered(List<Task> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(i + 1).append(". ").append(items.get(i));
        }
        return sb.toString();
    }

    /**
     * Parses a 1-based index from commands of the form {@code "<cmd> <number>"}.
     *
     * @param line full command line
     * @param cmd  command keyword used for error messages
     * @return parsed integer index (1-based)
     * @throws BoydException if the number is missing or invalid
     */
    private static int parseIndex(String line, String cmd) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new BoydException("Command should be: \"" + cmd + " <number>\"");
        }
        // Ensure the first token is exactly the command (avoid "market 1" being treated as "mark 1")
        if (!parts[0].equalsIgnoreCase(cmd)) {
            throw new BoydException("Unknown command: " + parts[0]);
        }
        try {
            return Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new BoydException("You must enter a valid number after '" + cmd + "'.");
        }
    }

    /**
     * Parses an "add task" command into a concrete {@link Task}.
     *
     * <p>Accepted forms:</p>
     * <ul>
     *   <li>{@code todo <description>}</li>
     *   <li>{@code deadline <description> /by <yyyy-MM-dd HH:mm>}</li>
     *   <li>{@code deadline <description> /by <yyyy-MM-dd>} (time defaults inside {@link Deadline})</li>
     *   <li>{@code event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>}</li>
     * </ul>
     *
     * @param input full user command
     * @return a new {@link ToDo}, {@link Deadline}, or {@link Event}
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
                    return new Deadline(desc, by); // yyyy-MM-dd (defaults inside Deadline)
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
            String to = toSplit[1].trim();   // "yyyy-MM-dd HH:mm"
            return new Event(eventDesc, from, to); // let Event validate/parse
        }
        default -> throw new BoydException("Unknown command: " + parts[0]);
        }
    }
}
