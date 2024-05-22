---
title: Declarative Gradle for Java and JVM
description: >
  Supporting key Java use-cases is a priority for Declarative Gradle,
  along with support for Android and Kotlin projects.
---

Supporting key Java use-cases is a priority for Declarative Gradle,
along with support for Android and Kotlin projects.
Right now, we have prototypes for both Java application  and libraries.

> **DISCLAIMER:** This page is under development, more content will be added soon.
> For now, check out the prototypes referenced below.

## Example

```kotlin
javaApplication {
    javaVersion = 21
    mainClass = "com.example.App"

    dependencies {
        implementation(project(":java-util"))
        implementation("com.google.guava:guava:32.1.3-jre")
    }
}
```

## Prototypes

- [Java Application](../../unified-prototype/testbed-java-application/)
- [Java Library](../../unified-prototype/testbed-java-library/)
- [Java Application for multiple JVMs](../../unified-prototype/testbed-jvm-application/)
- [Java Library for multiple JVMs](../../unified-prototype/testbed-jvm-library/)

## Related resources

- [Declarative Gradle for Kotlin](../kotlin/README.md)
- [Declarative Gradle for Android](../android/README.md)
