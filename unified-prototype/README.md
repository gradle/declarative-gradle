# Declarative Gradle - Unified Plugin Prototypes

This directory contains prototypes of plugins for JVM, Android, and KMP projects built using "unified" plugins that all utilize a similar model and are implemented using the Declarative DSL.

Currently, these different ecosystems still apply distinct plugins, but those plugins all share a common `plugin-common` dependency, which will gradually grow to contain more functionality.

So far, the only example modified to use the Declarative DSL is the Android example.

## JVM

Not yet updated for Declarative DSL.

## KMP

The sample Kotlin Multiplatform project lives in the `testbed-kmp` directory and has been updated to use the Declarative Gradle DSL.
The `unified-prototype/plugin-kmp` plugin demonstrates creating extensions using the Declarative DSL, and loading the data from those extensions into the KMP project used by KGP.

The sample project demonstrates setting properties, using a common dependencies block, and adding dependencies to specific targets.

### Implementation Notes

An Apache Commons dependency is used by the JVM code.
A SQLDelight dependency is used by the JS code.
The kotlinx.datetime multiplatform dep is used by common code.

The `StandaloneKmpLibraryPlugin` plugin works by using `project.afterEvaluate` to load data from the Declarative DSL extensions into KGP's model.

### Limitations

The KMP example is currently limited, and does not support any targets other than `js` and `jvm`.

### Running
From the `testbed-kmp` directory, run `build` using the Gradle wrapper in the parent directory:

```shell
cd testbed-kmp
../gradlew build
```

to build the JS, JVM and KMP metadata jars for the example KMP project build in the `testbed-kmp/build/libs` directory.

You can also build the JS and JVM libs separately:

```shell 
cd testbed-kmp
../gradlew :testbed-kmp:jsJar
```

```shell 
cd testbed-kmp
../gradlew :testbed-kmp:jvmJar
```

## Android

Sample Android projects live in the `testbed-android-library` and `testbed-android-application` directories. They have been updated to use the Declarative Gradle DSL. 
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