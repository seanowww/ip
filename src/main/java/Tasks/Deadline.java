package Tasks;

public class Deadline extends Task{

    String deadline;

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline + ")";
    }

    @Override
    public String toDataString() {
        return String.format("D | %d | %s | %s",
                (this.isDone ? 1 : 0),
                super.description,
                deadline);
    }
}
