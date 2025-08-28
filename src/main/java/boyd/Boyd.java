package boyd;

import java.util.ArrayList;
import java.util.Scanner;

import boyd.exceptions.BoydException;
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
public class Boyd {

    private static TaskList tasks;

    private final Ui ui = new Ui();

    private static final Storage storage = new Storage();

    /**
     * Launches the application.
     *
     * @param args command-line arguments (ignored). The app loads from
     *             {@code ./data/boyd.txt} by default.
     */
    public static void main(String[] args) {
        new Boyd("./data/boyd.txt").run();
    }

    /**
     * Constructs the Boyd app and initializes the task list from disk.
     * <p>
     * If loading fails (e.g., file missing or corrupted), the app
     * starts with an empty task list and continues to run.
     *
     * @param filepath path to the save file (e.g., {@code ./data/boyd.txt})
     */
    public Boyd(String filepath) {
        try {
            tasks = new TaskList(storage.load(filepath), storage);
        } catch (BoydException e) {
            // ui.showLoadingError(); // optional: surface a user-facing warning
            tasks = new TaskList(new ArrayList<>(), storage);
        }
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