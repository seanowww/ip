package boyd;

import java.util.ArrayList;

import boyd.exceptions.BoydException;
import boyd.utils.BoydResponse;
import boyd.utils.Parser;
import boyd.utils.Storage;
import boyd.utils.TaskList;

/**
 * Facade for the Boyd application logic.
 *
 * <p>Wires together {@link Storage} (persistence), {@link TaskList}
 * (in-memory model), and {@link Parser} (command parsing). The core is
 * UI-agnostic and can be used by a console runner or a JavaFX GUI.</p>
 */
public class Boyd {

    /** Default path for persisted data. */
    private static final String DEFAULT_SAVE_PATH = "./data/boyd.txt";

    /** Shared storage provider for loading/saving tasks. */
    private static final Storage STORAGE = new Storage();

    /** Chatbot display name used in greetings. */
    private static final String CHATBOT_NAME = "Boyd";

    /** In-memory task list backing the application. */
    private final TaskList tasks;

    /**
     * Constructs an instance using the default save path.
     *
     * <p>If loading fails (e.g., file missing or corrupted), the app starts with
     * an empty task list and continues to run.</p>
     */
    public Boyd() {
        TaskList loaded;
        try {
            loaded = new TaskList(STORAGE.load(DEFAULT_SAVE_PATH), STORAGE);
        } catch (BoydException e) {
            loaded = new TaskList(new ArrayList<>(), STORAGE);
        }
        assert loaded != null : "TaskList must not be null";
        this.tasks = loaded;
    }

    /**
     * Constructs an instance and initializes the task list from the given path.
     *
     * <p>If loading fails (e.g., file missing or corrupted), the app starts with
     * an empty task list and continues to run.</p>
     *
     * @param filePath path to the save file (e.g., {@code ./data/boyd.txt})
     * @throws IllegalArgumentException if {@code filePath} is {@code null} or blank
     */
    public Boyd(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("filePath must be non-null and non-blank");
        }
        TaskList loaded;
        try {
            loaded = new TaskList(STORAGE.load(filePath), STORAGE);
        } catch (BoydException e) {
            loaded = new TaskList(new ArrayList<>(), STORAGE);
        }
        assert loaded != null : "TaskList must not be null";
        this.tasks = loaded;
    }

    /**
     * Returns a standard greeting message for the chatbot.
     *
     * @return greeting text addressed to the user
     */
    public String getGreeting() {
        assert CHATBOT_NAME != null && !CHATBOT_NAME.isBlank()
                : "CHATBOT_NAME must be configured";
        return "Hello! I'm " + CHATBOT_NAME + "!" + System.lineSeparator()
                + "What can I do for you?";
    }

    /**
     * Generates a response for the user's chat message by delegating to the parser.
     *
     * @param input raw user input
     * @return a {@link BoydResponse} containing the message and status flags
     * @throws IllegalArgumentException if {@code input} is {@code null}
     */
    public BoydResponse getResponse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("input must be non-null");
        }
        assert this.tasks != null : "tasks must be initialized";
        return Parser.handle(input, this.tasks);
    }
}
