# Declarative Kotlin Multiplatform projects

This explores how to use a simpler, more static DSL to configure applications and libraries written for KMP.

All configuration is moved under a new `androidApplication` block.
This example only demonstrates an application, but the same ideas apply to libraries.

## [Example](testbed/build.gradle.kts)

The `testbed` subproject uses the new DSL.

The new DSL demonstrates several ideas:
- `platforms` is a list property in the `kmpApplication` block that specifies the multiplatform targets built by this project.
- Common top-level properties that apply to all targets (like the `languageVersion` of Kotlin used, and the whether or not to `publishSources` for the project) are backed by `Property` instances and available directly in the `kmpApplication` block.
There is code in the `Greeter.kt` file in `commonMain` that requires Kotlin 1.9 to compile.
- `dependencies {}` in the `kmpApplication` block allows for declaring common dependencies usable by all targets, in the same way the `commonMain` KMP sourceSet would. 
- `targets {}` allows for configuring specific KMP platform variants.
Static extension methods are provided for each platform (only `jvm` and `js` implemented so far) to allow for configuring platform-specific dependencies with IDE type assistance.
- Publishing the project is configured to use a local Maven repo under `build/repo` using the `publishing` block provided by the `maven-publish` plugin, to demonstrate the results of publishing the project with and without sources.

## Goals

- This DSL should look and feel like a Gradle DSL, be similar to Java and Android builds, and be navigable by Gradle developers new to KMP.
- The `targets` block should be a container holding each platform, not merely a static extension.
- Extraneous blocks should be simplified or removed and their contents pulled up a level.

### Running the project

All tasks below should be executed from the `testbed` directory.
Running the project requires running Gradle with Java 17.

To run (on the JVM):
```shell
  ./gradlew jvmRun -DmainClass=GreeterKt --quiet
```

To publish the project to `build/repo` (Can confirm presence of sources jars in the directory):
```shell
  ./gradlew publishAllPublicationsToTestRepository
```

To run all tests (JVM and Browser-based JS tests):
```shell
  ./gradlew allTests
```
