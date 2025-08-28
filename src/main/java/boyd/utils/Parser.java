package boyd.utils;

import boyd.tasks.Deadline;
import boyd.tasks.Event;
import boyd.tasks.Task;
import boyd.tasks.ToDo;
import boyd.exceptions.BoydException;

import java.time.format.DateTimeParseException;

public class Parser {
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

        if (trimmed.startsWith("find")) {
            String[] parts = trimmed.split("\\s+", 2);
            if (parts.length < 2 || parts[1].isBlank()) {
                throw new BoydException("Command should be: \"find <keyword>\"");
            }
            tasks.find(parts[1].trim());
            return false;
        }

        // otherwise treat as an "add task" command
        Task t = parseTask(trimmed);
        tasks.add(t);
        return false;
    }

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
