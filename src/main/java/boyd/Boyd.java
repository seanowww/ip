package boyd;

import java.util.ArrayList;
import java.util.Scanner;

import boyd.exceptions.BoydException;
import boyd.utils.Parser;
import boyd.utils.Storage;
import boyd.utils.TaskList;
import boyd.utils.Ui;

public class Boyd {

    private static final String DEFAULT_SAVE_PATH = "./data/boyd.txt";
    private static final Storage STORAGE = new Storage();

    private static TaskList tasks;

    private final Ui ui = new Ui();

    public static void main(String[] args) {
        new Boyd(DEFAULT_SAVE_PATH).run();
    }

    public Boyd(String filePath) {
        try {
            tasks = new TaskList(STORAGE.load(filePath), STORAGE);
        } catch (BoydException e) {
            tasks = new TaskList(new ArrayList<>(), STORAGE);
        }
    }

    public void run() {
        ui.greet();
        // try-with-resources ensures Scanner is closed (standard: manage resources)
        try (Scanner scanner = new Scanner(System.in)) {
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
        }
        ui.bye();
    }
}
