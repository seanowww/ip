import Tasks.*;
import exceptions.BoydException;
import utils.CommandType;

public class TaskFactory {
    public static Task parseTask(String input) {
        String[] parts = input.trim().split("\\s+", 2); // split into two chunks
        CommandType commandType;

        try {
            commandType = CommandType.fromString(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new BoydException("Unknown command: " + parts[0]);
        }

        switch (commandType) {
            case TODO:
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    throw new BoydException("The description of a todo cannot be empty!");
                }
                return new ToDo(parts[1]);

            case DEADLINE:
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    throw new BoydException("The description of a deadline cannot be empty!");
                }

                String[] splitDeadline = parts[1].split(" /by ", 2);
                if (splitDeadline.length < 2 || splitDeadline[0].trim().isEmpty() || splitDeadline[1].trim().isEmpty()) {
                    throw new BoydException("Deadline must have a description and a '/by' date.");
                }

                String desc = splitDeadline[0].trim();
                String by = splitDeadline[1].trim();
                return new Deadline(desc, by);

            case EVENT:
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    throw new BoydException("The description of an event cannot be empty!");
                }

                String[] fromSplit = parts[1].split(" /from ", 2);
                if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty()) {
                    throw new BoydException("Event must have a description and a '/from' time.");
                }

                String eventDesc = fromSplit[0].trim();
                String[] toSplit = fromSplit[1].split(" /to ", 2);
                if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
                    throw new BoydException("Event must have both a '/from' and a '/to' time.");
                }

                String from = toSplit[0].trim();
                String to = toSplit[1].trim();
                return new Event(eventDesc, from, to);

            default:
                throw new BoydException("Unknown command: " + parts[0]);
        }
    }
}
