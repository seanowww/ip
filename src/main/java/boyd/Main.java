package boyd;

import java.util.ArrayList;
import java.util.Scanner;

import boyd.exceptions.BoydException;
import boyd.utils.DialogBox;
import boyd.utils.Parser;
import boyd.utils.Storage;
import boyd.utils.TaskList;
import boyd.utils.Ui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Region;

/**
 * Entry point of the Boyd application.
 * <p>
 * This class wires together the {@link Storage} (persistence),
 * {@link TaskList} (in-memory model), {@link Parser} (command parsing),
 * and {@link Ui} (console I/O). It is responsible for bootstrapping the app,
 * running the read–eval–print loop, and shutting down cleanly.
 */
public class Main extends Application{

    private static final String DEFAULT_SAVE_PATH = "./data/boyd.txt";
    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.jpeg"));
    private Image boydImage = new Image(this.getClass().getResourceAsStream("/images/DaBoyd.jpg"));
    private static final Storage STORAGE = new Storage();
    private Boyd boyd = new Boyd();

    private static TaskList tasks;
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private final Ui ui = new Ui();

    public Main() {
        this(DEFAULT_SAVE_PATH);
    }

    @Override
    public void start(Stage stage) {
        //Setting up required components
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setTitle("Boyd");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        stage.setScene(scene);
        stage.show();

        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });
        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Creates a dialog box containing user input, and appends it to
     * the dialog container. Clears the user input after processing.
     */
    private void handleUserInput() {
        String userText = userInput.getText();
        String dukeText = boyd.getResponse(userInput.getText());
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getDukeDialog(dukeText, boydImage)
        );
        userInput.clear();
    }

    /**
     * Constructs the Boyd app and initializes the task list from disk.
     * <p>
     * If loading fails (e.g., file missing or corrupted), the app
     * starts with an empty task list and continues to run.
     *
     * @param filePath path to the save file (e.g., {@code ./data/boyd.txt})
     */
    public Main(String filePath) {
        try {
            tasks = new TaskList(STORAGE.load(filePath), STORAGE);
        } catch (BoydException e) {
            tasks = new TaskList(new ArrayList<>(), STORAGE);
        }
    }

    /**
     * Launches the application.
     *
     * @param args command-line arguments (ignored). The app loads from
     *             {@code ./data/boyd.txt} by default.
     */
    public static void main(String[] args) {
        new Boyd(DEFAULT_SAVE_PATH).run();
    }

}
