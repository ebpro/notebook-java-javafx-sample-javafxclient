# JavaFX Sample Application

## Overview

This project contains two minimal JavaFX applications demonstrating the use of JavaFX with a modern UI design.

## Applications

- `fr.univtln.bruno.samples.jfx.fxapp1.App`
- `fr.univtln.bruno.samples.jfx.fxapp2.App`

## Prerequisites

- JDK 21 or higher
- 
## Building and Running

### Using Maven

To package and run the application using the JavaFX Maven plugin:

```bash
./mvnw package
./mvnw javafx:run
```

### Using JLink

To package the application with the necessary dependencies and JRE, even with non-modular dependencies, 
use the dedicated profile to package the application with the necessary dependencies and JRE:

```bash
./mvnw -P jlink package
./target/image/bin/myapp
```

## Project Structure

- `src/main/java`: Contains the Java source files.
- `src/main/resources`: Contains the FXML and CSS files.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
