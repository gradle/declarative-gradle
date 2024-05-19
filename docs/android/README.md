
# Declarative Gradle for Android

## 1. Summary

Declarative Gradle DSL is currently in alpha.

The Declarative Gradle DSL code is available at https://github.com/gradle/declarative-gradle/tree/main/unified-prototype.
The Declarative Gradle DSL plugin for Android is available at: 
https://plugins.gradle.org/plugin/org.gradle.experimental.android-library.
You can find a list of all available plugins [here](https://plugins.gradle.org/search?term=declarative-gradle)

Common build files written using the Groovy DSL or Kotlin DSL with the name `build.gradle` and `build.gradle.kts` respectively will be replaced with `build.gradle.dcl`.

`.dcl` files are written in Kotlin in a fully declarative way. 

## 2. Key features / Syntax examples

The restricted DSL allows a limited set of constructs. Generic control flow and calls to arbitrary methods are not allowed.

For Android, the `androidLibrary` and `androidApplication` software types are used. For other applications, there are `jvmLibrary` and `kmpLibrary` types available as well. Therefore, your `build.gradle.dcl` files begin with:

```kotlin
androidLibrary {
}
```

The `androidLibrary` software type exposes [several configuration options](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/unified-plugin/plugin-android/src/main/java/org/gradle/api/experimental/android/library/AndroidLibrary.java) and dependencies. 

```kotlin
androidLibrary {
    namespace = ""
    dependencies {}
    buildTypes {}
}
```

As you can see, there is no imperative logic here.

The `androidApplication` software type can be found [here](https://github.com/gradle/declarative-gradle/blob/main/unified-prototype/unified-plugin/plugin-jvm/src/main/java/org/gradle/api/experimental/java/JavaApplication.java).

## 3. Getting Started with NowInAndroid

[Now in Android](https://github.com/android/nowinandroid) is a fully functional Android app built entirely with Kotlin and Jetpack Compose from the Android team.

We forked Now in Android and updated the build files using the Declarative Gradle DSL. The prototype repository can be found [here](https://github.com/gradle/nowinandroid/tree/main-declarative).

The [settings file](settings.gradle.dcl) applies a new "Android ecosystem plugin", which exposes `androidLibrary` and `androidApplication` software types that can be used in subprojects. 

The current prototype is limited to a single `androidLibrary` software type convention, so only a few subprojects have been converted.

Converted subprojects:
- [`:core:common`](core/common/build.gradle.dcl)
- [`:core:data`](core/data/build.gradle.dcl)
- [`:core:domain`](core/domain/build.gradle.dcl)

Let’s take a look at a `build.gradle.dcl` file which replaces the common `build.gradle` or `build.gradle.kts` files:

core/common/build.gradle.dcl
```kotlin
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

### 3.1 Prerequisites

- Make sure to use [JDK 17](https://www.oracle.com/fr/java/technologies/downloads/#java17) and that your JAVA_HOME points to Java 17.
- Make sure [Android Studio](https://developer.android.com/studio) is installed. Note that syntax highlighting works in [Android Studio nightlies](https://developer.android.com/studio/nightly) only. 
- Make sure that your [ANDROID_HOME](https://developer.android.com/tools/variables#android_home) path is set.

### 3.2 Setup

```shell
git clone https://github.com/gradle/nowinandroid.git
cd nowinandroid
git checkout main-declarative
git clone https://github.com/gradle/declarative-gradle.git
```

This should checkout the `main` branch of the [Declarative Gradle prototype plugins](https://github.com/gradle/declarative-gradle) inside the **root** of Gradle's NowInAndroid fork. 

You should have this project structure:
```
nowinandroid/
    declarative-gradle/
```

### 3.3 Building

You can assemble the project with the following command:

```shell
./gradlew buildDemoDebug
```

### 3.4 Testing

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
````
