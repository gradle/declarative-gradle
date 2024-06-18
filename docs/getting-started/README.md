# Getting Started with Declarative Gradle

Declarative Gradle is in the experimental stage,
so it requires some extra steps to get started with.
It has limited testing and compatibility with IDEs,
so your mileage may vary.

## Installing Components

- You will need the latest [Gradle Build Tool nightly build](https://gradle.org/nightly/).
  The sample repositories include proper Gradle definitions,
  so for them, no extra steps are needed
- Make sure to use [JDK 17](https://www.oracle.com/fr/java/technologies/downloads/#java17) and
  that your `JAVA_HOME` points to Java 17.
- For specific project types and IDEs, for example Android projects, extra installation steps.
  See the guidelines on the respective documentation pages.

## Getting Started with Android development

[Support for Android](../android/README.md) is our main priority in the first alpha releases.
To help with getting started,
we updated the popular _Now in Android_ demo to use Declarative Gradle DSL and its DSL in the build definitions.
You can find the repository [here](https://github.com/gradle/nowinandroid/tree/main-declarative).

To get started, follow the steps in [this guide](../android/README.md#3-getting-started-with-nowinandroid).

## Getting started for other project types

At the moment, we do not have a detailed guide for other project types.
They are coming soon.
For now, you can check out the [Declarative Gradle prototypes](../../unified-prototypes/README.md).
They include samples and built-in documentation so that you can try them out.
It might be too early to adopt them in your projects due to the upcoming compatibility breaking changes,
but you're welcome to try Declarative Gradle in non-production projects and experimental branches.

- [Declarative Gradle for Java](../../unified-prototype/README.md#java)
- [Declarative Gradle for Kotlin JVM](../../unified-prototype/README.md#kotlin-jvm)
- [Declarative Gradle for Kotlin Multiplatform (KMP)](../../unified-prototype/README.md#kotlin-kmp)
- [Declarative Gradle for Swift](../../unified-prototype/README.md#swift)

## References

- [Support for Android](../android/README.md)
- [Other Declarative Gradle prototypes](../../unified-prototypes/README.md)
