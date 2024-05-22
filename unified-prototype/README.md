# Declarative Gradle - Unified Plugin Prototypes

This directory contains prototypes of plugins for JVM, Android, KMP and Swift projects built using "unified" plugins that all utilize a similar model and are implemented using the Declarative DSL.

Currently, these different ecosystems still apply distinct plugins, but those plugins all share a common `plugin-common` dependency, which will gradually grow to contain more functionality.

## Java

Sample Java projects live in the `testbed-java-library` and `testbed-java-application` directories.

These samples show the definition of a simple Java application and library that target a single version of Java.

To run the application, use:

```shell
> ./gradlew testbed-java-application:runAll
```

## JVM

Sample JVM projects live in the `testbed-jvm-library` and `testbed-jvm-application` directories.

These samples show the definition of a simple Java application and library that are implemented using a mix of Java 11 and Java 17 source code.

To run the application, use:

```shell
> ./gradlew testbed-jvm-application:runAll
```

## Kotlin JVM

Sample Kotlin JVM projects live in the `testbed-kotlin-jvm-library` and `testbed-kotlin-jvm-application` directories.

These samples show the definition of a simple Kotlin JVM application and library that target a single version of Kotlin and Java.

To run the application, use:

```shell
> ./gradlew testbed-kotlin-jvm-application:runAll
```

## Kotlin Multiplatform

The sample Kotlin Multiplatform projects live in the `testbed-kotlin-library` and `testbed-kotlin-application` directories.

The `unified-prototype/plugin-kmp` plugin demonstrates creating extensions using the Declarative DSL, and loading the data from those extensions into the KMP project used by KGP.

The sample project demonstrates setting properties, using a common dependencies block, and adding dependencies to specific targets.

### Limitations

The KMP example is currently limited, and does not support any targets other than `nodeJs`, `jvm` and `macOsArm64`.

### Running

To run the application, use:

```shell
../gradlew testbed-kotlin-application:runAll
```

### Building

From the `testbed-kotlin-application` directory, run `build` using the Gradle wrapper in the parent directory:

```shell
cd testbed-kotlin-application
../gradlew build
```

to build the JS, JVM and KMP metadata jars for the example KMP project build in the `testbed-kotlin-application/build/libs` directory.

You can also build the JS and JVM libs separately:

```shell 
cd testbed-kotlin-application
../gradlew jsJar
```

```shell 
cd testbed-kotlin-application
../gradlew jvmJar
```

## Android

Sample Android projects live in the `testbed-android-library` and `testbed-android-application` directories.
The `unified-prototype/plugin-android` plugin demonstrates creating extensions using the Declarative DSL, and loading the data from those extensions into the Android project used by AGP.

The sample project demonstrates setting properties, using a common dependencies block, and adding dependencies to specific Android targets.

### Implementation Notes

Guava is declared as an implementation dependency.
The OKHttp dep is used by the release build type's implementation, and is not necessary for the debug variant.

Both plugin implementations work by using `project.afterEvaluate` to load data from the Declarative DSL extensions into AGP's model.

### Limitations

The Android example is currently limited, and does not support many use cases such as adding tests or running the `publish` task.
It requires JDK >= 17 to build.

### Running 
From the `testbed-android-library` or `testbed-android-application` directory, run `build` using the Gradle wrapper in the parent directory:

```shell
cd testbed-android-application
../gradlew build
```

to build debug and release `aar`s for the example Android project build in the `testbed-android/build/outputs/aar` directory.

You can also build the `Debug` and `Release` variants separately:

```shell 
gradlew :testbed-android-application:assembleDebug
```

```shell 
gradlew :testbed-android-application:assembleRelease
```

## Swift

The sample Swift projects live in the `testbed-swift-library` and `testbed-swift-application` directories.

To run the application, use:

```shell
../gradlew testbed-swift-application:runAll
```
