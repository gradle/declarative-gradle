# declarative-samples-java-app
A sample Java application written in the Declarative Gradle DSL, using the prototype Declarative Gradle `javaApplication` Software Type defined in the `org.gradle.experimental.jvm-ecosystem` ecosystem plugin.

## Building and Running

This sample shows the definition of a multiproject Java application implemented using Java 17 source code.
The project is the result of converting the project produced by the `gradle init` command in Gradle 8.9.

To build and test the application without running, use:

```shell
> ./gradlew build
```

To run the application, use:

```shell
> ./gradlew run
```
