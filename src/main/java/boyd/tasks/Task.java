package boyd.tasks;

/**
 * Base class for all tasks in the Boyd app.
 * <p>
 * A task has a textual {@code description} and a completion flag {@code isDone}.
 * Subclasses (e.g., {@link ToDo}, {@link Deadline}, {@link Event}) add any
 * extra metadata and define their own persistence format via {@link #toDataString()}.
 * </p>
 */
public abstract class Task {

    protected final String description;

    protected boolean isDone;

    /**
     * Creates a new task with the given description, initially not done.
     *
     * @param description the task description (not {@code null})
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the single-character status icon used in list displays.
     * <ul>
     *   <li>{@code "X"} — task is done</li>
     *   <li>{@code " "} — task is not done</li>
     * </ul>
     *
     * @return {@code "X"} if done; otherwise a single space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     * <p>Keep or remove depending on whether your CLI supports "unmark".</p>
     */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /** @return the task description */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the user-facing representation, e.g. {@code "[X] read book"}.
     *
     * @return display string combining status icon and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }

    /**
     * Serializes this task to the on-disk format used by {@code Storage}.
     * <p>
     * Each subclass defines its own layout, e.g.:
     * <ul>
     *   <li>{@link ToDo}: {@code T | <0|1> | <description>}</li>
     *   <li>{@link Deadline}: {@code D | <0|1> | <description> | <yyyy-MM-dd HH:mm>}</li>
     *   <li>{@link Event}: {@code E | <0|1> | <description> | <from> - <to>}</li>
     * </ul>
     * </p>
     *
     * @return persistence string for this task
     */
    public abstract String toDataString();
}
