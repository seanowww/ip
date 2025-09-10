package boyd;

import java.util.ArrayList;

import boyd.exceptions.BoydException;
import boyd.utils.BoydResponse;
import boyd.utils.Parser;
import boyd.utils.Storage;
import boyd.utils.TaskList;

/**
 * Core facade for the Boyd application logic.
 * <p>
 * This class wires together the {@link Storage} (persistence),
 * {@link TaskList} (in-memory model), and {@link Parser} (command parsing).
 * It is UI-agnostic and can be used by both a console runner and a JavaFX GUI.
 * </p>
 */
public class Boyd {

    /** Default path for persisted data. */
    private static final String DEFAULT_SAVE_PATH = "./data/boyd.txt";

    /** Shared storage provider for loading/saving tasks. */
    private static final Storage STORAGE = new Storage();

    /** Chatbot display name used in greetings. */
    private static final String CHATBOT_NAME = "Boyd";

    /** In-memory task list backing the application. */
    private static TaskList tasks;

    /**
     * Constructs a {@code Boyd} instance using the default save path.
     * <p>
     * If loading fails (e.g., file missing or corrupted), the app starts with an
     * empty task list and continues to run.
     * </p>
     */
    public Boyd() {
        try {
            tasks = new TaskList(STORAGE.load(DEFAULT_SAVE_PATH), STORAGE);
        } catch (BoydException e) {
            tasks = new TaskList(new ArrayList<>(), STORAGE);
        }
    }

    /**
     * Constructs a {@code Boyd} instance and initializes the task list from the given path.
     * <p>
     * If loading fails (e.g., file missing or corrupted), the app starts with an
     * empty task list and continues to run.
     * </p>
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
     * Returns a standard greeting message for the chatbot.
     *
     * @return greeting text addressed to the user
     */
    public String getGreeting() {
        return "Hello! I'm " + CHATBOT_NAME + "!" + System.lineSeparator()
                + "What can I do for you?";
    }

    /**
     * Generates a response for the user's chat message by delegating to the parser.
     * <p>
     * Errors and exit conditions are encoded in the returned {@link BoydResponse}.
     * </p>
     *
     * @param input raw user input
     * @return a {@link BoydResponse} containing the message and status flags
     */
    public BoydResponse getResponse(String input) {
        assert tasks != null;
        return Parser.handle(input, tasks);
    }
}
