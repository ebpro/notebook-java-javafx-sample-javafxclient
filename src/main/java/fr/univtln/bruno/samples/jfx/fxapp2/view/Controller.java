package fr.univtln.bruno.samples.jfx.fxapp2.view;

import fr.univtln.bruno.samples.jfx.fxapp2.model.PersonRepository;
import fr.univtln.bruno.samples.jfx.fxapp2.model.Page;
import fr.univtln.bruno.samples.jfx.fxapp2.model.Person;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.extern.java.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main controller class for the MVC example application.
 * Handles the presentation logic and binds the UI to the model.
 */
@Log
public class Controller {
    // Constants
    private static final int PAGE_SIZE = 10;
    private static final int MAX_PAGE_INDICATORS = 10;
    public static final String ADDRESS = "Address: ";
    public static final String NAME = "Name: ";
    public static final String UUID = "UUID: ";

    // Thread pool for background tasks
    private final ExecutorService executorService = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    // Model access layer
    // Simulated repository for Person data
    private final PersonRepository personRepository = PersonRepository.newInstance();

    // Programmatically created UI components
    private final TableView<Person> table = createTable();

    // FXML injected components
    @FXML
    private Pagination paginator;

    @FXML
    private ListView<Person> searchList;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label detailName;
    @FXML
    private Label detailAddress;
    @FXML
    private Label detailUUID;

