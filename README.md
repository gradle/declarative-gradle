# Declarative Gradle Prototypes

This repository contains prototypes and public examples
of the [Declarative Gradle](https://blog.gradle.org/declarative-gradle)
syntax and project definitions.

## Disclaimer

_Declarative Gradle_ is an experimental project.
At this point, no compatibility is guaranteed,
as well as there is no commitment to the DSL syntax
and available features.
More information will be released soon
Any feedback is welcome!

<!-- TODO: Add project manifesto -->

## Concept

Here is a very brief example of how top-level Declarative Gradle syntax may look like:

```kotlin
// Clear type of softwareß the project produces
javaLibrary {
    // All info about the library is grouped here

    // GroupID/ArtifactID/Version for publishing
    publishedAs("my-group:my-lib:2.0")

    // Common dependencies for all targets
    dependencies {
        api("some:lib:1.2")
        implementation(projects.someLib)
    }

    // A library might have more than one target
    targets {
        // All information about specific targets is grouped here
        java(11) {
            // Specific information about Java 11 target
            dependencies {
                implementation("some:back-port-lib:1.5")
            }
        }
        java(17) // No additional info for the Java 17 target
    }
}
```

## Read More

- [Initial Declarative Gradle Announcement and Full Manifesto](https://blog.gradle.org/declarative-gradle)

## Discuss

- `#declarative-gradle` channel on the community Slack
- Gradle Forums

See [Gradle Community Resources](https://gradle.org/resources/) for the links to the channels.
