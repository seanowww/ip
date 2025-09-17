package boyd;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Entrypoint that delegates to {@link Main} to start the JavaFX application.
     *
     * @param args command-line arguments passed by the runtime
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
