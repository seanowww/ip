import Tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exceptions.*;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import utils.Storage;

public class Boyd {
    private static List<Task> mem = new ArrayList<>();
    private static String line = "____________________________________________________________";
    private static String chatbotName = "Boyd";
    private static FileWriter myWriter;
    private static final Storage storage = new Storage();

    public static void main(String[] args) {
        mem = storage.load();
        greet();
        echo();
    }

    public static void greet() {
        System.out.println(line);
        System.out.println("Hello! I'm " + chatbotName + "!\nWhat can I do for you?");
        System.out.println(line);
    }

    public static void bye() {
        System.out.println(line);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }

    public static void echo() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("bye")) break;

            try {
                // mark <number>
                if (command.startsWith("mark")) {
                    if (!command.startsWith("mark ")) {
                        throw new BoydException("Command should be of the format: \"mark <number>\"");
                    }
                    int itemNo = Integer.parseInt(command.substring(5).trim());
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
                    storage.save(mem);
                    continue;
                }

                // delete <number>
                if (command.startsWith("delete")) {
                    if (!command.startsWith("delete ")) {
                        throw new BoydException("Command should be of the format: \"delete <number>\"");
                    }
                    int itemNo = Integer.parseInt(command.substring(7).trim());
                    if (itemNo <= 0 || itemNo > mem.size()) {
                        throw new BoydException("Invalid item number!");
                    }
                    if (mem.isEmpty()) {
                        throw new BoydException("No items yet!");
                    }

                    String taskDescription = mem.get(itemNo - 1).toString();
                    mem.remove(itemNo - 1);
                    System.out.println(line);
                    System.out.println("Noted! I've removed this task:\n" +
                            "  " + taskDescription + "\n" +
                            "Now you have " + mem.size() + " tasks in this list.");
                    System.out.println(line);
                    storage.save(mem);
                    continue;
                }

                // list
                if (command.equalsIgnoreCase("list")) {
                    System.out.println(line);
                    if (mem.isEmpty()) {
                        throw new BoydException("You haven't added any items!");
                    }
                    for (int i = 0; i < mem.size(); i++) {
                        System.out.println((i + 1) + ". " + mem.get(i).toString());
                    }
                    System.out.println(line);
                    continue;
                }

                // add
                Task task = TaskFactory.parseTask(command);
                mem.add(task);
                System.out.println(line);
                System.out.println("Got it! Added:\n  " + task.toString() + "\n" +
                        "Now you have " + mem.size() + " tasks in this list.");
                System.out.println(line);
                storage.save(mem);

            } catch (BoydException e) {
                System.out.println(line);
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
                System.out.println(line);
            } catch (NumberFormatException e) {
                System.out.println(line);
                System.out.println("Error: You must enter a valid number after 'mark' or 'delete'.");
                System.out.println("Please try again.");
                System.out.println(line);
            }
        }
        bye();
    }
}
