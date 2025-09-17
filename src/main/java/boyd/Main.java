package boyd;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX launcher for the Boyd GUI using FXML.
 */
public class Main extends Application {

    /** Classpath location of the main FXML layout. */
    private static final String MAIN_FXML_PATH = "/view/MainWindow.fxml";

    /** Core application instance injected into the controller. */
    private final Boyd boyd = new Boyd();

    /**
     * Starts the JavaFX application and initializes the primary stage.
     *
     * @param stage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        try {
            URL fxmlUrl = Main.class.getResource(MAIN_FXML_PATH);
            assert fxmlUrl != null : "FXML resource not found: " + MAIN_FXML_PATH;

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            AnchorPane root = fxmlLoader.load();

            // Inject core object into controller
            MainWindow controller = fxmlLoader.getController();
            assert controller != null : "MainWindow controller should be present";
            controller.setBoyd(boyd);

            Scene scene = new Scene(root);
            // Attach stylesheet programmatically to avoid FXML URL resolution issues.
            scene.getStylesheets().add(Main.class.getResource("/view/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Boyd");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
