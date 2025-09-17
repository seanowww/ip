package boyd.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task with a start and end time.
 * <p>
 * Accepts datetimes in the form {@code yyyy-MM-dd HH:mm} and stores them as
 * {@link LocalDateTime}, formatted consistently for display and persistence.
 * </p>
 */
public class Event extends Task {

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final String start; // normalized "uuuu-MM-dd HH:mm"
    private final String end; // normalized "uuuu-MM-dd HH:mm"

    /**
     * Creates an {@code Event} with validated start and end datetimes.
     *
     * @param description the event description
     * @param from        start datetime string (must be {@code yyyy-MM-dd HH:mm})
     * @param to          end datetime string (must be {@code yyyy-MM-dd HH:mm})
     * @throws java.time.format.DateTimeParseException if either datetime is invalid
     */
    public Event(String description, String from, String to) {
        super(description);
        this.startDateTime = parseDateOrDateTime(from);
        this.endDateTime = parseDateOrDateTime(to);
        this.start = this.startDateTime.format(DEFAULT_DATETIME_FORMAT);
        this.end = this.endDateTime.format(DEFAULT_DATETIME_FORMAT);
    }

    private static LocalDateTime parseDateOrDateTime(String input) {
        // Accept either yyyy-MM-dd HH:mm or yyyy-MM-dd (default to 00:00)
        String trimmed = input.trim();
        if (trimmed.indexOf(' ') > 0) {
            return LocalDateTime.parse(trimmed, DEFAULT_DATETIME_FORMAT);
        }
        LocalDate date = LocalDate.parse(trimmed);
        return date.atTime(0, 0);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }

    @Override
    public String toDataString() {
        return String.format(
                "E | %d | %s | %s - %s", (
                        this.isDone ? 1 : 0),
                getDescription(),
                start,
                end
        );
    }
}
