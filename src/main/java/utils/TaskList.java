package utils;

import Tasks.Task;
import exceptions.BoydException;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final List<Task> mem;
    private final Storage storage;
    private static String line = "____________________________________________________________";

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
        System.out.println(line);
        System.out.println("Got it! Added:\n  " + task.toString() + "\n" +
                "Now you have " + mem.size() + " tasks in this list.");
        System.out.println(line);
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
        System.out.println(line);
        System.out.println("Noted! I've removed this task:\n" +
                "  " + taskDescription + "\n" +
                "Now you have " + mem.size() + " tasks in this list.");
        System.out.println(line);
        persist();
    }

    public void list() {
        System.out.println(line);
        if (mem.isEmpty()) {
            throw new BoydException("You haven't added any items!");
        }
        for (int i = 0; i < mem.size(); i++) {
            System.out.println((i + 1) + ". " + mem.get(i).toString());
        }
        System.out.println(line);
    }

    public void mark(int itemNo) {
        if (itemNo <= 0 || itemNo > mem.size()) {
            throw new BoydException("Invalid item number!");
        }
        if (mem.isEmpty()) {
            throw new BoydException("No items yet!");
        }

        mem.get(itemNo - 1).markAsDone();
        System.out.println(line);
        System.out.println("Nice! I've marked this task as done:\n" +
                "  " + mem.get(itemNo - 1).toString());
        System.out.println(line);
        persist();
    }
}
