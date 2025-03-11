package fr.univtln.bruno.samples.jfx.fxapp2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Main application class for the JavaFX MVC sample application.
 * This class extends the JavaFX Application class and serves
 * as the entry point for the application lifecycle.
 */
@Log
public class App extends Application {

    // Constants
    private static final String FXML_FILE = "fxapp02.fxml";
    private static final String APP_TITLE = "Java FX Ex02";
    private static final int SCENE_WIDTH = 1280;
    private static final int SCENE_HEIGHT = 1024;

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
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
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(App.class.getResource(FXML_FILE));

            // Create the scene with the loaded layout
            BorderPane rootPane = loader.load();
            Scene scene = new Scene(rootPane, SCENE_WIDTH, SCENE_HEIGHT);

            // Optional: add CSS stylesheet if needed
            scene.getStylesheets().add(App.class.getResource("styles.css") .toExternalForm());

            // Set up and display the stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.show();

            log.info("Application UI loaded successfully");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to load FXML file: " + FXML_FILE, e);
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