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

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Boyd instance */
    public void setBoyd(Boyd d) {
        boyd = d;
        //Proactively message the user first
        var greeting = boyd.getGreeting();
        dialogContainer.getChildren().add(
                DialogBox.getBoydDialog(greeting, boydImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Boyd's reply and then appends them to
     * the dialog container. Clears the user input after processing.
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
