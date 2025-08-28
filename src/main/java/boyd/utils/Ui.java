package boyd.utils;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final String CHATBOT_NAME = "boyd.Boyd";

    public Ui() {
    }

    public void printLine() {
        System.out.println(LINE);
    }

    public void greet() {
        printLine();
        System.out.println("Hello! I'm " + CHATBOT_NAME + "!\nWhat can I do for you?");
        printLine();
    }

    public void bye() {
        printLine();
        System.out.println("Bye. Hope to see you again soon!");
        printLine();
    }

    public void printErrorMessage(String message) {
        printLine();
        System.out.println("Error: " + message);
        System.out.println("Please try again.");
        printLine();
    }

    public void printWithLines(String message) {
        printLine();
        System.out.println(message);
        printLine();
    }
}
