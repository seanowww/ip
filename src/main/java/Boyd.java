import Tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exceptions.*;
import java.io.FileWriter;   // Import the FileWriter class

import utils.Storage;
import utils.TaskFactory;
import utils.TaskList;

public class Boyd {
    private static TaskList tasks;
    private static String line = "____________________________________________________________";
    private static String chatbotName = "Boyd";
    private static FileWriter myWriter;
    private static final Storage storage = new Storage();

    public static void main(String[] args) {
        //new Boyd("./data/boyd.txt").run();
        String filepath = "./data/boyd.txt";
        try {
            tasks = new TaskList(storage.load(filepath), storage);
            greet();
            echo();
        } catch (BoydException e) {
            //ui.showLoadingError();
            tasks = new TaskList(new ArrayList<>(), storage);
        }
    }

    /*public Boyd(String filepath) {
        //ui = new Ui();

    }

    public void run() {

    }*/

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
                    tasks.mark(itemNo);
                    continue;
                }

                // delete <number>
                if (command.startsWith("delete")) {
                    if (!command.startsWith("delete ")) {
                        throw new BoydException("Command should be of the format: \"delete <number>\"");
                    }
                    int itemNo = Integer.parseInt(command.substring(7).trim());
                    tasks.remove(itemNo);
                    continue;
                }

                // list
                if (command.equalsIgnoreCase("list")) {
                    tasks.list();
                    continue;
                }

                // add
                Task task = TaskFactory.parseTask(command);
                tasks.add(task);

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
