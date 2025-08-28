import Tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exceptions.*;
import java.io.FileWriter;   // Import the FileWriter class

import utils.Parser;
import utils.Storage;
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
        while (true) {
            String line = scanner.nextLine();
            try {
                boolean shouldExit = Parser.handle(line, tasks);
                if (shouldExit) {
                    break;
                }
            } catch (BoydException e) {
                ui.printErrorMessage(e.getMessage());
            }
        }
        ui.bye();
    }
}
