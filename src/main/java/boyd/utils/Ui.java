package boyd.utils;

/**
 * Console UI helper for the Boyd chatbot.
 * <p>
 * Provides convenience methods to print a consistent separator line,
 * standard greetings/farewells, and framed messages to {@link System#out}.
 * </p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * Ui ui = new Ui();
 * ui.greet();
 * ui.printWithLines("Type 'help' to see commands.");
 * ui.bye();
 * }</pre>
 */
public class Ui {
    /** Horizontal separator used to frame console output blocks. */
    private static final String LINE = "____________________________________________________________";

    private final String CHATBOT_NAME = "Boyd";

    /** Creates a new {@code Ui}. */
    public Ui() {
    }

    /**
     * Prints the horizontal separator line followed by a newline.
     */
    public void printLine() {
        System.out.println(LINE);
    }

    /**
     * Prints a standard greeting that includes the chatbot name and a prompt,
     * framed above and below by the separator line.
     */
    public void greet() {
        printLine();
        System.out.println("Hello! I'm " + CHATBOT_NAME + "!\nWhat can I do for you?");
        printLine();
    }

    /**
     * Prints a standard farewell message framed by the separator line.
     */
    public void bye() {
        printLine();
        System.out.println("Bye. Hope to see you again soon!");
        printLine();
    }

    /**
     * Prints a framed error message with a follow-up instruction line.
     *
     * @param message the error detail to display
     */
    public void printErrorMessage(String message) {
        printLine();
        System.out.println("Error: " + message);
        System.out.println("Please try again.");
        printLine();
    }

    /**
     * Prints an arbitrary message framed by the separator line.
     *
     * @param message the message to display
     */
    public void printWithLines(String message) {
        printLine();
        System.out.println(message);
        printLine();
    }
}
