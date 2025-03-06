# Generating new builds with `gradle init` 

Gradle supports generating new builds using [`gradle init`](https://docs.gradle.org/current/userguide/build_init_plugin.html). 

To make it easier to try Declarative Gradle with new samples, our prototypes have changed `gradle init` to generate samples that use declarative configuration language (DCL) files and software types.

An experimental system property has been added to Gradle to add new things that `gradle init` can generate. The value of the system property is a comma-separated list of plugins published to the [Gradle Plugin Portal](https://plugins.gradle.org/).

Example: `gradle init -Dorg.gradle.buildinit.specs=<plugin-id-1:version>,<plugin-id-2:version>`

!!! tip
    This feature is only supported with nightlies of Gradle 8.12 created after October 24, 2024. 

    If you have the correct version of Gradle, you'll be asked a new question:

    Additional project types were loaded.  Do you want to generate a project using a contributed project specification?

    Answer 'yes' (the default) to generate new Declarative Gradle builds.

# Supported project types

## Android 

`gradle init -Dorg.gradle.buildinit.specs=org.gradle.experimental.android-ecosystem-init:0.1.40`

There are four build samples you can choose from.

By default, this generates a project like [gradle/declarative-samples-android-app](https://github.com/gradle/declarative-samples-android-app) which is based on the prototype plugins. You can also generate a single Android application build with an empty Activity or basic Activity.

It can also generate a project like  [gradle/declarative-samples-agp-app](https://github.com/gradle/declarative-samples-agp-app) which is based on the Official Android Software Types Preview.

## Java 

`gradle init -Dorg.gradle.buildinit.specs=org.gradle.experimental.jvm-ecosystem-init:0.1.40`

This init sample asks no questions and generates a project using Java like [gradle/declarative-samples-java-app](https://github.com/gradle/declarative-samples-java-app).

## Kotlin 

`gradle init -Dorg.gradle.buildinit.specs=org.gradle.experimental.kmp-ecosystem-init:0.1.40`

This init sample asks no questions and generates a project using Kotlin like [gradle/declarative-samples-kotlin-app](https://github.com/gradle/declarative-samples-kotlin-app).
