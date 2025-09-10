package boyd.tasks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    protected final Set<String> tags = new HashSet<>();

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
     * Adds a tag to the set of tags
     *
     * @param tag the tag to be added (not {@code null})
     */
    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public Set<String> getTags() {
        return this.tags;
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
     * Returns the user-facing representation, e.g. {@code "[X] read book <tags>"}.
     *
     * @return display string combining status icon, description and tags
     */
    @Override
    public String toString() {
        String str = "[" + getStatusIcon() + "] " + this.description;
        if (!tags.isEmpty()) {
            str += " " + tags.stream()
                    .map(tag -> "#" + tag)
                    .collect(Collectors.joining(" "));
        }
        return str;
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

    protected String formatTags() {
        if (tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags);
    }
}
