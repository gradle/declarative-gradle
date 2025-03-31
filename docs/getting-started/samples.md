<!-- omit in toc -->
# Samples
!!! tip
    Make sure you followed the [setup](./setup.md) instructions fully before trying out a sample.

All samples in this page can be used to demonstrate features made possible by Declarative Gradle.

- [Generate a new build with `gradle init`](../reference/build-init.md)
- [Java Application](#java-application)
- [Kotlin Application](#kotlin-application)
- [Android Application](#android-application)
- [Gradle Client Application](#gradle-client-application)
- [Now In Android](#now-in-android)
- [Bleeding edge prototypes](#bleeding-edge-prototypes)

After picking a sample, read about new [Declarative Gradle features](./features.md).

[Provide us feedback](../feedback.md).

## Java Application

A sample Java application written in the Declarative Gradle DSL, using the prototype Declarative Gradle `javaApplication` Software Type defined in the `org.gradle.experimental.jvm-ecosystem` ecosystem plugin.

Please follow the README available at [gradle/declarative-samples-java-app](https://github.com/gradle/declarative-samples-java-app) to try this sample.

## Kotlin Application

A sample Kotlin application written in the Declarative Gradle DSL, using the prototype Declarative Gradle `kotlinJvmApplication` Software Type defined in the `org.gradle.experimental.kmp-ecosystem` ecosystem plugin.

Please follow the README available at [gradle/declarative-samples-kotlin-app](https://github.com/gradle/declarative-samples-kotlin-app) to try this sample.

## Android Application

### Using the Official Android Software Types (Preview)

A sample Android application written in the Declarative Gradle DSL, using the official Android Software Types Preview `androidApp` and `androidLibrary` defined in the `com.android.ecosystem` ecosystem plugin.

Please follow the README available at [gradle/declarative-samples-agp-app](https://github.com/gradle/declarative-samples-agp-app) to try this sample.


### Using Prototype Declarative Plugins

A sample Android application written in the Declarative Gradle DSL, using the prototype Declarative Gradle `androidApplication` Software Type defined in the `org.gradle.experimental.android-ecosystem` ecosystem plugin.

Please follow the README available at [gradle/declarative-samples-android-app](https://github.com/gradle/declarative-samples-android-app) to try this sample.

## Gradle Client Application

A visualization application aimed at demonstrating features in Declarative Gradle for which IDEA support is not yet fully developed.

Please follow the README located at [gradle/gradle-client](https://github.com/gradle/gradle-client) to try out this application.

## Now In Android

[Now in Android](https://github.com/android/nowinandroid) is a fully functional Android app built entirely with Kotlin and Jetpack Compose from the Android team.
We forked _Now in Android_ and updated the build to work with Declarative Gradle.
The prototype repository can be found in [gradle/nowinandroid](https://github.com/gradle/nowinandroid).

Please follow the [README](https://github.com/gradle/nowinandroid/blob/main-declarative/DECLARATIVE-README.md) to try this sample.

> **_NOTE:_** This test project is not being actively updated as Declarative Gradle evolves. 
> It was an experiment mainly done for validating the EAP 2 release and doesn't make use of newer features that would better model the project.

## Bleeding edge prototypes

If you want to go further you can take a look at some more projects for JVM, Android, KMP, Swift and C++ projects. [These projects](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype) may use newer versions of Gradle or require additional manual setup, so you should try the other samples first.

Please follow the README available in each directory linked below to try these samples.

<!-- omit in toc -->
### Java

- [Java Application](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-java-application/)
- [Java Library](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-java-library/)
- [Java Application for multiple JVMs](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-jvm-application/)
- [Java Library for multiple JVMs](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-jvm-library/)

<!-- omit in toc -->
### Kotlin

- [Kotlin JVM Application](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-kotlin-jvm-application/)
- [Kotlin JVM Library](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-kotlin-jvm-library/)
- [Kotlin Multiplatform Application](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-kotlin-application/)
- [Kotlin Multiplatform Library](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-kotlin-library/)

<!-- omit in toc -->
### Android

- [Android Application](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-android-application/)
- [Android Library](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-android-library/)

<!-- omit in toc -->
### Swift

- [Swift Application](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-swift-application/)
- [Swift Library](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-swift-library/)

<!-- omit in toc -->
### C++

- [C++ Application](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-cpp-application/)
- [C++ Library](https://github.com/gradle/declarative-gradle/tree/main/unified-prototype/testbed-cpp-library/)
