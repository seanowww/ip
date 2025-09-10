package boyd.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task with a due date/time.
 * Accepts either a date-only string (defaults to 00:00)
 * or a date + time pair and formats as "uuuu-MM-dd HH:mm".
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"); // e.g., 2019-12-02 18:00

    private final String due;
    private final LocalDate date;
    private final LocalDateTime dateTime;

    /** Convenience: user gives one string "2019-12-02" (defaults time to 00:00). */
    public Deadline(String description, String date) {
        super(description);
        this.date = LocalDate.parse(date);
        this.dateTime = this.date.atTime(0, 0);
        this.due = this.dateTime.format(DEFAULT_DATETIME_FORMAT);
    }

    /** Convenience: user inputs date + time, e.g., "2019-12-02", "18:00". */
    public Deadline(String description, String date, String time) {
        super(description);
        this.date = LocalDate.parse(date);
        this.dateTime = LocalDateTime.parse(date + " " + time, DEFAULT_DATETIME_FORMAT);
        this.due = this.dateTime.format(DEFAULT_DATETIME_FORMAT);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + due + ")";
    }

    @Override
    public String toDataString() {
        return String.format(
                "D | %d | %s | %s", (
                        this.isDone ? 1 : 0),
                getDescription(),
                due
        );
    }
}
