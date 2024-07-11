# Software Features

Software features are capabilities that can be added to a project.  They are optional and can be made available in a build by applying a plugin in the declarative settings file that registers the software feature in the build.  Once a feature is available, it can be used in a build by referencing the feature in the project's declarative build file.  If a project does not reference a feature, it will not be used in the build.

For example, to make the software features associated with producing jvm software available in a build, the `jvm-ecosystem` plugin is applied in the declarative settings file:

```kotlin
plugins {
    id("org.gradle.experimental.jvm-ecosystem")
}
```

Software features have three distinct components:

### A Public Model

This is the user-configurable model exposed in the declarative DSL.  Configuring the model object on the target is also the trigger for applying the feature's implementation.

### A Target

The target is the object that the software feature is applied to.  The public model is added as a configurable property of the target.

### An Implementation

The implementation is the build logic necessary to implement the capability the software feature provides.  This is a plugin class that modifies the software feature's target and/or model in some way.

## Software Types

The most fundamental type of software feature is a Software Type.  A Software Type is a collection of capabilities that are common to a particular type of software.  For example, an `androidLibrary` software type is a collection of capabilities that are common to Android libraries.  A `javaLibrary` software type is a collection of capabilities that are common to Java libraries.  Software types are used to define the type of software that a project produces and target the `Project` object of a project it is applied to.  They are applied by referencing the software type as a top-level block in the project's declarative build file.

For example, to declare that a project produces a java library built for Java 11, the `javaLibrary` software type is applied to the project:

```kotlin
javaLibrary {
    javaVersion = 11
}
```

A project can have only one software type applied to it.

In the future, additional types of software features will be added to Declarative Gradle to provide more capabilities to projects.  For instance, the ability to add additional capabilities to a software type in a composable way, or the ability to restrict the model of a software type to a subset of its capabilities.

## Shared Model Defaults

Shared Model Defaults are collections of settings that are applied to all projects in a build that use a given software type.  They are shared across all projects in a build.  These are declared by referencing the software type in the `conventions` block in the declarative settings file.

For example, to declare that all projects that produce Java libraries should produce a library built for Java 11, the `javaLibrary` software type is configured in `settings.gradle.dcl`:

```kotlin
conventions {
    javaLibrary {
        javaVersion = 11
    }
}
```
