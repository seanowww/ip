import Tasks.*;

public class TaskFactory {
    public static Task parseTask(String input) {
        String[] parts = input.trim().split("\\s+", 2); //split into two chunks
        String command = parts[0].toLowerCase();

        if (command.equals("todo")) {
            return new ToDo(parts[1]);
        } else if (command.equals("deadline")) {
            String[] splitDeadline = parts[1].split(" /by ", 2);
            String desc = splitDeadline[0].trim();
            String by = splitDeadline[1].trim();
            return new Deadline(desc, by);
        } else if (command.equals("event")) {
            String[] fromSplit = parts[1].split(" /from ", 2);
            String desc = fromSplit[0].trim();

            String[] toSplit = fromSplit[1].split(" /to ", 2);
            String from = toSplit[0].trim();
            String to = toSplit[1].trim();
            return new Event(desc, from, to);
        } else {
            throw new IllegalArgumentException("Unknown command: " + command);
        }
    }
}
