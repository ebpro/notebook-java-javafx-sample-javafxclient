package fr.univtln.bruno.samples.jfx.fxapp1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

/**
 * Main application class for the JavaFX sample application.
 * This class extends the JavaFX Application class and serves
 * as the entry point for the JavaFX application lifecycle.
 */
@Slf4j
public class App extends Application {

    // Constants
    private static final String FXML_FILE = "fxapp01.fxml";
    private static final String APP_TITLE = "Java FX Ex01";

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        // This method is non-blocking and returns immediately
        // The start() method is called by the JavaFX runtime
        // to initialize the application UI
        // The stop() method is called when the application exits
        // (e.g., when the user closes the window)
        launch(args);
    }

    /**
     * Initializes the application. Called by the JavaFX runtime after
     * the Application object is created but before start().
     *
     * @throws Exception if initialization fails
     */
    @Override
    public void init() throws Exception {
        log.info("Initializing application...");
        super.init();
        // Add any pre-UI initialization code here
    }

    /**
     * The main entry point for the JavaFX application.
     * This method loads the FXML UI definition and sets up the primary stage.
     *
     * @param primaryStage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(App.class.getResource(FXML_FILE));

            // Create the scene with the loaded layout
            BorderPane rootPane = loader.load();
            Scene scene = new Scene(rootPane);

            // Optional: add CSS stylesheet if needed
            scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());

            // Set up and display the stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.show();

            log.info("Application UI loaded successfully");
        } catch (IOException e) {
            log.error("Failed to load FXML file: " + FXML_FILE, e);
            throw e;
        }
    }

    /**
     * Called by the JavaFX runtime when the application should stop.
     * Use this method to clean up resources and perform shutdown tasks.
     *
     * @throws Exception if cleanup fails
     */
    @Override
    public void stop() throws Exception {
        log.info("Application shutting down...");
        // Add cleanup code here (e.g., closing database connections)
        super.stop();
    }
}