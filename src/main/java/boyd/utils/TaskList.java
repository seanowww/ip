package boyd.utils;

import boyd.tasks.Task;
import boyd.exceptions.BoydException;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final List<Task> tasks;
    private final Storage storage;
    private final Ui ui = new Ui();

    public TaskList(List<? extends Task> taskList, Storage storage) {
        this.tasks = new ArrayList<>(taskList);
        this.storage = storage;
    }

    private void persist() {
        if (storage != null) {
            storage.save(tasks);
        }
    }

    public void add(Task task) {
        tasks.add(task);
        String message = "Got it! Added:\n  " + task.toString() + "\n" +
                "Now you have " + tasks.size() + " tasks in this list.";
        ui.printWithLines(message);
        persist();
    }

    public void remove(int itemNo) {
        if (itemNo <= 0 || itemNo > tasks.size()) {
            throw new BoydException("Invalid item number!");
        }
        if (tasks.isEmpty()) {
            throw new BoydException("No items yet!");
        }
        Task task = tasks.get(itemNo - 1);
        String taskDescription = task.toString();
        tasks.remove(itemNo - 1);
        String message = "Noted! I've removed this task:\n" +
                "  " + taskDescription + "\n" +
                "Now you have " + tasks.size() + " tasks in this list.";
        ui.printWithLines(message);
        persist();
    }

    public void list() {
        StringBuilder message = new StringBuilder();
        if (tasks.isEmpty()) {
            throw new BoydException("You haven't added any items!");
        }
        for (int i = 0; i < tasks.size(); i++) {
            message.append((i + 1)).append(". ").append(tasks.get(i).toString());
            if (i < tasks.size() - 1) {
                message.append("\n");
            }
        }
        ui.printWithLines(message.toString());
    }

    public void mark(int itemNo) {
        if (itemNo <= 0 || itemNo > tasks.size()) {
            throw new BoydException("Invalid item number!");
        }
        if (tasks.isEmpty()) {
            throw new BoydException("No items yet!");
        }
        tasks.get(itemNo - 1).markAsDone();
        String message = "Nice! I've marked this task as done:\n" +
                "  " + tasks.get(itemNo - 1).toString();
        ui.printWithLines(message);
        persist();
    }

    // boyd/utils/TaskList.java
    public void find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BoydException("Find requires a non-empty keyword.");
        }

        String needle = keyword.toLowerCase();
        TaskList matches = new TaskList(new ArrayList<>(), storage);

        for (Task t : tasks) {
            if (t.toString().toLowerCase().contains(needle)) {
                matches.add(t);
            }
        }

        if (matches.isEmpty()) {
            ui.printWithLines("No matching tasks found.");
            return;
        }

        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:")
                .append(System.lineSeparator());
        for (int i = 0; i < matches.size(); i++) {
            sb.append(i + 1).append(". ").append(matches.get(i).toString());
            if (i < matches.size() - 1) sb.append(System.lineSeparator());
        }
        ui.printWithLines(sb.toString());
    }

    public Task get(int i) {
        if (tasks.isEmpty() || i > tasks.size()) {
            throw new BoydException("Can't get task at that index!");
        }
        return tasks.get(i);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }
}
