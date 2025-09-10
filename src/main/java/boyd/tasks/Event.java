package boyd.tasks;

/**
 * Represents an event task with a start and end time.
 * The times are stored as strings (e.g., "yyyy-MM-dd HH:mm").
 */
public class Event extends Task {

    private final String from;
    private final String to;

    /**
     * Creates an {@code Event}.
     *
     * @param description the event description
     * @param from        start datetime string
     * @param to          end datetime string
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toDataString() {
        return String.format(
                "E | %d | %s | %s - %s | %s", (
                        this.isDone ? 1 : 0),
                getDescription(),
                from,
                to,
                super.formatTags()
        );
    }
}
