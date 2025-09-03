package boyd;

import java.util.ArrayList;
import java.util.Scanner;

import boyd.exceptions.BoydException;
import boyd.utils.DialogBox;
import boyd.utils.Parser;
import boyd.utils.Storage;
import boyd.utils.TaskList;
import boyd.utils.Ui;

/**
 * Entry point of the Boyd application.
 * <p>
 * This class wires together the {@link Storage} (persistence),
 * {@link TaskList} (in-memory model), {@link Parser} (command parsing),
 * and {@link Ui} (console I/O). It is responsible for bootstrapping the app,
 * running the read–eval–print loop, and shutting down cleanly.
 */
public class Boyd{

    private static final String DEFAULT_SAVE_PATH = "./data/boyd.txt";
    private static final Storage STORAGE = new Storage();

    private static TaskList tasks;

    private final Ui ui = new Ui();

    public Boyd() {
        try {
            tasks = new TaskList(STORAGE.load(DEFAULT_SAVE_PATH), STORAGE);
        } catch (BoydException e) {
            tasks = new TaskList(new ArrayList<>(), STORAGE);
        }
    }

    /**
     * Constructs the Boyd app and initializes the task list from disk.
     * <p>
     * If loading fails (e.g., file missing or corrupted), the app
     * starts with an empty task list and continues to run.
     *
     * @param filePath path to the save file (e.g., {@code ./data/boyd.txt})
     */


    public Boyd(String filePath) {
        try {
            tasks = new TaskList(STORAGE.load(filePath), STORAGE);
        } catch (BoydException e) {
            tasks = new TaskList(new ArrayList<>(), STORAGE);
        }
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        return "Boyd heard: " + input;
    }

    /**
     * Launches the application.
     *
     * @param args command-line arguments (ignored). The app loads from
     *             {@code ./data/boyd.txt} by default.
     */

    public static void main(String[] args) {
        new Boyd(DEFAULT_SAVE_PATH).run();
    }


    /**
     * Runs the main REPL loop:
     * <ol>
     *   <li>Greets the user via {@link Ui}.</li>
     *   <li>Reads a line from {@link java.util.Scanner}.</li>
     *   <li>Delegates command handling to {@link Parser#handle(String, TaskList)}.</li>
     *   <li>Exits when the parser indicates a {@code bye} command.</li>
     * </ol>
     * Any {@link BoydException} thrown by parsing/commands is caught and rendered
     * as a friendly error message via {@link Ui}.
     */
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
