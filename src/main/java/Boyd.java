import Tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exceptions.*;
import java.io.FileWriter;   // Import the FileWriter class

import utils.Storage;
import utils.TaskFactory;
import utils.TaskList;
import utils.Ui;

public class Boyd {
    private static TaskList tasks;
    private static FileWriter myWriter;
    private final Ui ui = new Ui();
    private static final Storage storage = new Storage();

    public static void main(String[] args) {
        new Boyd("./data/boyd.txt").run();
    }

    public Boyd(String filepath) {
        try {
            tasks = new TaskList(storage.load(filepath), storage);
        } catch (BoydException e) {
            //ui.showLoadingError();
            tasks = new TaskList(new ArrayList<>(), storage);
        }
    }

    public void run() {
        ui.greet();
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
                String message = "Error: " + e.getMessage();
                ui.printErrorMessage(message);
            } catch (NumberFormatException e) {
                String message = "Error: You must enter a valid number after 'mark' or 'delete'.";
                ui.printErrorMessage(message);
            }
        }
        ui.bye();
    }
}
