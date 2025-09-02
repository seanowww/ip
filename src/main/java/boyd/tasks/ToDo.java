package boyd.tasks;

/**
 * Represents a simple to-do task without any date/time.
 */
public class ToDo extends Task {

    /**
     * Creates a new {@code ToDo} with the given description.
     *
     * @param description the task description
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toDataString() {
        return String.format(
                "T | %d | %s", (
                        this.isDone ? 1 : 0),
                super.description
        );
    }
}
