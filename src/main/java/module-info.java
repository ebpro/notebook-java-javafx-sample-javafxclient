/**
 * Module definition for JavaFX applications.
 * This module contains sample JavaFX applications that demonstrate
 * various concepts and patterns in JavaFX development.
 */
module fr.univtln.bruno.samples.jfx {
    // Required JavaFX modules
    requires javafx.controls;    // For JavaFX UI controls
    requires javafx.fxml;        // For FXML-based UI definitions

    // Third-party dependencies
    requires lombok;             // For reducing boilerplate code
    requires javafaker;          // For generating sample data
    requires org.slf4j;          // For logging with SLF4J

    // Java standard modules
    requires java.logging;       // For application logging
    requires java.sql;           // For database connectivity

    // Module exports and opens for JavaFX app 1 (simple example)
    exports fr.univtln.bruno.samples.jfx.fxapp1;               // Export APIs
    opens fr.univtln.bruno.samples.jfx.fxapp1 to javafx.fxml;  // Allow FXML reflection access

    // Module exports and opens for JavaFX app 2 (MVC architecture)
    exports fr.univtln.bruno.samples.jfx.fxapp2;               // Export main package
    opens fr.univtln.bruno.samples.jfx.fxapp2 to javafx.fxml;  // Allow FXML reflection access

    // View layer exports and opens
    exports fr.univtln.bruno.samples.jfx.fxapp2.view;          // Export view components
    opens fr.univtln.bruno.samples.jfx.fxapp2.view to javafx.fxml;  // Allow FXML reflection access

    // Model layer exports and opens
    exports fr.univtln.bruno.samples.jfx.fxapp2.model;         // Export model classes
    opens fr.univtln.bruno.samples.jfx.fxapp2.model to javafx.fxml;  // Allow FXML reflection access
}