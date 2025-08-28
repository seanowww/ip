package boyd.utils;

import boyd.tasks.Task;
import boyd.exceptions.BoydException;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final List<Task> mem;
    private final Storage storage;
    private final Ui ui = new Ui();

    public TaskList(List<? extends Task> taskList, Storage storage) {
        this.mem = new ArrayList<>(taskList);
        this.storage = storage;
    }

    private void persist() {
        if (storage != null) {
            storage.save(mem);
        }
    }

    public void add(Task task) {
        mem.add(task);
        String message = "Got it! Added:\n  " + task.toString() + "\n" +
                "Now you have " + mem.size() + " tasks in this list.";
        ui.printWithLines(message);
        persist();
    }

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

    public int size() {
        return mem.size();
    }
}
