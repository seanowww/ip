package boyd;

import boyd.utils.BoydResponse;
import boyd.utils.DialogBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Boyd boyd;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.jpeg"));
    private Image boydImage = new Image(this.getClass().getResourceAsStream("/images/DaBoyd.jpg"));

    /**
     * Initializes controller bindings after FXML is loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Boyd} core and posts an initial greeting.
     *
     * @param d the application core instance
     * @throws IllegalArgumentException if {@code d} is {@code null}
     */
    public void setBoyd(Boyd d) {
        if (d == null) {
            throw new IllegalArgumentException("Boyd instance must not be null");
        }
        boyd = d;
        // Proactively message the user first
        String greeting = boyd.getGreeting();
        dialogContainer.getChildren().add(
                DialogBox.getBoydDialog(greeting, boydImage)
        );
    }

    /**
     * Handles the Send action: reads user input, gets a response, and updates the dialog.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        BoydResponse res = boyd.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                res.isError()
                        ? DialogBox.getErrorDialog(res.message(), boydImage)
                        : DialogBox.getBoydDialog(res.message(), boydImage)
        );

        userInput.clear();
        if (res.isExit()) {
            javafx.application.Platform.exit();
        }
    }
}
