package Tasks;

public class Event extends Task{
    String from;
    String to;

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
        return String.format("E | %d | %s | %s - %s",
                (this.isDone ? 1 : 0),
                super.description,
                from,
                to);
    }
}
