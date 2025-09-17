package boyd.utils;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import boyd.exceptions.BoydException;
import boyd.tasks.Deadline;
import boyd.tasks.Event;
import boyd.tasks.Task;
import boyd.tasks.ToDo;

/**
 * Parses user input into commands and applies them to a {@link TaskList}.
 *
 * <p>This parser is UI-agnostic: it returns {@link BoydResponse} values that
 * encode the message and status (OK/ERROR/EXIT) instead of printing.</p>
 */
public final class Parser {

    private Parser() {
        // Utility class; do not instantiate.
    }

    /**
     * Handles a single line of user input by mutating the given {@link TaskList} as needed
     * and returning a {@link BoydResponse} describing the outcome.
     *
     * @param input raw user input (non-null)
     * @param tasks task list to operate on (non-null)
     * @return a {@link BoydResponse} with the formatted message and status flags
     * @throws IllegalArgumentException if {@code input} or {@code tasks} is {@code null}
     */
    public static BoydResponse handle(String input, TaskList tasks) {
        if (input == null) {
            throw new IllegalArgumentException("input must be non-null");
        }
        if (tasks == null) {
            throw new IllegalArgumentException("tasks must be non-null");
        }

        final String trimmed = input.trim();
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
                assert taskList != null : "TaskList.getTasks() must not return null";
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
                        "Nice! I've marked this task as done:%n  %s", task);
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
            // Expected, user-recoverable errors
            return BoydResponse.error(e.getMessage());
        } catch (RuntimeException e) {
            // Unexpected parser/runtime errors (do not leak details)
            return BoydResponse.error("Something went wrong. Please try again.");
        }
    }

    /**
     * Formats tasks as a numbered list (1-based), one per line.
     *
     * @param items tasks to format (non-null; must not contain null)
     * @return numbered list string
     */
    private static String formatNumbered(List<Task> items) {
        assert items != null : "items must be non-null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            Task task = items.get(i);
            assert task != null : "items must not contain null elements";
            if (i > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(i + 1).append(". ").append(task);
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
        assert line != null : "line must be non-null";
        assert cmd != null && !cmd.isBlank() : "cmd must be non-blank";

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
     * Parses a description-with-tags string into its description and tags.
     *
     * @param descriptionWithTags text that may contain words and #tags
     * @return a {@link ParsedInput} containing description and tags
     */
    public static ParsedInput parseDescriptionAndTags(String descriptionWithTags) {
        String[] parts = descriptionWithTags.trim().split("\\s+");

        StringBuilder descBuilder = new StringBuilder();
        List<String> tags = new ArrayList<>();

        for (String part : parts) {
            if (part.startsWith("#") && part.length() > 1) {
                tags.add(part.substring(1).toLowerCase()); // drop "#" and standardise to lowercase
            } else {
                descBuilder.append(part).append(" ");
            }
        }

        String description = descBuilder.toString().trim();
        if (description.isEmpty()) {
            throw new BoydException("You cannot add tags without a description!");
        }
        return new ParsedInput(description, tags);
    }

    /**
     * Parses an "add task" command into a concrete {@link Task}.
     *
     * <p>Accepted forms:</p>
     * <ul>
     *   <li>{@code todo <description>}</li>
     *   <li>{@code deadline <description> /by <yyyy-MM-dd HH:mm>}</li>
     *   <li>{@code deadline <description> /by <yyyy-MM-dd>}</li>
     *   <li>{@code event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>}</li>
     * </ul>
     *
     * @param input full user command (non-null)
     * @return a new {@link ToDo}, {@link Deadline}, or {@link Event}
     * @throws IllegalArgumentException if {@code input} is {@code null}
     * @throws BoydException if the command is unknown or malformed
     */
    public static Task parseTask(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must be non-null");
        }

        String[] parts = input.trim().split("\\s+", 2);
        if (parts.length == 0 || parts[0].isBlank()) {
            throw new BoydException("Unknown command: (empty)");
        }

        final CommandType commandType;
        try {
            commandType = CommandType.fromString(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new BoydException("Unknown command: " + parts[0]);
        }

        assert commandType != null;

        switch (commandType) {
        case TODO -> {
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                throw new BoydException("The description of a todo cannot be empty!");
            }
            String desc = parts[1].trim();
            return new ToDo(desc);
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
            Task task;
            try {
                if (dateTimeChunks.length == 2) {
                    task = new Deadline(desc, dateTimeChunks[0], dateTimeChunks[1]); // yyyy-MM-dd HH:mm
                } else {
                    task = new Deadline(desc, by); // yyyy-MM-dd (defaults inside Deadline)
                }
                return task;
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
            Task task;
            String[] toSplit = fromSplit[1].split("\\s+/to\\s+", 2);
            if (toSplit.length < 2 || toSplit[0].isBlank() || toSplit[1].isBlank()) {
                throw new BoydException("Event must have both a '/from' and a '/to' time.");
            }
            String from = toSplit[0].trim(); // "yyyy-MM-dd HH:mm"
            String to = toSplit[1].trim(); // "yyyy-MM-dd HH:mm"
            // Event constructor validates datetime format; surface as user-facing error.
            try {
                task = new Event(eventDesc, from, to);
            } catch (DateTimeParseException e) {
                throw new BoydException("Datetime format must be: yyyy-MM-dd HH:mm.");
            }
            return task;
        }
        default -> throw new BoydException("Unknown command: " + parts[0]);
        }
    }
}
