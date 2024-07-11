
# Declarative Gradle for Android

Support for Android is our main priority in the first alpha releases.
We target the classic Android applications and libraries,
and also Kotlin Multiplatform projects.

!!! info
    _Declarative Gradle_ is an experimental project.
    Currently, no compatibility is guaranteed, and there is no commitment to the DSL syntax
    and available features.
    More information will be released soon.

## Key Features

The Declarative Gradle DSL code is available at <https://github.com/gradle/declarative-gradle/tree/main/unified-prototype>.
The Declarative Gradle DSL plugin for Android is available at: 
<https://plugins.gradle.org/plugin/org.gradle.experimental.android-library>.
You can find a list of all available plugins [here](https://plugins.gradle.org/search?term=declarative-gradle).

Common build files written using the Groovy DSL or Kotlin DSL with the name `build.gradle` and `build.gradle.kts` respectively will be replaced with `build.gradle.dcl`.

`.dcl` files are written in Kotlin in a fully declarative way. 


The restricted DSL allows a limited set of constructs. Generic control flow and calls to arbitrary methods are not allowed.
For Android development, we provide the following software types:

- `androidApplication` - Android Application
- `androidLibrary` - Android Library
- `jvmLibrary` - Generic library for a JVM-compatible virtual machine
- `kmpLibrary` - A kotlin multi-platform library

The `androidLibrary` software type exposes [several configuration options](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/unified-plugin/plugin-android/src/main/java/org/gradle/api/experimental/android/library/AndroidLibrary.java) and dependencies.
As you can see, there is no imperative logic here:

```kotlin
androidLibrary {
    namespace = ""
    dependencies {}
    buildTypes {}
}
```

The `androidApplication` software type example can be found [here](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/unified-plugin/plugin-jvm/src/main/java/org/gradle/api/experimental/java/JavaApplication.java).

## Setting Up

### Environment

- Make sure to use [JDK 17](https://www.oracle.com/fr/java/technologies/downloads/#java17) and that your JAVA_HOME points to Java 17.

### Gradle Plugins

The experiimental Declarative Gradle DSL plugin for Android is available on the Gradle Plugin Portal:
[`org.gradle.experimental.android-library`](https://plugins.gradle.org/plugin/org.gradle.experimental.android-library).
The location **will** change before the final release.

You can find a list of all available Declarative Gradle plugins [here](https://plugins.gradle.org/search?term=declarative-gradle).

### Android Studio

The latest [Android Studio 2024.1.2 Nightly](https://developer.android.com/studio/nightly)
supports code completion and syntax highlighting for Declarative Gradle files (*.dcl).
Some features work out of the box on this version,
others require additional configuration.

- Make sure the recent [Android Studio Nigthly](https://developer.android.com/studio/nightly) is installed.
- Set [`ANDROID_HOME`](https://developer.android.com/tools/variables#android_home) path to the location of the Android Studio Nigthly installation.
- Enable semantic assistance (completion and error highlighting) for Declarative Gradle
  1. Open _Help -> Open Custom Vm Options_
  2. Add text `-Didea.is.internal=true`
  3. Restart the IDE
  4. Open _Tools -> Internal Actions -> Android -> Edit Studio Flags_ and type _"Gradle Declarative"_ in the search window
  5. Switch on the _Studio support for declarative files_ flag.

## Getting Started with NowInAndroid

[Now in Android](https://github.com/android/nowinandroid) is a fully functional Android app built entirely with Kotlin and Jetpack Compose from the Android team.
To help you with getting started,
we forked _Now in Android_ and updated the build files using the Declarative Gradle DSL. The prototype repository can be found [here](https://github.com/gradle/nowinandroid/tree/main-declarative).

The settings file applies a new "Android ecosystem plugin", which exposes `androidLibrary` and `androidApplication` software types that can be used in subprojects.

The migration to Declarative Gradle is in proghress, not all subprojects have been migrated yet.
Some of the converted subprojects:
[`:core:common`](https://github.com/gradle/nowinandroid/blob/main-declarative/ccore/common/build.gradle.dcl),
[`:core:data`](https://github.com/gradle/nowinandroid/blob/main-declarative/ccore/data/build.gradle.dcl),
[`:core:domain`](https://github.com/gradle/nowinandroid/blob/main-declarative/ccore/domain/build.gradle.dcl).


Let’s take a look at a `build.gradle.dcl` file which replaces the common `build.gradle` or `build.gradle.kts` files:

```kotlin title="_core/common/build.gradle.dcl_"
androidLibrary {
    namespace = "com.google.samples.apps.nowinandroid.core.common"
    dependencies {
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
        testImplementation("app.cash.turbine:turbine:1.0.0")
    }
    buildTypes {
        buildTypes {
            // Need the empty closure to avoid "dangling pure expression" error
            debug {}
            release {}
        }
    }
}
```

The `androidLibrary` software type exposes [several configuration options](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/unified-plugin/plugin-android/src/main/java/org/gradle/api/experimental/android/library/AndroidLibrary.java) and dependencies. 

### Setup

**Step 1.** Setup the Android development environment as documented above

**Step 2.** Checkout the repositories

```shell
git clone https://github.com/gradle/nowinandroid.git
cd nowinandroid
git checkout main-declarative
git clone https://github.com/gradle/declarative-gradle.git
```

This should checkout the `main` branch of the [Declarative Gradle prototype plugins](https://github.com/gradle/declarative-gradle) inside the **root** of Gradle's NowInAndroid fork. 

You should have this project structure:
```shell
nowinandroid/
    declarative-gradle/
```

### Building

You can assemble the project with the following command:

```shell
./gradlew buildDemoDebug
```

### Testing

You can run tests using the following commands:

```shell
./gradlew testDemoDebug :lint:test
```

```shell
./gradlew testDemoDebugUnitTest -Proborazzi.test.verify=false
```

After starting a local Android emulator in Android Studio:

```shell
./gradlew connectedDemoDebugAndroidTest --daemon
```
