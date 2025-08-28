package boyd.utils;

import boyd.tasks.Task;
import boyd.exceptions.BoydException;

import java.util.ArrayList;
import java.util.List;

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

    private final List<Task> mem;

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
        this.mem = new ArrayList<>(taskList);
        this.storage = storage;
    }

    /**
     * Persists the current state if {@link Storage} is present.
     * <p>Does nothing when {@code storage == null}.</p>
     */
    private void persist() {
        if (storage != null) {
            storage.save(mem);
        }
    }

    /**
     * Adds a task to the end of the list, prints a confirmation, and persists.
     *
     * @param task task to add
     */
    public void add(Task task) {
        mem.add(task);
        String message = "Got it! Added:\n  " + task.toString() + "\n" +
                "Now you have " + mem.size() + " tasks in this list.";
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
        if (itemNo <= 0 || itemNo > mem.size()) {
            throw new BoydException("Invalid item number!");
        }
        if (mem.isEmpty()) {
            throw new BoydException("No items yet!");
        }
        Task task = mem.get(itemNo - 1);
        String taskDescription = task.toString();
        mem.remove(itemNo - 1);
        String message = "Noted! I've removed this task:\n" +
                "  " + taskDescription + "\n" +
                "Now you have " + mem.size() + " tasks in this list.";
        ui.printWithLines(message);
        persist();
    }

    /**
     * Prints the tasks in display order, numbered starting at 1 and framed by lines.
     *
     * @throws BoydException if the list is empty
     */
    public void list() {
        StringBuilder message = new StringBuilder();
        if (mem.isEmpty()) {
            throw new BoydException("You haven't added any items!");
        }
        for (int i = 0; i < mem.size(); i++) {
            message.append((i + 1)).append(". ").append(mem.get(i).toString());
            if (i < mem.size() - 1) {
                message.append("\n");
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
        if (itemNo <= 0 || itemNo > mem.size()) {
            throw new BoydException("Invalid item number!");
        }
        if (mem.isEmpty()) {
            throw new BoydException("No items yet!");
        }
        mem.get(itemNo - 1).markAsDone();
        String message = "Nice! I've marked this task as done:\n" +
                "  " + mem.get(itemNo - 1).toString();
        ui.printWithLines(message);
        persist();
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return task count
     */
    public int size() {
        return mem.size();
    }
}
