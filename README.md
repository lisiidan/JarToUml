# UML Generator for .jar Files

This Java tool analyzes `.jar` files and generates UML class diagrams using Java Reflection. The output can be generated in either [PlantUML](https://plantuml.com/) or [yUML](https://yuml.me/) format, depending on the implementation selected in code.

## Features

- Analyzes `.jar` archives containing compiled Java classes.
- Uses reflection to extract:
  - Classes and interfaces
  - Fields (including type and visibility)
  - Methods (name, return type)
  - Relationships (extends, implements, associations)
- Generates output as a text-based UML diagram in one of the supported formats, based on given configuration.

## ⚙️ How to Use
Choose .jar file and see the plantUML diagram and generated text. (will implement yuml in the future)
For now, the configuration is hardcoded. (will be changed in the future)

### Requirements:
- Java 17+ recommended
- The `.jar` file to analyze must contain compiled `.class` files

### Running:

```bash
./gradlew run
