package boyd.utils;

import java.io.IOException;
import java.util.Collections;

import boyd.MainWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A dialog box consisting of an {@link ImageView} to represent the speaker's face
 * and a {@link Label} containing the speaker's text.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with text and avatar image.
     *
     * @param text  message content (non-null)
     * @param image avatar image (non-null)
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // For a small app, printing stack trace is acceptable.
            // In production, prefer a logger.
            e.printStackTrace();
        }

        assert text != null : "Dialog text must not be null";
        assert image != null : "Dialog image must not be null";

        this.dialog.setText(text);
        this.displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box such that the image is on the left and the text on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box representing a user message.
     *
     * @param text message content
     * @param image image representing the user
     * @return a {@code DialogBox} for the user
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        // Style: user bubble (default assigned in FXML: bubble user)
        return dialogBox;
    }

    /**
     * Returns a dialog box representing a Boyd message.
     *
     * @param text message content
     * @param image image representing Boyd
     * @return a {@code DialogBox} for Boyd
     */
    public static DialogBox getBoydDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        // Style: boyd bubble (programmatically swap class)
        dialogBox.dialog.getStyleClass().remove("user");
        dialogBox.dialog.getStyleClass().add("boyd");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Returns a dialog box representing an error message from Boyd.
     *
     * @param text error message content
     * @param image image representing Boyd
     * @return a {@code DialogBox} for an error response
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        String errorMessage = "Error: " + text + System.lineSeparator()
                + "Please try again.";
        DialogBox dialogBox = new DialogBox(errorMessage, image);
        // Style: error bubble (programmatically swap class)
        dialogBox.dialog.getStyleClass().remove("user");
        dialogBox.dialog.getStyleClass().add("error");
        dialogBox.flip();
        return dialogBox;
    }
}
