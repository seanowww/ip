package boyd;

import java.util.ArrayList;
import java.util.Scanner;

import boyd.exceptions.BoydException;

import boyd.utils.Parser;
import boyd.utils.Storage;
import boyd.utils.TaskList;
import boyd.utils.Ui;

public class Boyd {
    private static TaskList tasks;
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
