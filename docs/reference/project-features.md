# Project Features and Types

Project features are capabilities that can be added to a project.  They are optional and can be made available in a build by applying a plugin in the declarative settings file that registers the project feature in the build.  Once a feature is available, it can be used in a build by referencing the feature in a project's declarative build file.  If no project references a feature, it will not be used in the build.

For example, to make the project features associated with producing jvm software available in a build, the `jvm-ecosystem` plugin is applied in the declarative settings file:

```kotlin
plugins {
    id("org.gradle.experimental.jvm-ecosystem")
}
```

However, this simply makes the project features from the JVM ecosystem available in the build.  To actually use a project feature, it must be referenced in a project's declarative build file (more about this later).

Project features have several distinct components:

### A Definition

This is the user-configurable object exposed in the declarative DSL.  Configuring the definition on the target is also the trigger for applying the feature's implementation.

### A Build Model

This is the data model that represents the low level configuration of the project feature.  The build model is populated based on the configuration of the project feature's definition.  Other features may interact with the build model to provide additional capabilities.

### A Target

The target is the object that the project feature is applied to.  The definition is added as a configurable property of the target.

### An Implementation

The implementation is the build logic necessary to implement the capability the project feature provides.  This is an action that modifies the project feature's target and/or model in some way.

## Project Types

The most fundamental type of project feature is a Project Type.  A Project Type is a collection of capabilities that are common to a particular type of software.  For example, an `androidLibrary` project type is a collection of capabilities that are common to Android libraries.  A `javaLibrary` project type is a collection of capabilities that are common to Java libraries.  Project types are used to define the type of software that a project produces and target the `Project` object of a project it is applied to.  They are applied by referencing the project type as a top-level block in the project's declarative build file.

For example, to declare that a project produces a java library built for Java 11, the `javaLibrary` project type is applied to the project:

```kotlin
javaLibrary {
    javaVersion = 11
}
```

A project can have only one project type applied to it.  A project type is at the root of the hierarchy of applied project features and always targets the top-level of the declarative build file.  It can logically be thought of as a project feature without a target.

Other types of project features may be applied further down the hierarchy in the declarative build file, targeting nested objects within the top-level project type.  For example, a `checkstyle` project feature may target the `javaLibrary` project type: 

```kotlin
javaLibrary {
    javaVersion = 11
    checkstyle {
        toolVersion = "10.12.3"
    }
}
```

## Shared Model Defaults

Shared Model Defaults are collections of settings that are applied to all projects in a build that use a given project type.  They are shared across all projects in a build.  These are declared by referencing the project type in the `defaults` block in the declarative settings file.

For example, to declare that all projects that produce Java libraries should produce a library built for Java 11, the `javaLibrary` project type is configured in `settings.gradle.dcl`:

```kotlin
defaults {
    javaLibrary {
        javaVersion = 11
    }
}
```
