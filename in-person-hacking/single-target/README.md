# Declarative Java-based projects

This explores ideas with a simpler DSL to configure applications and libraries written with Java.

Instead of using the top-level dependencies block or extensions, all configuration is moved under a new `javaApplication` and `javaLibrary` block.

## Example

The example is generated with `gradle init`. It requires running Gradle with Java 17.

`./gradlew build` should compile and test the project.

### [App](javalibrary/app/build.gradle.kts)

Declares a few dependencies and the main class for a Java application.

### Libraries [utilities](javalibrary/utilities/build.gradle.kts) [list](javalibrary/list/build.gradle.kts)

Declares a few dependencies for a Java library.

### [Conventions](javalibrary/build-logic/src/main/kotlin/)

Sets up some common conventions for a Java application and library.