    private SimpleBooleanProperty searchInProgress = new SimpleBooleanProperty(false);
    /**
     * Initializes the controller.
     * This method is automatically called after the FXML elements have been loaded.
     */
    @FXML
    private void initialize() {
        log.info("Initializing controller...");

        // Configure pagination control
        configurePagination();

        // Configure search functionality
        configureSearch();

        // Configure list cell appearance using a custom cell factory
        configureSearchListCellFactory();

        // Configure table selection behavior
        configureTableSelectionHandling();

        // Add listeners for selection changes
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));
        searchList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));

        // Initialize the status label
        updateStatus("Ready");

        log.info("Controller initialization complete");
    }

    /**
     * Sets up the pagination control with proper configuration.
     */
    private void configurePagination() {
        paginator.setMaxPageIndicatorCount(MAX_PAGE_INDICATORS);
        paginator.setPageFactory(this::createPage);
        createPage(0); // Trigger initial page load
    }

    /**
     * Shows the details of a person in the detail pane.
     *
     * @param person the person to show details for
     */
    private void showPersonDetails(Person person) {
        if (person != null) {
            detailName.setText(NAME + person.getName());
            detailAddress.setText(ADDRESS + person.getAddress());
            detailUUID.setText(UUID + person.getUuid());
        } else {
            detailName.setText(NAME);
            detailAddress.setText(ADDRESS);
            detailUUID.setText(UUID);
        }
    }

    /**
     * Configures the search functionality including key listeners and button action.
     */
    private void configureSearch() {
        // Configure search button action
        searchButton.setOnAction(event -> performSearch());

        // Add key listener to search field (search on Enter key)
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                performSearch();
            }
        });

        // Bind the button's disabled state to: (empty text field OR search in progress)
        searchButton.disableProperty().bind(
            searchField.textProperty().isEmpty().or(searchInProgress)
        );

        // Configure selection handling in search results list
        searchList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                highlightPersonInTable(newVal);
            }
        });
    }

    /**
     * Configures the cell factory for the search list to display person names.
     * Enhances the display with address information in a tooltip.
     */
    private void configureSearchListCellFactory() {
        searchList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);

                if (empty || person == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(person.getName());
                    Tooltip tooltip = new Tooltip(ADDRESS+ person.getAddress());
                    setTooltip(tooltip);
                }
            }
        });
    }

    /**
     * Configures table selection handling to show details when a row is selected.
     */
    private void configureTableSelectionHandling() {
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateStatus("Selected: " + newVal.getName());
            }
        });
    }

    /**
     * Creates a TableView to display Person data with sortable columns.
     * The table is dynamically configured with columns for name, address, and UUID.
     * @return a configured TableView for Person objects
     */
    @SuppressWarnings("unchecked")
    private TableView<Person> createTable() {
        // Create table instance
        TableView<Person> tableView = new TableView<>();

        // Set table properties
        tableView.setPlaceholder(new Label("No data available"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Create and configure name column
        // The Column is bound to the name property of the Person object
        TableColumn<Person, String> nameColumn = new TableColumn<>("NAME");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        // Create and configure address column
        TableColumn<Person, String> addressColumn = new TableColumn<>("ADDRESS");
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));

        // Add columns to table
        tableView.getColumns().addAll(nameColumn, addressColumn);

        return tableView;
    }

    /**
     * Creates a page for the paginator based on the page number.
     *
     * @param  pageIndex Node createPage(int pageIndex) { the page number to create
     * @return a Node containing the table view for the requested page
     */
    private Node createPage(int pageIndex) {
        try {
            // Fetch data for the requested page
            Page<Person> page = personRepository.findAll(PAGE_SIZE, pageIndex + 1);

            // Update the table with the page data
            table.setItems(FXCollections.observableArrayList(page.content()));

            // Update pagination control with the total number of pages
            int pageCount = (int) Math.ceil((double) page.dataSize() / page.pageSize());
            paginator.setPageCount(pageCount);

            // Update status with page information
            updateStatus(String.format("Page %d of %d (%d total records)",
                    pageIndex + 1, pageCount, page.dataSize()));

            // Return the table wrapped in a border pane
            return new BorderPane(table);
        } catch (Exception e) {
            log.severe("Error creating page: " + e.getMessage());
            Label errorLabel = new Label("Error loading data: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            return errorLabel;
        }
    }

    /**
     * Performs a search operation with appropriate UI feedback.
     */
    private void performSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            updateStatus("Please enter search text");
            return;
        }

        updateStatus("Searching for: " + searchText + "...");
        searchInProgress.set(true);  // Set search in progress to true

        executeSearch(searchText);
    }

    /**
     * Executes the search in a background thread.
     *
     * @param searchText the text to search for
     */
    private void executeSearch(String searchText) {
        Task<List<Person>> task = new Task<>() {
            @Override
            protected List<Person> call() {
                updateMessage("Searching...");
                return PersonRepository.newInstance().search(searchText).content();
            }
        };

        task.setOnSucceeded(event -> {
            List<Person> results = task.getValue();
            searchList.setItems(FXCollections.observableList(results));
            updateStatus("Found " + results.size() + " results for \"" + searchText + "\"");
            searchInProgress.set(false);  // Set search in progress to false
        });

        task.setOnFailed(event -> {
            log.severe("Search failed: " + task.getException().getMessage());
            updateStatus("Search failed: " + task.getException().getMessage());
            searchInProgress.set(false);  // Set search in progress to false
        });

        executorService.submit(task);
    }

    /**
     * Updates the status label text.
     *
     * @param message the message to display
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            Platform.runLater(() -> statusLabel.setText(message));
        }
    }

    /**
     * Highlights a person in the table view.
     *
     * @param person the person to highlight
     */
    private void highlightPersonInTable(Person person) {
        // Find which page contains this person
        int itemsPerPage = PAGE_SIZE;
        List<Person> allPersons = personRepository.findAll(1000, 1).content(); // Get all persons (not efficient but works for demo)
        int index = allPersons.indexOf(person);

        if (index >= 0) {
            int pageIndex = index / itemsPerPage;

            // Navigate to the correct page
            paginator.setCurrentPageIndex(pageIndex);

            // Select the person in the table
            Platform.runLater(() -> {
                table.getSelectionModel().select(person);
                table.scrollTo(person);
            });
        }
    }

    /**
     * Cleanup resources when the controller is no longer needed.
     * This should be called when the application is closing.
     */
    public void cleanup() {
        executorService.shutdown();
        log.info("Controller resources cleaned up");
    }
}