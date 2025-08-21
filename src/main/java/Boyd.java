import Tasks.Task;

import java.util.Scanner;
import exceptions.*;

public class Boyd {
    private static Task[] mem = new Task[100];
    private static int itemCount = 0;
    private static String line = "____________________________________________________________";
    private static String chatbotName = "Boyd";

    public static void main(String[] args) {
        /*String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);*/
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
        String chatbotName = "Boyd";
        Scanner scanner = new Scanner(System.in);
        String command = "";
        while (true) {
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("bye")) {
                break;
            }
            try {
                if (command.startsWith("mark")) {
                    if (command.length() == 6) {
                        int itemNo = Integer.parseInt(command.substring(5)); //convert to integer
                        if (itemNo > itemCount) {
                            throw new BoydException("Invalid item number!");
                        }
                        if (itemCount == 0) {
                            throw new BoydException("No items yet!");
                        }
                        mem[itemNo - 1].markAsDone();
                        System.out.println(line);
                        String message = "Nice! I've marked this task as done:\n" + mem[itemNo - 1].getDescription();
                        System.out.println(message);
                        System.out.println(line);
                    } else {
                        throw new BoydException("Command should be of the format: \"mark <number>\"");
                    }
                    continue;
                }
                if (command.equalsIgnoreCase("list")) {
                    System.out.println(line);
                    if (itemCount == 0) {
                        throw new BoydException("You haven't added any items!");
                    }
                    for (int i = 0; i < itemCount; i++) {
                        System.out.println((i + 1) + ". " + mem[i].getDescription());
                    }
                    System.out.println(line);
                    continue;
                }
                Task task = TaskFactory.parseTask(command);
                mem[itemCount] = task;
                System.out.println(line);
                itemCount++;
                System.out.println("Got it! Added:\n  " + task.getDescription() + "\n" +
                        "Now you have " + itemCount + " tasks in this list.");
                System.out.println(line);
            } catch (BoydException e) {
                System.out.println(line);
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
                System.out.println(line);
            } catch (NumberFormatException e) {
                // handles invalid number parsing
                System.out.println(line);
                System.out.println("Error: You must enter a valid number after 'mark'.");
                System.out.println("Please try again.");
                System.out.println(line);
            }
            bye();
        }
    }
}