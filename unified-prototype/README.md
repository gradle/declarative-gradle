# DEclarative Gradle - Unified Plugin Prototypes

This directory contains prototypes of plugins for JVM, Android, and KMP projects built using "unified" plugins that all utilize a similar model and are implemented using the Declarative DSL.

Currently, these different ecosystems still apply distinct plugins, but those plugins all share a common `plugin-common` dependency, which will gradually grow to contain more functionality.

So far, the only example modified to use the Declarative DSL is the Android example.

## JVM

Not yet updated for Declarative DSL.

## KMP

Not yet updated for Declarative DSL.

## Android

The sample Android project lives in the `testbed-android` directory and has been updated to use the Declarative Gradle DSL. 
The `unified-prototype/plugin-android` plugin demonstrates creating extensions using the Declarative DSL, and loading the data from those extensions into the Android project used by AGP.

The sample project demonstrates setting properties, using a common dependencies block, and adding dependencies to specific Android targets.

### Implementation Notes

A Guava dependency is exposed as part of the library's API.
The OKHttp dep is used by the release variant's implementation, and is not necessary for the debug variant.

The `StandaloneAndroidLibraryPlugin` plugin works by using `project.afterEvaluate` to load data from the Declarative DSL extensions into AGP's model.

### Limitations

The Android example is currently limited, and does not support many use cases such as adding tests or running the `publish` task.
It requires JDK >= 17 to build.

### Running 
From the `testbed-android` directory, run `build` using the Gradle wrapper in the parent directory:

```shell
cd testbed-android
../gradlew build
```

to build debug and release `aar`s for the example Android project build in the `testbed-android/biuld/outputs/aar` directory.

You can also build the `Debug` and `Release` variants separately:

```shell 
cd testbed-android
../gradlew :testbed-android:assembleDebug
```

```shell 
cd testbed-android
../gradlew :testbed-android:assembleRelease
```