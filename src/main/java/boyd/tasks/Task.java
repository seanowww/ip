package boyd.tasks;

/**
 * Base class for all tasks.
 * Holds a description and completion state, and defines the
 * serialization hook {@link #toDataString()} for storage.
 */
public abstract class Task {

    protected final String description;

    protected boolean isDone;

    /**
     * Constructs a task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns a one-character status icon used in display strings.
     *
     * @return {@code "X"} if done, otherwise a single space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /** @return the task description */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }

    /**
     * Produces a single-line representation suitable for saving to disk.
     * Implementations must return exactly one line without trailing newline.
     *
     * @return the storage line for this task
     */
    public abstract String toDataString();
}
