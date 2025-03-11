package fr.univtln.bruno.samples.jfx.fxapp1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller for the Student Task Manager application.
 * Demonstrates basic JavaFX controller principles for beginners.
 */
public class Controller {

    // FXML injected UI components
    // These are automatically populated by the JavaFX runtime
    // when the FXML file is loaded
    // The @FXML annotation is used to mark fields that are injected
    @FXML
    private TextField taskInputField;
    
    @FXML
    private ListView<String> taskListView;
    
    @FXML
    private Button addButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Label statusLabel;
    
    // Data model for the list of tasks
    // This is a JavaFX observable list that will automatically update the UI
    // when items are added or removed
    private ObservableList<String> tasks;
    
    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the task list
        tasks = FXCollections.observableArrayList();
        taskListView.setItems(tasks);
        
        // Set up action handlers for buttons
        addButton.setOnAction(event -> addTask());
        clearButton.setOnAction(event -> clearTasks());
        
        // Add key event handler to the input field
        taskInputField.setOnAction(event -> addTask());
        
        // Add initial status message
        updateStatus("Ready to add tasks");
    }
    
    /**
     * Adds a new task to the list.
     */
    private void addTask() {
        String task = taskInputField.getText().trim();
        
        // Validate input
        if (task.isEmpty()) {
            updateStatus("Task cannot be empty");
            return;
        }
        
        // Add the task to the list
        tasks.add(task);
        
        // Clear the input field and update status
        taskInputField.clear();
        taskInputField.requestFocus();
        updateStatus("Added: " + task);
    }
    
    /**
     * Clears all tasks from the list.
     */
    private void clearTasks() {
        int count = tasks.size();
        
        if (count > 0) {
            tasks.clear();
            updateStatus("Cleared " + count + " task(s)");
        } else {
            updateStatus("No tasks to clear");
        }
    }
    
    /**
     * Updates the status message.
     * 
     * @param message the message to display
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}