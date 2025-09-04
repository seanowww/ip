package boyd.utils;

import java.util.ArrayList;
import java.util.List;

import boyd.exceptions.BoydException;
import boyd.tasks.Task;

/**
 * Mutable, in-memory list of {@link Task} items with optional persistence.
 * <p>
 * This class is UI-agnostic: it does not print or format messages. Mutating
 * operations (add, remove, mark) persist via {@link Storage} when available.
 * Indices for user-facing operations are <strong>1-based</strong>.
 * </p>
 */
public class TaskList {
    private final List<Task> tasks;
    private final Storage storage; // may be null for in-memory only

    /**
     * Creates a {@code TaskList} initialized from an existing list (defensive copy).
     *
     * @param taskList initial tasks
     * @param storage persistence provider; may be {@code null} for in-memory only
     */
    public TaskList(List<? extends Task> taskList, Storage storage) {
        this.tasks = new ArrayList<>(taskList);
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
     * Adds a task to the end of the list and persists.
     *
     * @param task task to add
     * @return the added task
     */
    public Task add(Task task) {
        tasks.add(task);
        persist();
        return task;
    }

    /**
     * Removes the task at the given 1-based position and persists.
     *
     * @param itemNo 1-based index of the task to remove
     * @return the removed task
     * @throws BoydException if the index is out of range
     */
    public Task remove(int itemNo) {
        validate1Based(itemNo);
        Task removed = tasks.remove(itemNo - 1);
        persist();
        return removed;
    }

    /**
     * Returns all tasks in display order.
     * <p>
     * Callers should not modify the returned list structure. If you need a
     * strictly unmodifiable view, wrap this in {@code List.copyOf(getTasks())}.
     * </p>
     *
     * @return the underlying list of tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Marks the task at the given 1-based position as done and persists.
     *
     * @param itemNo 1-based index of the task to mark done
     * @return the task that was marked
     * @throws BoydException if the index is out of range
     */
    public Task mark(int itemNo) {
        validate1Based(itemNo);
        Task task = tasks.get(itemNo - 1);
        task.markAsDone();
        persist();
        return task;
    }

    /**
     * Finds tasks whose string representation contains the given keyword (case-insensitive).
     * <p>
     * This method does not modify or persist state. Returns an empty list if there
     * are no matches; callers may format user-facing messages as needed.
     * </p>
     *
     * @param keyword non-empty keyword to search for
     * @return a list of matching tasks (possibly empty)
     * @throws BoydException if {@code keyword} is {@code null} or blank
     */
    public List<Task> find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BoydException("Find requires a non-empty keyword.");
        }
        String needle = keyword.toLowerCase();
        List<Task> matches = new ArrayList<>();
        for (Task t : tasks) {
            if (t.toString().toLowerCase().contains(needle)) {
                matches.add(t);
            }
        }
        return matches;
    }

    /**
     * Returns the task at the given zero-based index (no persist).
     *
     * @param index zero-based index into this list
     * @return the {@link Task} at the given index
     * @throws BoydException if {@code index} is outside {@code [0, size())}
     */
    public Task get(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new BoydException("Can't get task at that index!");
        }
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list contains no tasks.
     *
     * @return {@code true} if there are no tasks; {@code false} otherwise
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Validates a 1-based index against the current list size. */
    private void validate1Based(int n) {
        if (n <= 0 || n > tasks.size()) {
            throw new BoydException("Invalid item number!");
        }
    }
}
