package boyd.utils;

import java.util.ArrayList;
import java.util.List;

import boyd.exceptions.BoydException;
import boyd.tasks.Task;

/**
 * Mutable, in-memory list of {@link Task} items with simple persistence and UI output.
 * <p>
 * This class manages tasks, prints user-facing messages via {@link Ui}, and
 * persists changes via {@link Storage} when available.
 * Indices passed to mutating methods are <strong>1-based</strong> (as shown to users).
 * </p>
 *
 * @implNote All methods that change the list call {@link #persist()} which is a no-op if
 * {@link Storage} is {@code null}. User-visible messages are printed inside this class.
 */
public class TaskList {
    private final List<Task> tasks;
    private final Storage storage;

    private final Ui ui = new Ui();

    /**
     * Creates a {@code TaskList} initialized from an existing list.
     * The provided list is copied defensively.
     *
     * @param taskList initial tasks (copied)
     * @param storage  persistence provider; may be {@code null} for in-memory only
     */
    public TaskList(List<? extends Task> taskList, Storage storage) {
        this.tasks = new ArrayList<>(taskList); // defensive copy
        this.storage = storage;
    }

    /**
     * Persists the current state if {@link Storage} is present.
     * <p>Does nothing when {@code storage == null}.</p>
     */
    private void persist() {
        if (storage != null) {
            storage.save(tasks);
        }
    }

    /**
     * Adds a task to the end of the list, prints a confirmation, and persists.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
        String message = String.format(
                "Got it! Added:%n  %s%nNow you have %d tasks in this list.",
                task, tasks.size());
        ui.printWithLines(message);
        persist();
    }

    /**
     * Removes the task at the given 1-based position, prints a confirmation, and persists.
     *
     * @param itemNo 1-based index of the task to remove
     * @throws BoydException if the index is out of range or the list is empty
     */
    public void remove(int itemNo) {
        if (itemNo <= 0 || itemNo > tasks.size()) {
            throw new BoydException("Invalid item number!");
        }
        Task removed = tasks.remove(itemNo - 1);
        String message = String.format(
                "Noted! I've removed this task:%n  %s%nNow you have %d tasks in this list.",
                removed, tasks.size());
        ui.printWithLines(message);
        persist();
    }

    /**
     * Prints the tasks in display order, numbered starting at 1 and framed by lines.
     *
     * @throws BoydException if the list is empty
     */
    public void list() {
        if (tasks.isEmpty()) {
            throw new BoydException("You haven't added any items!");
        }
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            message.append(i + 1).append(". ").append(tasks.get(i));
            if (i < tasks.size() - 1) {
                message.append(System.lineSeparator());
            }
        }
        ui.printWithLines(message.toString());
    }

    /**
     * Marks the task at the given 1-based position as done, prints a confirmation, and persists.
     *
     * @param itemNo 1-based index of the task to mark done
     * @throws BoydException if the index is out of range or the list is empty
     */
    public void mark(int itemNo) {
        if (itemNo <= 0 || itemNo > tasks.size()) {
            throw new BoydException("Invalid item number!");
        }
        Task task = tasks.get(itemNo - 1);
        task.markAsDone();
        String message = String.format(
                "Nice! I've marked this task as done:%n  %s",
                task);
        ui.printWithLines(message);
        persist();
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }
}
