package boyd.utils;

import java.util.ArrayList;
import java.util.List;

import boyd.exceptions.BoydException;
import boyd.tasks.Task;

public class TaskList {
    private final List<Task> tasks;
    private final Storage storage;
    private final Ui ui = new Ui();

    public TaskList(List<? extends Task> taskList, Storage storage) {
        this.tasks = new ArrayList<>(taskList); // defensive copy
        this.storage = storage;
    }

    private void persist() {
        if (storage != null) {
            storage.save(tasks);
        }
    }

    public void add(Task task) {
        tasks.add(task);
        String message = String.format(
                "Got it! Added:%n  %s%nNow you have %d tasks in this list.",
                task, tasks.size());
        ui.printWithLines(message);
        persist();
    }

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

    public int size() {
        return tasks.size();
    }
}
